package org.motechproject.tama.domain;

public class MedicalHistory extends BaseEntity {

    private HIVMedicalHistory hivMedicalHistory;

    public HIVMedicalHistory getHivMedicalHistory() {
        return hivMedicalHistory;
    }

    public void setHivMedicalHistory(HIVMedicalHistory hivMedicalHistory) {
        this.hivMedicalHistory = hivMedicalHistory;
    }
}
