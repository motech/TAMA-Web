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
        outboxSchedulerService.scheduleOutboxJobs(patient);
    }

    public void reEnroll(Patient patient) {
        outboxSchedulerService.rescheduleOutboxJobs(patient);
    }
}