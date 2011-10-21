package org.motechproject.tama.service;

import org.motechproject.server.pillreminder.service.PillReminderService;
import org.motechproject.tama.TamaException;
import org.motechproject.tama.domain.*;
import org.motechproject.tama.platform.service.TamaSchedulerService;
import org.motechproject.tama.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Component
public class PatientService {

    private AllPatients allPatients;
    private AllUniquePatientFields allUniquePatientFields;
    private PillReminderService pillReminderService;
    private AllTreatmentAdvices allTreatmentAdvices;
    private TamaSchedulerService tamaSchedulerService;
    private AllLabResults allLabResults;
    private AllRegimens allRegimens;

    @Autowired
    public PatientService(TamaSchedulerService tamaSchedulerService, PillReminderService pillReminderService, AllPatients allPatients, AllTreatmentAdvices allTreatmentAdvices, AllLabResults allLabResults, AllRegimens allRegimens, AllUniquePatientFields allUniquePatientFields) {
        this.allPatients = allPatients;
        this.allUniquePatientFields = allUniquePatientFields;
        this.tamaSchedulerService = tamaSchedulerService;
        this.allTreatmentAdvices = allTreatmentAdvices;
        this.pillReminderService = pillReminderService;
        this.allLabResults = allLabResults;
        this.allRegimens = allRegimens;
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

    public Patient getPatient(String patientId) {
        return allPatients.findByPatientId(patientId).get(0);
    }

    public List<LabResult> getLabResults(String patientId) {
        return allLabResults.findByPatientId(patientId);
    }

    public TreatmentAdvice getTreatmentAdvice(String patientId) {
        return allTreatmentAdvices.findByPatientId(patientId);
    }

    public Regimen getRegimen(String regimenId) {
        return allRegimens.get(regimenId);
    }

    public PatientMedicalConditions getPatientMedicalConditions(String patientId) {
        Patient patient = getPatient(patientId);
        List<LabResult> labResults = getLabResults(patientId);
        TreatmentAdvice treatmentAdvice = getTreatmentAdvice(patientId);
        Regimen regimen = getRegimen(treatmentAdvice.getRegimenId());

        PatientMedicalConditions patientMedicalConditions = new PatientMedicalConditions();
        patientMedicalConditions.setRegimenName(regimen.getName());
        patientMedicalConditions.setGender(patient.getGender().getType());
        patientMedicalConditions.setAge(patient.getAge());

        Collections.sort(labResults, new LabResult.LabResultComparator());

        for (LabResult result : labResults) {
            if (result.isCD4()) {
                patientMedicalConditions.setCd4Count(Integer.parseInt(result.getResult()));
                break;
            }
        }

        return patientMedicalConditions;
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