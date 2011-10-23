package org.motechproject.tama.service;

import org.motechproject.server.pillreminder.service.PillReminderService;
import org.motechproject.tama.domain.CallPreference;
import org.motechproject.tama.domain.Patient;
import org.motechproject.tama.domain.TreatmentAdvice;
import org.motechproject.tama.platform.service.TAMASchedulerService;
import org.motechproject.tama.repository.AllTreatmentAdvices;

public class DailyPillReminderCallService {

    private TAMASchedulerService tamaSchedulerService;
    private PillReminderService pillReminderService;
    private AllTreatmentAdvices allTreatmentAdvices;

    public DailyPillReminderCallService(TAMASchedulerService tamaSchedulerService, PillReminderService pillReminderService, AllTreatmentAdvices allTreatmentAdvices) {
        this.tamaSchedulerService = tamaSchedulerService;
        this.pillReminderService = pillReminderService;
        this.allTreatmentAdvices = allTreatmentAdvices;
    }

    public void patientEnrolled(Patient patient) {
        if (patient.getPatientPreferences().hasAgreedToBeCalledAtBestCallTime()) {
            tamaSchedulerService.scheduleJobForOutboxCall(patient);
        }
    }

    public void patientQuit(Patient patient) {
        tamaSchedulerService.unscheduleJobForOutboxCall(patient);
        tamaSchedulerService.unscheduleRepeatingJobForOutboxCall(patient.getId());
        unscheduleDailyReminderJobs(patient);
    }

    public void patientUpdated(Patient oldPatient, Patient updatedPatient) {
        if (bestCallTimeChanged(updatedPatient, oldPatient)) {
            rescheduleOutboxCalls(updatedPatient, oldPatient);
        }
    }

    private void unscheduleDailyReminderJobs(Patient patient) {
        pillReminderService.unscheduleJobs(patient.getId());
        TreatmentAdvice treatmentAdvice = allTreatmentAdvices.findByPatientId(patient.getId());
        if (treatmentAdvice != null)
            tamaSchedulerService.unscheduleJobForAdherenceTrendFeedback(treatmentAdvice);
    }

    private boolean bestCallTimeChanged(Patient patient, Patient dbPatient) {
        return !(patient.getPatientPreferences().getBestCallTime().equals(dbPatient.getPatientPreferences().getBestCallTime()));
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

}
