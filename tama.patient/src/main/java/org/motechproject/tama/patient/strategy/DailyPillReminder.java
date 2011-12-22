package org.motechproject.tama.patient.strategy;

import org.motechproject.tama.patient.domain.Patient;
import org.motechproject.tama.patient.domain.TreatmentAdvice;

public abstract class DailyPillReminder {

    public abstract void enroll(Patient patient, TreatmentAdvice treatmentAdvice);

    public abstract void reEnroll(Patient patient, TreatmentAdvice treatmentAdvice);
}