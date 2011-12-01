package org.motechproject.tamafunctional.testdata;

import org.joda.time.LocalDate;
import org.motechproject.tamacommon.util.UniqueMobileNumber;
import org.motechproject.util.DateUtil;

public class TestPatient extends TestEntity {
    private String patientId;
    private LocalDate dateOfBirth;
    private String mobileNumber;
    private String travelTimeToClinicInDays;
    private String travelTimeToClinicInHours;
    private String travelTimeToClinicInMinutes;
    private TestHIVMedicalHistory hivMedicalHistory;
    private TestNonHIVMedicalHistory nonHIVMedicalHistory;
    private TestPatientPreferences patientPreferences;

    private TestPatient() {
    }

    public static TestPatient withMandatory() {
        TestPatient testPatient = new TestPatient();
        return testPatient.patientId(unique("1234_")).
                dateOfBirth(DateUtil.newDate(1990, 5, 21)).
                mobileNumber(Long.toString(UniqueMobileNumber.generate())).
                travelTimeToClinicInDays("1").
                travelTimeToClinicInHours("2").
                travelTimeToClinicInHours("3").
                hivMedicalHistory(TestHIVMedicalHistory.withMandatory()).
                nonHIVMedicalHistory(TestNonHIVMedicalHistory.withMandatory()).
                patientPreferences(TestPatientPreferences.withMandatory());
    }

    public static TestPatient withMandatory(TestClinician clinician) {
        return withMandatory();
    }

    public TestHIVMedicalHistory hivMedicalHistory() {
        return hivMedicalHistory;
    }

    public TestPatient hivMedicalHistory(TestHIVMedicalHistory hivMedicalHistory) {
        this.hivMedicalHistory = hivMedicalHistory;
        return this;
    }

    public TestPatientPreferences patientPreferences() {
        return patientPreferences;
    }

    public TestPatient patientPreferences(TestPatientPreferences patientPreferences) {
        this.patientPreferences = patientPreferences;
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

    @Override
    public String resourceName() {
        return "patients";
    }

    public String patientId() {
        return patientId;
    }

    public TestNonHIVMedicalHistory nonHIVMedicalHistory() {
        return nonHIVMedicalHistory;
    }

    public TestPatient nonHIVMedicalHistory(TestNonHIVMedicalHistory nonHIVMedicalHistory) {
        this.nonHIVMedicalHistory = nonHIVMedicalHistory;
        return this;
    }
}
