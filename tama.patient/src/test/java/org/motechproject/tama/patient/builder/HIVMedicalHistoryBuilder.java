package org.motechproject.tama.patient.builder;

import org.motechproject.tama.patient.domain.HIVMedicalHistory;
import org.motechproject.tama.refdata.domain.HIVTestReason;
import org.motechproject.tama.refdata.domain.ModeOfTransmission;

public class HIVMedicalHistoryBuilder {

    private HIVMedicalHistory hivMedicalHistory = new HIVMedicalHistory();

    public HIVMedicalHistoryBuilder withTestReason(HIVTestReason testReason) {
        this.hivMedicalHistory.setTestReason(testReason);
        return this;
    }

    public HIVMedicalHistoryBuilder withModeOfTransmission(ModeOfTransmission modeOfTransmission) {
        this.hivMedicalHistory.setModeOfTransmission(modeOfTransmission);
        return this;
    }

    public HIVMedicalHistory build() {
        return this.hivMedicalHistory;
    }

    public static HIVMedicalHistoryBuilder startRecording() {
        return new HIVMedicalHistoryBuilder();
    }

    public HIVMedicalHistoryBuilder withDefaults() {
        return this.withModeOfTransmission(new ModeOfTransmission("Vertical")).withTestReason(new HIVTestReason("STDs"));
    }
}
