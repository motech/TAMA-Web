package org.motechproject.tama.domain;

public class MedicalHistory extends BaseEntity {

    private HIVMedicalHistory hivMedicalHistory;
    private NonHIVMedicalHistory nonHivMedicalHistory;

    public HIVMedicalHistory getHivMedicalHistory() {
        return hivMedicalHistory;
    }

    public void setHivMedicalHistory(HIVMedicalHistory hivMedicalHistory) {
        this.hivMedicalHistory = hivMedicalHistory;
    }

    public NonHIVMedicalHistory getNonHivMedicalHistory() {
        return nonHivMedicalHistory;
    }

    public void setNonHivMedicalHistory(NonHIVMedicalHistory nonHivMedicalHistory) {
        this.nonHivMedicalHistory = nonHivMedicalHistory;
    }
}
