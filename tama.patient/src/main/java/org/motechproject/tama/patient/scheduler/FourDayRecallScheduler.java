package org.motechproject.tama.patient.scheduler;

import org.motechproject.tama.patient.domain.Patient;
import org.motechproject.tama.patient.domain.TreatmentAdvice;

public abstract class FourDayRecallScheduler {

    public abstract void scheduleFourDayRecallJobs(Patient patient, TreatmentAdvice treatmentAdvice);

    public abstract void unscheduleFourDayRecallJobs(Patient patient);
}