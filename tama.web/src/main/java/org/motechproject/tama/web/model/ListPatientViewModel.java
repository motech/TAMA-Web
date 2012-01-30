package org.motechproject.tama.web.model;

import org.motechproject.tama.patient.domain.Patient;
import org.motechproject.tama.patient.domain.Status;

public class ListPatientViewModel extends Patient {

    private Patient patient;
    private boolean onTreatmentAdvice;

    public ListPatientViewModel(Patient patient) {
        this.setId(patient.getId());
        this.setDateOfBirth(patient.getDateOfBirth());
        this.setDateOfBirthAsDate(patient.getDateOfBirthAsDate());
        this.setPatientId(patient.getPatientId());
        this.setMobilePhoneNumber(patient.getMobilePhoneNumber());
        this.setGender(patient.getGender());
        this.setClinic(patient.getClinic());
        this.setRegistrationDate(patient.getRegistrationDate());
        this.setStatus(patient.getStatus());
    }

    public Patient getPatient() {
        return patient;
    }

    public boolean getOnTreatmentAdvice() {
        return onTreatmentAdvice;
    }

    public boolean isOnTreatmentAdvice() {
        return onTreatmentAdvice;
    }

    public void setOnTreatmentAdvice(boolean onTreatmentAdvice) {
        this.onTreatmentAdvice = onTreatmentAdvice;
    }

    public boolean isActivateEnabled() {
        return !getStatus().equals(Status.Active) && !isReviveEnabled();
    }

    public boolean isReviveEnabled() {
        return onTreatmentAdvice && (getStatus().equals(Status.Temporary_Deactivation) || getStatus().equals(Status.Suspended));
    }
}
