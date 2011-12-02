package org.motechproject.tamahealthtip.domain;

import org.motechproject.tama.ivr.decisiontree.domain.MedicalCondition;
import org.motechproject.tamadomain.domain.Patient;

public class HealthTipParams {

    private MedicalCondition medicalCondition;
    private Patient patient;

    public HealthTipParams(MedicalCondition medicalCondition, Patient patient) {
        this.medicalCondition = medicalCondition;
        this.patient = patient;
    }

    public int numberOfMonthsSinceTreatmentStarted() {
        return medicalCondition.numberOfMonthsSinceTreatmentStarted();
    }

    public boolean isPatientOnDailyPillReminder() {
        return patient.isOnDailyPillReminder();
    }
}