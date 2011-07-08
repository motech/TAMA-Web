package org.motechproject.tama.builder;

import java.util.Calendar;
import java.util.Date;

import org.motechproject.tama.domain.Clinic;
import org.motechproject.tama.domain.Patient;

public class PatientBuilder {

    private Patient patient = new Patient();

    public PatientBuilder withPatientId(String id){
        this.patient.setPatientId(id);
        return this;
    }

    public PatientBuilder withMobileNumber(String mobileNumber) {
        patient.setMobilePhoneNumber(mobileNumber);
        return this;
    }

    public PatientBuilder withDateOfBirth(Date dateOfBirth) {
        patient.setDateOfBirth(dateOfBirth);
        return this;
    }

    public PatientBuilder withPasscode(String passcode) {
        patient.setPasscode(passcode);
        return this;
    }

    public PatientBuilder withTravelTimeToClinicInDays(int days) {
        patient.setTravelTimeToClinicInDays(days);
        return this;
    }

    public PatientBuilder withTravelTimeToClinicInMins(int mins) {
        patient.setTravelTimeToClinicInMinutes(mins);
        return this;
    }

    public PatientBuilder withTravelTimeToClinicInHours(int hours) {
        patient.setTravelTimeToClinicInHours(hours);
        return this;
    }

    public PatientBuilder withId(String id) {
        patient.setId(id);
        return this;
    }

    public PatientBuilder withRevision(String revision) {
        patient.setRevision(revision);
        return this;
    }

    public PatientBuilder withStatus(Patient.Status status) {
        this.patient.setStatus(status);
        return this;
    }

    public Patient build() {
        return this.patient;
    }

    public static PatientBuilder startRecording() {
        return new PatientBuilder();
    }


    public PatientBuilder withClinic(Clinic clinic) {
        patient.setClinic(clinic);
        return this;
    }

    public PatientBuilder withDefaults(){

    	Calendar calendar = Calendar.getInstance();
    	calendar.set(1990, 5, 21);
    	Date dob = calendar.getTime();

        return this.withPatientId("1234").
                withDateOfBirth(dob).
                withMobileNumber("9765456789").
                withPasscode("123456").
                withClinic(Clinic.newClinic()).
                withTravelTimeToClinicInDays(1).
                withTravelTimeToClinicInHours(2).
                withTravelTimeToClinicInHours(3);
    }
}
