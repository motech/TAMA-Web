package org.motechproject.tamafunctional.testdata;

import org.joda.time.LocalDate;
import org.motechproject.util.DateUtil;

public class TestPatient extends TestEntity {
    private String patientId;
    private LocalDate dateOfBirth;
    private String mobileNumber;
    private String passcode;
    private TestClinic clinic;
    private String travelTimeToClinicInDays;
    private String travelTimeToClinicInHours;
    private String travelTimeToClinicInMinutes;
    private TestHIVMedicalHistory hivMedicalHistory;

    private TestPatient() {
    }

    public static TestPatient withMandatory(TestClinic clinic) {
        TestPatient testPatient = new TestPatient();
        return testPatient.patientId(unique("1234_")).
                dateOfBirth(DateUtil.newDate(1990, 5, 21)).
                mobileNumber("9765456789").
                passcode("123456").
                clinic(clinic).
                travelTimeToClinicInDays("1").
                travelTimeToClinicInHours("2").
                travelTimeToClinicInHours("3").
                medicalHistory(TestHIVMedicalHistory.withDefault());
    }

    public static TestPatient withMandatory(TestClinician clinician) {
        return withMandatory(clinician.clinic());
    }

    private TestPatient clinic(TestClinic clinic) {
        this.clinic = clinic;
        return this;
    }

    public TestHIVMedicalHistory medicalHistory() {
        return hivMedicalHistory;
    }

    public TestPatient medicalHistory(TestHIVMedicalHistory hivMedicalHistory) {
        this.hivMedicalHistory = hivMedicalHistory;
        return this;
    }

    public TestPatient travelTimeToClinicInDays(String days) {
        travelTimeToClinicInDays = days;
        return this;
    }

    public TestPatient travelTimeToClinicInHours(String hours) {
        travelTimeToClinicInHours = hours;
        return this;
    }

    public TestPatient travelTimeToClinicInMinutues(String minutes) {
        travelTimeToClinicInMinutes = minutes;
        return this;
    }

    public TestPatient passcode(String passcode) {
        this.passcode = passcode;
        return this;
    }

    public TestPatient patientId(String patientId) {
        this.patientId = patientId;
        return this;
    }

    public TestPatient mobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
        return this;
    }

    public String mobileNumber() {
        return mobileNumber;
    }

    public TestPatient dateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
        return this;
    }

    public LocalDate dateOfBirth() {
        return dateOfBirth;
    }

    public String travelTimeToClinicInDays() {
        return travelTimeToClinicInDays;
    }

    public String travelTimeToClinicInHours() {
        return travelTimeToClinicInHours;
    }

    public String travelTimeToClinicInMinutes() {
        return travelTimeToClinicInMinutes;
    }

    public String passcode() {
        return passcode;
    }

    @Override
    public String resourceName() {
        return "patients";
    }

    public String patientId() {
        return patientId;
    }
}
