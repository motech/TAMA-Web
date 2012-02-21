package org.motechproject.tama.web.model;

import org.motechproject.tama.patient.domain.Patient;
import org.motechproject.tama.patient.domain.TreatmentAdvice;
import org.motechproject.tama.refdata.domain.Regimen;

import java.util.Date;

public class PatientReport {

    private Patient patient;
    private Regimen regimen;
    private TreatmentAdvice earliestTreatmentAdvice;

    public PatientReport(Patient patient, Regimen regimen, TreatmentAdvice earliestTreatmentAdvice) {
        this.patient = patient;
        this.regimen = regimen;
        this.earliestTreatmentAdvice = earliestTreatmentAdvice;
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

    public Date getARTStartDate() {
        return earliestTreatmentAdvice.getStartDate();
    }

    public String getRegimenName() {
        return regimen.getDisplayName();
    }
}
