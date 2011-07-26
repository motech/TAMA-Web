package org.motechproject.tama.domain;

public class HIVMedicalHistory extends BaseEntity {

    private String testReasonId;
    private String modeOfTransmissionId;

    public String getTestReasonId() {
        return testReasonId;
    }

    public void setTestReasonId(String testReasonId) {
        this.testReasonId = testReasonId;
    }

    public String getModeOfTransmissionId() {
        return modeOfTransmissionId;
    }

    public void setModeOfTransmissionId(String modeOfTransmissionId) {
        this.modeOfTransmissionId = modeOfTransmissionId;
    }
}
