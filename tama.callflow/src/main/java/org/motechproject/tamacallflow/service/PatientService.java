package org.motechproject.tamacallflow.service;

import org.joda.time.DateTime;
import org.motechproject.server.pillreminder.service.PillReminderService;
import org.motechproject.tamacommon.TamaException;
import org.motechproject.tamadomain.domain.*;
import org.motechproject.tamadomain.repository.*;
import org.motechproject.tama.ivr.decisiontree.domain.MedicalCondition;
import org.motechproject.tamacallflow.mapper.MedicalConditionsMapper;
import org.motechproject.tamacallflow.platform.service.TamaSchedulerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class PatientService {

    private AllPatients allPatients;
    private AllVitalStatistics allVitalStatistics;
    private AllUniquePatientFields allUniquePatientFields;
    private PillReminderService pillReminderService;
    private AllTreatmentAdvices allTreatmentAdvices;
    private TamaSchedulerService tamaSchedulerService;
    private AllLabResults allLabResults;
    private AllRegimens allRegimens;
    private WeeklyAdherenceService weeklyAdherenceService;
    private DosageAdherenceService dosageAdherenceService;

    @Autowired
    public PatientService(TamaSchedulerService tamaSchedulerService, PillReminderService pillReminderService, AllPatients allPatients,
                          AllTreatmentAdvices allTreatmentAdvices, AllLabResults allLabResults, AllRegimens allRegimens,
                          AllUniquePatientFields allUniquePatientFields, AllVitalStatistics allVitalStatistics, WeeklyAdherenceService weeklyAdherenceService,
                          DosageAdherenceService dosageAdherenceService) {

        this.allPatients = allPatients;
        this.allUniquePatientFields = allUniquePatientFields;
        this.tamaSchedulerService = tamaSchedulerService;
        this.allTreatmentAdvices = allTreatmentAdvices;
        this.pillReminderService = pillReminderService;
        this.allLabResults = allLabResults;
        this.allRegimens = allRegimens;
        this.allVitalStatistics = allVitalStatistics;
        this.weeklyAdherenceService = weeklyAdherenceService;
        this.dosageAdherenceService = dosageAdherenceService;
    }

    public void update(Patient patient) {
        Patient dbPatient = allPatients.get(patient.getId());
        patient.setRevision(dbPatient.getRevision());

        List<UniquePatientField> oldUniquePatientFields = allUniquePatientFields.get(patient);
        allUniquePatientFields.remove(patient);
        try {
            allUniquePatientFields.add(patient);
        } catch (TamaException e) {
            for (UniquePatientField uniquePatientField : oldUniquePatientFields) {
                allUniquePatientFields.add(new UniquePatientField(uniquePatientField.getId(), uniquePatientField.getPrimaryDocId()));
            }
            throw e;
        }

        if (callPreferenceChangedFromDailyToFourDayRecall(patient, dbPatient)) {
            patient.getPatientPreferences().setCallPreferenceTransitionDate(DateTime.now());
        }

        allPatients.update(patient);
        postUpdate(patient, dbPatient);
    }

    public MedicalCondition getPatientMedicalConditions(String patientId) {
        Patient patient = allPatients.get(patientId);
        LabResults labResults = allLabResults.findByPatientId(patientId);
        VitalStatistics vitalStatistics = allVitalStatistics.findByPatientId(patientId);
        TreatmentAdvice earliestTreatmentAdvice = allTreatmentAdvices.earliestTreatmentAdvice(patientId);
        TreatmentAdvice currentTreatmentAdvice = allTreatmentAdvices.currentTreatmentAdvice(patientId);
        Regimen currentRegimen = allRegimens.get(currentTreatmentAdvice.getRegimenId());

        return new MedicalConditionsMapper(patient, labResults, vitalStatistics, earliestTreatmentAdvice, currentRegimen).map();
    }

    private void postUpdate(Patient patient, Patient dbPatient) {
        if (callPreferenceChangedFromDailyToFourDayRecall(patient, dbPatient)) {
            tamaSchedulerService.unscheduleJobForOutboxCall(dbPatient);
            tamaSchedulerService.unscheduleRepeatingJobForOutboxCall(dbPatient.getId());
            unscheduleDailyReminderJobs(patient);
            scheduleFourDayRecallJobs(patient);
            return;
        }

        if (bestCallTimeChanged(patient, dbPatient)) {
            rescheduleOutboxCalls(patient, dbPatient);
        }

        if (bestCallTimeChanged(patient, dbPatient) || dayOfWeekForWeeklyAdherenceCallChanged(patient, dbPatient)) {
            rescheduleFourDayRecallJobs(patient);
        }
    }

    private void rescheduleOutboxCalls(Patient updatedPatient, Patient patient) {
        boolean shouldUnScheduleOldOutboxJobs = shouldScheduleOutboxCallsFor(patient);
        if (shouldUnScheduleOldOutboxJobs) {
            tamaSchedulerService.unscheduleJobForOutboxCall(patient);
            tamaSchedulerService.unscheduleRepeatingJobForOutboxCall(patient.getId());
        }
        if (shouldScheduleOutboxCallsFor(updatedPatient)) {
            tamaSchedulerService.scheduleJobForOutboxCall(updatedPatient);
        }
    }

    boolean shouldScheduleOutboxCallsFor(Patient patient) {
        return patient.getPatientPreferences().getCallPreference() == CallPreference.DailyPillReminder && patient.getPatientPreferences().hasAgreedToBeCalledAtBestCallTime();
    }

    private void rescheduleFourDayRecallJobs(Patient patient) {
        tamaSchedulerService.unScheduleFourDayRecallJobs(patient);
        scheduleFourDayRecallJobs(patient);
    }

    private boolean dayOfWeekForWeeklyAdherenceCallChanged(Patient patient, Patient dbPatient) {
        return patient.getPatientPreferences().getDayOfWeeklyCall() != dbPatient.getPatientPreferences().getDayOfWeeklyCall();
    }

    private boolean bestCallTimeChanged(Patient patient, Patient dbPatient) {
        return !(patient.getPatientPreferences().getBestCallTime().equals(dbPatient.getPatientPreferences().getBestCallTime()));
    }

    private void scheduleFourDayRecallJobs(Patient patient) {
        TreatmentAdvice treatmentAdvice = allTreatmentAdvices.currentTreatmentAdvice(patient.getId());
        if (treatmentAdvice != null && patient.getPatientPreferences().getCallPreference() == CallPreference.FourDayRecall)
            tamaSchedulerService.scheduleJobsForFourDayRecall(patient, treatmentAdvice);
    }

    private void unscheduleDailyReminderJobs(Patient patient) {
        TreatmentAdvice treatmentAdvice = allTreatmentAdvices.currentTreatmentAdvice(patient.getId());
        if (treatmentAdvice != null) {
            pillReminderService.unscheduleJobs(patient.getId());
            tamaSchedulerService.unscheduleJobForAdherenceTrendFeedback(treatmentAdvice);
        }
    }

    private boolean callPreferenceChangedFromDailyToFourDayRecall(Patient patient, Patient dbPatient) {
        return dbPatient.getPatientPreferences().getCallPreference().equals(CallPreference.DailyPillReminder) && patient.getPatientPreferences().getCallPreference().equals(CallPreference.FourDayRecall);
    }

    public void reActivate(String patientId, SuspendedAdherenceData suspendedAdherenceData) {
        allPatients.activate(patientId);
        Patient patient = allPatients.get(patientId);
        suspendedAdherenceData.suspendedFrom(patient.getLastSuspendedDate());
        if(patient.getPatientPreferences().getCallPreference() == CallPreference.FourDayRecall){
            weeklyAdherenceService.recordAdherence(suspendedAdherenceData);
        }else{
            dosageAdherenceService.recordAdherence(suspendedAdherenceData);
        }
    }
}






