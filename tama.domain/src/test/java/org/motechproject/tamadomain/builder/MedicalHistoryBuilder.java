package org.motechproject.tamadomain.builder;

import org.motechproject.tamadomain.domain.*;

public class MedicalHistoryBuilder {

    private MedicalHistory medicalHistory = new MedicalHistory();

    public MedicalHistoryBuilder withHIVMedicalHistory(HIVMedicalHistory hivMedicalHistory) {
        this.medicalHistory.setHivMedicalHistory(hivMedicalHistory);
        return this;
    }

    public MedicalHistoryBuilder withNonHIVMedicalHistory(NonHIVMedicalHistory nonHivMedicalHistory) {
        this.medicalHistory.setNonHivMedicalHistory(nonHivMedicalHistory);
        return this;
    }


    public MedicalHistory build() {
        return this.medicalHistory;
    }

    public static MedicalHistoryBuilder startRecording() {
        return new MedicalHistoryBuilder();
    }

    public MedicalHistoryBuilder withDefaults() {
        HIVMedicalHistory hivMedicalHistory = HIVMedicalHistoryBuilder.startRecording().withDefaults().build();
        NonHIVMedicalHistory nonHivMedicalHistory = new NonHIVMedicalHistory();

        SystemCategoryDefinition systemCategoryDefiniton = SystemCategoryDefinition.Other;
        SystemCategory systemCategory = new SystemCategory(systemCategoryDefiniton.getCategoryName(), systemCategoryDefiniton.getAilments());
        nonHivMedicalHistory.addSystemCategory(systemCategory);

        return this.withHIVMedicalHistory(hivMedicalHistory).withNonHIVMedicalHistory(nonHivMedicalHistory);
    }
}
