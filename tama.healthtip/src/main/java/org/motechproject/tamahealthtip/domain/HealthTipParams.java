package org.motechproject.tamahealthtip.domain;

import org.motechproject.tama.ivr.decisiontree.domain.MedicalCondition;
import org.motechproject.tamadomain.domain.Patient;

public class HealthTipParams {

    private MedicalCondition medicalCondition;
    private Patient patient;
    private boolean dosageMissedLastWeek;

    public HealthTipParams(MedicalCondition medicalCondition, Patient patient, boolean dosageMissedLastWeek) {
        this.medicalCondition = medicalCondition;
        this.patient = patient;
        this.dosageMissedLastWeek = dosageMissedLastWeek;
    }

    public int numberOfMonthsSinceTreatmentStarted() {
        return medicalCondition.numberOfMonthsSinceTreatmentStarted();
    }

    public boolean isPatientOnDailyPillReminder() {
        return patient.isOnDailyPillReminder();
    }

    public boolean isDosageMissedLastWeek() {
        return dosageMissedLastWeek;
    }
}