package org.motechproject.tama.patient.domain;

import org.motechproject.tama.refdata.domain.Regimen;

import java.util.Date;

public class PatientReport {

    private Patient patient;
    private TreatmentAdvice earliestTreatmentAdvice;
    private TreatmentAdvice currentTreatmentAdvice;
    private Regimen currentRegimen;

    public PatientReport(Patient patient, TreatmentAdvice earliestTreatmentAdvice, TreatmentAdvice currentTreatmentAdvice, Regimen currentRegimen) {
        this.patient = patient;
        this.earliestTreatmentAdvice = earliestTreatmentAdvice;
        this.currentTreatmentAdvice = currentTreatmentAdvice;
        this.currentRegimen = currentRegimen;
    }

    public boolean getCanBeGenerated() {
        return earliestTreatmentAdvice != null;
    }

    public Patient getPatient() {
        return patient;
    }

    public String getPatientId() {
        return patient.getPatientId();
    }

    public String getPatientDocId() {
        return patient.getId();
    }

    public String getClinicName() {
        return patient.getClinic().getName();
    }

    public Date getARTStartedOn() {
        return earliestTreatmentAdvice.getStartDate();
    }

    public String getCurrentRegimenName() {
        return currentRegimen.getDisplayName();
    }

    public Date getCurrentRegimenStartDate() {
        return currentTreatmentAdvice.getStartDate();
    }
}
