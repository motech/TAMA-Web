package org.motechproject.tama.patient.scheduler;

import org.motechproject.tama.patient.domain.Patient;

public abstract class OutboxScheduler {

    public abstract void scheduleOutboxJobs(Patient patient);

    public abstract void unscheduleOutboxJobs(Patient patient);
}