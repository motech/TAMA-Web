package org.motechproject.tama.patient.reporting;

import lombok.Data;
import org.motechproject.tama.patient.domain.Patient;
import org.motechproject.tama.refdata.domain.Gender;
import org.motechproject.tama.reports.contract.PatientRequest;

import java.util.Date;

@Data
public class BasicDetails {

    private String patientId;
    private String patientDocumentId;
    private Date dateOfBirth;
    private String gender;
    private String clinicId;
    private String travelTimeToClinic;
    private Date registeredOn;
    private String status;
    private String notes;
    private boolean complete;

    public BasicDetails(Patient patient) {
        patientId = patient.getPatientId();
        patientDocumentId = patient.getId();
        clinicId = patient.getClinic_id();
        dateOfBirth = patient.getDateOfBirthAsDate();
        gender = getGenderString(patient);
        registeredOn = patient.getRegistrationDateAsDate();
        status = patient.getStatus().getDisplayName();
        notes = patient.getNotes();
        complete = patient.isComplete();
        travelTimeToClinic = travelTimeToClinic(patient.getTravelTimeToClinicInDays(), patient.getTravelTimeToClinicInHours(), patient.getTravelTimeToClinicInMinutes());
    }

    public BasicDetails(PatientRequest patientRequest) {
        patientId = patientRequest.getPatientId();
        patientDocumentId = patientRequest.getPatientDocumentId();
        clinicId = patientRequest.getClinicId();
        dateOfBirth = patientRequest.getDateOfBirth();
        gender = patientRequest.getGender();
        notes = patientRequest.getNotes();
        complete = patientRequest.getComplete();
        travelTimeToClinic = patientRequest.getTravelTimeToClinic();
        registeredOn = patientRequest.getRegisteredOn();
        status = patientRequest.getStatus();
    }

    public void copyTo(PatientRequest request) {
        request.setPatientId(patientId);
        request.setPatientDocumentId(patientDocumentId);
        request.setDateOfBirth(dateOfBirth);
        request.setClinicId(clinicId);
        request.setComplete(complete);
        request.setGender(gender);
        request.setNotes(notes);
        request.setRegisteredOn(registeredOn);
        request.setStatus(status);
        request.setTravelTimeToClinic(travelTimeToClinic);
    }

    private String getGenderString(Patient patient) {
        Gender patientGender = patient.getGender();
        return (null == patientGender) ? "" : patientGender.getType();
    }

    private String travelTimeToClinic(int days, int hours, int minutes) {
        return String.format("%d days, %d hours, %d minutes", days, hours, minutes);
    }
}
