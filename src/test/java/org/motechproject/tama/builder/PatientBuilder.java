package org.motechproject.tama.builder;

import org.joda.time.LocalDate;
import org.motechproject.tama.domain.*;
import org.motechproject.util.DateUtil;

public class PatientBuilder {

    private Patient patient = new Patient();

    public PatientBuilder withPatientId(String id) {
        this.patient.setPatientId(id);
        return this;
    }

    public PatientBuilder withMobileNumber(String mobileNumber) {
        patient.setMobilePhoneNumber(mobileNumber);
        return this;
    }

    public PatientBuilder withDateOfBirth(LocalDate dateOfBirth) {
        patient.setDateOfBirth(dateOfBirth);
        return this;
    }

    public PatientBuilder withGender(Gender gender) {
        patient.setGender(gender);
        return this;
    }

    public PatientBuilder withIVRLanguage(IVRLanguage language) {
        patient.setIvrLanguage(language);
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

    public PatientBuilder withHIVTestReason(HIVTestReason hivTestReason) {
        patient.getMedicalHistory().getHivMedicalHistory().setTestReason(hivTestReason);
        return this;
    }

    public PatientBuilder withModeOfTransmission(ModeOfTransmission modeOfTransmission) {
        patient.getMedicalHistory().getHivMedicalHistory().setModeOfTransmission(modeOfTransmission);
        return this;
    }

    public PatientBuilder withMedicalHistory(MedicalHistory medicalHistory) {
        patient.setMedicalHistory(medicalHistory);
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
        patient.setClinic_id(clinic.getId());
        return this;
    }

    public PatientBuilder withDefaults() {

        return this.withPatientId("1234_" + DateUtil.now().getMillis()).
                withDateOfBirth(DateUtil.newDate(1990, 5, 21)).
                withMobileNumber("9765456789").
                withPasscode("123456").
                withClinic(Clinic.newClinic()).
                withTravelTimeToClinicInDays(1).
                withTravelTimeToClinicInHours(2).
                withTravelTimeToClinicInHours(3).
                withMedicalHistory(MedicalHistoryBuilder.startRecording().withDefaults().build());
    }
}
