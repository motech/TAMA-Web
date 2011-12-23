package org.motechproject.tama.outbox.service;

import org.motechproject.tama.patient.domain.Patient;
import org.motechproject.tama.patient.service.PatientService;
import org.motechproject.tama.patient.strategy.Outbox;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OutboxService implements Outbox {

    private OutboxSchedulerService outboxSchedulerService;

    @Autowired
    public OutboxService(OutboxSchedulerService outboxSchedulerService, PatientService patientService) {
        this.outboxSchedulerService = outboxSchedulerService;
        patientService.registerOutbox(this);
    }

    public void enroll(Patient patient) {
        if (patient.hasAgreedToBeCalledAtBestCallTime()) {
            outboxSchedulerService.scheduleOutboxJobs(patient);
        }
    }

    void disEnroll(Patient dbPatient) {
        if (dbPatient.hasAgreedToBeCalledAtBestCallTime()) {
            outboxSchedulerService.unscheduleOutboxJobs(dbPatient);
        }
    }

    public void reEnroll(Patient dbPatient, Patient patient) {
        boolean bestCallTimeChanged = dbPatient.getPatientPreferences().getBestCallTime() != patient.getPatientPreferences().getBestCallTime();
        boolean dayOfWeeklyCallChanged = dbPatient.getPatientPreferences().getDayOfWeeklyCall() != patient.getPatientPreferences().getDayOfWeeklyCall();
        if (bestCallTimeChanged || dayOfWeeklyCallChanged) {
            disEnroll(dbPatient);
            enroll(patient);
        }
    }
}