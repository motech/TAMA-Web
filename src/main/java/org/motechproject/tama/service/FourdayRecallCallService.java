package org.motechproject.tama.service;

import org.motechproject.tama.domain.Patient;
import org.motechproject.tama.domain.TreatmentAdvice;
import org.motechproject.tama.platform.service.TAMASchedulerService;
import org.motechproject.tama.repository.AllTreatmentAdvices;

public class FourdayRecallCallService {

    private TAMASchedulerService tamaSchedulerService;
    private AllTreatmentAdvices allTreatmentAdvices;

    public FourdayRecallCallService(TAMASchedulerService tamaSchedulerService, AllTreatmentAdvices allTreatmentAdvices) {
        this.tamaSchedulerService = tamaSchedulerService;
        this.allTreatmentAdvices = allTreatmentAdvices;
    }

    public void patientEnrolled(Patient patient) {
        scheduleFourDayRecallJobs(patient);
    }

    public void patientUpdated(Patient oldPatient, Patient updatedPatient) {
        if (bestCallTimeChanged(updatedPatient, oldPatient) || dayOfWeekForWeeklyAdherenceCallChanged(updatedPatient, oldPatient)) {
            rescheduleFourDayRecallJobs(updatedPatient);
        }
    }

    private void rescheduleFourDayRecallJobs(Patient patient) {
        tamaSchedulerService.unScheduleFourDayRecallJobs(patient);
        scheduleFourDayRecallJobs(patient);
    }

    private void scheduleFourDayRecallJobs(Patient patient) {
        TreatmentAdvice treatmentAdvice = allTreatmentAdvices.findByPatientId(patient.getId());
        if (treatmentAdvice != null)
            tamaSchedulerService.scheduleJobsForFourDayRecall(patient, treatmentAdvice);
    }

    private boolean dayOfWeekForWeeklyAdherenceCallChanged(Patient patient, Patient dbPatient) {
        return patient.getPatientPreferences().getDayOfWeeklyCall() != dbPatient.getPatientPreferences().getDayOfWeeklyCall();
    }

    private boolean bestCallTimeChanged(Patient patient, Patient dbPatient) {
        return !(patient.getPatientPreferences().getBestCallTime().equals(dbPatient.getPatientPreferences().getBestCallTime()));
    }
}
