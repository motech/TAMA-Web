package org.motechproject.tama.reporting.mapper;

import lombok.Data;
import org.motechproject.tama.patient.domain.Patient;
import org.motechproject.tama.reports.contract.PatientRequest;

import java.util.Date;

@Data
public class BasicDetails {

    private String patientId;
    private Date dateOfBirth;
    private String gender;
    private String clinic;
    private String travelTimeToClinic;

    public BasicDetails(Patient patient) {
        patientId = patient.getPatientId();
        clinic = patient.getClinic().getName();
        dateOfBirth = patient.getDateOfBirthAsDate();
        gender = patient.getGender().getType();
        travelTimeToClinic = travelTimeToClinic(patient.getTravelTimeToClinicInDays(), patient.getTravelTimeToClinicInHours(), patient.getTravelTimeToClinicInMinutes());
    }

    public BasicDetails(PatientRequest patientRequest) {
        patientId = patientRequest.getPatientId();
        clinic = patientRequest.getClinic();
        dateOfBirth = patientRequest.getDateOfBirth();
        gender = patientRequest.getGender();
        travelTimeToClinic = patientRequest.getTravelTimeToClinic();
    }

    public void copyTo(PatientRequest request) {
        request.setPatientId(patientId);
        request.setDateOfBirth(dateOfBirth);
        request.setClinic(clinic);
        request.setGender(gender);
        request.setTravelTimeToClinic(travelTimeToClinic);
    }

    private String travelTimeToClinic(int days, int hours, int minutes) {
        return String.format("%d days, %d hours, %d minutes", days, hours, minutes);
    }
}
