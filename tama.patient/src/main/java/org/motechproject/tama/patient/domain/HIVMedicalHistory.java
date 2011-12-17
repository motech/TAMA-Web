package org.motechproject.tama.patient.domain;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.motechproject.tama.common.domain.BaseEntity;
import org.motechproject.tama.refdata.domain.HIVTestReason;
import org.motechproject.tama.refdata.domain.ModeOfTransmission;

public class HIVMedicalHistory extends BaseEntity {

    private HIVTestReason testReason;
    private String testReasonId;
    private ModeOfTransmission modeOfTransmission;
    private String modeOfTransmissionId;

    @JsonIgnore
    public HIVTestReason getTestReason() {
        return testReason;
    }

    public void setTestReason(HIVTestReason testReason) {
        this.testReason = testReason;
        this.testReasonId = testReason.getId();
    }

    public String getTestReasonId() {
        return testReasonId;
    }

    public void setTestReasonId(String testReasonId) {
        this.testReasonId = testReasonId;
    }

    @JsonIgnore
    public ModeOfTransmission getModeOfTransmission() {
        return modeOfTransmission;
    }

    public void setModeOfTransmission(ModeOfTransmission modeOfTransmission) {
        this.modeOfTransmission = modeOfTransmission;
        this.modeOfTransmissionId = modeOfTransmission.getId();
    }

    public String getModeOfTransmissionId() {
        return modeOfTransmissionId;
    }

    public void setModeOfTransmissionId(String modeOfTransmissionId) {
        this.modeOfTransmissionId = modeOfTransmissionId;
    }
}
