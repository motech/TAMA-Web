package org.motechproject.tama.web.model;

import org.motechproject.tama.patient.domain.Patient;
import org.motechproject.tama.patient.domain.Status;

public class PatientViewModel extends Patient {

    public PatientViewModel(Patient patient) {
        this.setId(patient.getId());
        this.setDateOfBirth(patient.getDateOfBirth());
        this.setDateOfBirthAsDate(patient.getDateOfBirthAsDate());
        this.setPatientId(patient.getPatientId());
        this.setMobilePhoneNumber(patient.getMobilePhoneNumber());
        this.setGender(patient.getGender());
        this.setClinic(patient.getClinic());
        this.setRegistrationDate(patient.getRegistrationDate());
        this.setStatus(patient.getStatus());
        this.setActivationDate(patient.getActivationDate());

        this.setClinic_id(patient.getClinic_id());
        this.setGenderId(patient.getGenderId());
        this.setLastDeactivationDate(patient.getLastDeactivationDate());
        this.setMedicalHistory(patient.getMedicalHistory());
        this.setNotes(patient.getNotes());
        this.setLastSuspendedDate(patient.getLastSuspendedDate());
        this.setPatientPreferences(patient.getPatientPreferences());
        this.setTravelTimeToClinicInDays(patient.getTravelTimeToClinicInDays());
        this.setTravelTimeToClinicInHours(patient.getTravelTimeToClinicInHours());
        this.setTravelTimeToClinicInMinutes(patient.getTravelTimeToClinicInMinutes());
        this.setDocumentType(patient.getDocumentType());
        this.setRevision(patient.getRevision());
    }

    public Patient getPatient() {
        return this;
    }

    private boolean isActivateEnabled() {
        return !getStatus().equals(Status.Active) && !isReviveEnabled();
    }

    private boolean isReviveEnabled() {
        return (getStatus().equals(Status.Temporary_Deactivation) || getStatus().equals(Status.Suspended));
    }

    private boolean isDeactivateEnabled() {
        return getStatus().equals(Status.Active);
    }

    public String getPatientSummaryLink() {
        return "patients/summary/"+getId();
    }

    public String getStatusAction(){
        if(isActivateEnabled())
            return "Activate";
        else if (isReviveEnabled())
            return "Reactivate";
        else if(isDeactivateEnabled())
            return "Deactivate";
        else
            return null;
    }

    public String getStatusActionUrl(){
        if(isActivateEnabled())
            return "/patients/activate";
        else if (isReviveEnabled())
            return "/patients/revive";
        else if (isDeactivateEnabled())
            return "/patients/deactivate";
        else
            return null;
    }
}
