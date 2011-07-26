package org.motechproject.tama.builder;

import org.motechproject.tama.domain.HIVMedicalHistory;
import org.motechproject.tama.domain.MedicalHistory;

public class MedicalHistoryBuilder {

    private MedicalHistory medicalHistory = new MedicalHistory();

    public MedicalHistoryBuilder withHIVMedicalHistory(HIVMedicalHistory hivMedicalHistory) {
        this.medicalHistory.setHivMedicalHistory(hivMedicalHistory);
        return this;
    }

    public MedicalHistory build() {
        return this.medicalHistory;
    }

    public static MedicalHistoryBuilder startRecording() {
        return new MedicalHistoryBuilder();
    }

    public MedicalHistoryBuilder withDefaults() {
        return this.withHIVMedicalHistory(HIVMedicalHistoryBuilder.startRecording().withDefaults().build());
    }
}
