package org.motechproject.tama.outbox.service;

import org.motechproject.tama.patient.domain.Patient;
import org.motechproject.tama.patient.service.PatientService;
import org.motechproject.tama.patient.strategy.Outbox;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class OutboxService extends Outbox {

    private OutboxSchedulerService outboxSchedulerService;

    @Autowired
    public OutboxService(OutboxSchedulerService outboxSchedulerService, PatientService patientService) {
        this.outboxSchedulerService = outboxSchedulerService;
        patientService.registerOutbox(this);
    }

    @Override
    public void enroll(Patient patient) {
        outboxSchedulerService.scheduleOutboxJobs(patient);
    }

    @Override
    public void reEnroll(Patient patient) {
        outboxSchedulerService.rescheduleOutboxJobs(patient);
    }
}