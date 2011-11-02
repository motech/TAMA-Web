package org.motechproject.tamafunctional.testdata;

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

    public boolean isPregnant() {
        return pregnant;
    }

    public TestNonHIVMedicalHistory pregnant(boolean pregnant) {
        this.pregnant = pregnant;
        return this;
    }

    private enum SystemOption {
        YES, HISTORY, NONE
    }

    public static TestNonHIVMedicalHistory withMandatory() {
        TestNonHIVMedicalHistory testNonHIVMedicalHistory = new TestNonHIVMedicalHistory();
        return testNonHIVMedicalHistory.baseLinePreTherapyLowerThanTen(true).pregnant(false);
    }

//    private SystemOption
}
