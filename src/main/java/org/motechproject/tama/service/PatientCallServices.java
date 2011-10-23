package org.motechproject.tama.service;

import org.motechproject.server.pillreminder.service.PillReminderService;
import org.motechproject.tama.domain.CallPreference;
import org.motechproject.tama.domain.Patient;
import org.motechproject.tama.platform.service.TAMASchedulerService;
import org.motechproject.tama.repository.AllTreatmentAdvices;

public class PatientCallServices {

    private DailyPillReminderCallService dailyPillReminderCallService;

    private FourdayRecallCallService fourdayRecallCallService;

    public PatientCallServices(TAMASchedulerService tamaSchedulerService, PillReminderService pillReminderService, AllTreatmentAdvices allTreatmentAdvices) {
        this.dailyPillReminderCallService = new DailyPillReminderCallService(tamaSchedulerService, pillReminderService, allTreatmentAdvices);
        this.fourdayRecallCallService = new FourdayRecallCallService(tamaSchedulerService, allTreatmentAdvices);
    }

    public void patientCreated(Patient patient) {
        if (patient.getPatientPreferences().getCallPreference().equals(CallPreference.DailyPillReminder)) {
            dailyPillReminderCallService.patientEnrolled(patient);
        }
    }

    public void patientUpdated(Patient oldPatient, Patient updatedPatient) {
        if (callPreferenceChangedFromDailyToFourDayRecall(updatedPatient, oldPatient)) {
            dailyPillReminderCallService.patientQuit(oldPatient);
            fourdayRecallCallService.patientEnrolled(updatedPatient);
        } else if (updatedPatient.getPatientPreferences().getCallPreference().equals(CallPreference.DailyPillReminder)) {
            dailyPillReminderCallService.patientUpdated(oldPatient, updatedPatient);
        }else {
            fourdayRecallCallService.patientUpdated(oldPatient, updatedPatient);
        }
    }

    private boolean callPreferenceChangedFromDailyToFourDayRecall(Patient patient, Patient dbPatient) {
        return dbPatient.getPatientPreferences().getCallPreference().equals(CallPreference.DailyPillReminder) && patient.getPatientPreferences().getCallPreference().equals(CallPreference.FourDayRecall);
    }
}
