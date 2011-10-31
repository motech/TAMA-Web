package org.motechproject.tama.service;

import org.motechproject.server.pillreminder.service.PillReminderService;
import org.motechproject.tama.TamaException;
import org.motechproject.tama.domain.*;
import org.motechproject.tama.mapper.MedicalConditionsMapper;
import org.motechproject.tama.platform.service.TamaSchedulerService;
import org.motechproject.tama.repository.*;
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

    @Autowired
    public PatientService(TamaSchedulerService tamaSchedulerService, PillReminderService pillReminderService, AllPatients allPatients, AllTreatmentAdvices allTreatmentAdvices, AllLabResults allLabResults, AllRegimens allRegimens, AllUniquePatientFields allUniquePatientFields, AllVitalStatistics allVitalStatistics) {
        this.allPatients = allPatients;
        this.allUniquePatientFields = allUniquePatientFields;
        this.tamaSchedulerService = tamaSchedulerService;
        this.allTreatmentAdvices = allTreatmentAdvices;
        this.pillReminderService = pillReminderService;
        this.allLabResults = allLabResults;
        this.allRegimens = allRegimens;
        this.allVitalStatistics = allVitalStatistics;
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
        allPatients.update(patient);
        postUpdate(patient, dbPatient);
    }

    public MedicalCondition getPatientMedicalConditions(String patientId) {
        Patient patient = allPatients.get(patientId);
        LabResults labResults = allLabResults.findByPatientId(patientId);
        VitalStatistics vitalStatistics = allVitalStatistics.findByPatientId(patientId);
        TreatmentAdvice treatmentAdvice = allTreatmentAdvices.findByPatientId(patientId);
        Regimen regimen = allRegimens.get(treatmentAdvice.getRegimenId());

        return new MedicalConditionsMapper(patient, labResults, vitalStatistics, treatmentAdvice, regimen).map();
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
        TreatmentAdvice treatmentAdvice = allTreatmentAdvices.findByPatientId(patient.getId());
        if (treatmentAdvice != null && patient.getPatientPreferences().getCallPreference() == CallPreference.FourDayRecall)
            tamaSchedulerService.scheduleJobsForFourDayRecall(patient, treatmentAdvice);
    }

    private void unscheduleDailyReminderJobs(Patient patient) {
        pillReminderService.unscheduleJobs(patient.getId());
        TreatmentAdvice treatmentAdvice = allTreatmentAdvices.findByPatientId(patient.getId());
        if (treatmentAdvice != null)
            tamaSchedulerService.unscheduleJobForAdherenceTrendFeedback(treatmentAdvice);
    }

    private boolean callPreferenceChangedFromDailyToFourDayRecall(Patient patient, Patient dbPatient) {
        return dbPatient.getPatientPreferences().getCallPreference().equals(CallPreference.DailyPillReminder) && patient.getPatientPreferences().getCallPreference().equals(CallPreference.FourDayRecall);
    }
}