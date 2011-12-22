package org.motechproject.tama.patient.scheduler;

import org.motechproject.tama.patient.domain.Patient;
import org.motechproject.tama.patient.domain.TreatmentAdvice;

public abstract class DailyPillReminderScheduler {

    public abstract void scheduleDailyPillReminderJobs(Patient patient, TreatmentAdvice treatmentAdvice);

    public abstract void unscheduleDailyPillReminderJobs(Patient patient);
}