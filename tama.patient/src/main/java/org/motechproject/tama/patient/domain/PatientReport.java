package org.motechproject.tama.patient.domain;

import lombok.EqualsAndHashCode;
import org.motechproject.tama.refdata.domain.Regimen;

import java.util.Date;

@EqualsAndHashCode
public class PatientReport {

    private Patient patient;
    private TreatmentAdvice earliestTreatmentAdvice;
    private TreatmentAdvice currentTreatmentAdvice;
    private Regimen currentRegimen;

    private PatientReport() {
        patient = null;
        earliestTreatmentAdvice = null;
        currentTreatmentAdvice = null;
        currentRegimen = null;
    }

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
        return (null == patient) ? "" : patient.getPatientId();
    }

    public String getPatientDocId() {
        return (null == patient) ? "" : patient.getId();
    }

    public String getClinicName() {
        return (null == patient) ? "" : patient.getClinic().getName();
    }

    public Date getARTStartedOn() {
        return earliestTreatmentAdvice != null ? earliestTreatmentAdvice.getStartDate() : null;
    }

    public String getCurrentRegimenName() {
        return currentRegimen != null ? currentRegimen.getDisplayName() : null;
    }

    public Date getCurrentRegimenStartDate() {
        return currentTreatmentAdvice != null ? currentTreatmentAdvice.getStartDate() : null;
    }

    public static PatientReport nullPatientReport() {
        return new PatientReport();
    }
}
