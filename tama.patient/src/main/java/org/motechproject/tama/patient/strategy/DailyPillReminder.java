package org.motechproject.tama.patient.strategy;

import org.motechproject.tama.patient.domain.Patient;
import org.motechproject.tama.patient.domain.TreatmentAdvice;

public interface DailyPillReminder {

    public void enroll(Patient patient, TreatmentAdvice treatmentAdvice);

    public void reEnroll(Patient patient, TreatmentAdvice treatmentAdvice);
}