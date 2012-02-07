package org.motechproject.tamafunctionalframework.testdata;

public class TestNonHIVMedicalHistory {
    private boolean pregnant;

    private boolean baseLinePreTherapyLowerThanTen;

    public boolean isBaseLinePreTherapyLowerThanTen() {
        return baseLinePreTherapyLowerThanTen;
    }

    public TestNonHIVMedicalHistory baseLinePreTherapyLowerThanTen(boolean baseLinePreTherapyLowerThanTen) {
        this.baseLinePreTherapyLowerThanTen = baseLinePreTherapyLowerThanTen;
        return this;
    }

    public TestNonHIVMedicalHistory pregnant(boolean pregnant) {
        this.pregnant = pregnant;
        return this;
    }

    public static TestNonHIVMedicalHistory withMandatory() {
        TestNonHIVMedicalHistory testNonHIVMedicalHistory = new TestNonHIVMedicalHistory();
        return testNonHIVMedicalHistory.baseLinePreTherapyLowerThanTen(true).pregnant(false);
    }
}
