package org.motechproject.tama.web.model;

import org.motechproject.tama.patient.domain.Patient;
import org.motechproject.tama.patient.domain.Status;
import org.motechproject.tama.patient.domain.TreatmentAdvice;

import java.util.Date;

public class PatientSummary {

    private Patient patient;
    private TreatmentAdvice treatmentAdvice;
    private Date artStartDate;
    private String regimenName;
    private String warning;

    public PatientSummary(Patient patient, TreatmentAdvice treatmentAdvice, Date artStartDate, String regimenName, String warning) {
        this.patient = patient;
        this.treatmentAdvice = treatmentAdvice;
        this.artStartDate = artStartDate;
        this.regimenName = regimenName;
        this.warning = warning;
    }

    public String getPatientId() {
        return patient.getPatientId();
    }

    public String getMobilePhoneNumber() {
        return patient.getMobilePhoneNumber();
    }

    public String getGender() {
        return patient.getGender().getType();
    }

    public Date getDateOfBirth() {
        return patient.getDateOfBirthAsDate();
    }

    public Date getRegistrationDate() {
        return patient.getRegistrationDateAsDate();
    }

    public Date getArtStartDate() {
        return artStartDate;
    }

    public Date getCurrentRegimenStartDate() {
        return treatmentAdvice == null ? null : treatmentAdvice.getStartDate();
    }

    public String getCurrentARTRegimen() {
        return regimenName;
    }

    public String getCallPlan() {
        return patient.getPatientPreferences().getDisplayCallPreference();
    }

    public String getWarning() {
        return warning;
    }

    public String getId(){
        return patient.getId();
    }

    public Status getStatus(){
        return patient.getStatus();
    }
}