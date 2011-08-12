package org.motechproject.tamafunctional.testdata;

public class TestHIVMedicalHistory {
    private String testReason;
    private String modeOfTransmission;

    private TestHIVMedicalHistory() {
    }

    public static TestHIVMedicalHistory withDefault() {
        TestHIVMedicalHistory hivMedicalHistory = new TestHIVMedicalHistory();
        return hivMedicalHistory.modeOfTransmission("Vertical").testReason("STDs");
    }

    private TestHIVMedicalHistory testReason(String testReason) {
        this.testReason = testReason;
        return this;
    }

    private TestHIVMedicalHistory modeOfTransmission(String modeOfTransmission) {
        this.modeOfTransmission = modeOfTransmission;
        return this;
    }
}