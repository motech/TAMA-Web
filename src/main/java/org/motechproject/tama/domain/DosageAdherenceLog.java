package org.motechproject.tama.domain;

import java.util.Date;

public class DosageAdherenceLog extends CouchEntity{

    private String patientId;

    private String regimenId;

    private String dosageId;

    private Date dosageDate;

    private DosageStatus dosageStatus;

    public String getPatientId() {
        return patientId;
    }

    public void setPatientId(String patientId) {
        this.patientId = patientId;
    }

    public String getRegimenId() {
        return regimenId;
    }

    public void setRegimenId(String regimenId) {
        this.regimenId = regimenId;
    }

    public String getDosageId() {
        return dosageId;
    }

    public void setDosageId(String dosageId) {
        this.dosageId = dosageId;
    }

    public Date getDosageDate() {
        return dosageDate;
    }

    public void setDosageDate(Date dosageDate) {
        this.dosageDate = dosageDate;
    }

    public DosageStatus getDosageStatus() {
        return dosageStatus;
    }

    public void setDosageStatus(DosageStatus dosageStatus) {
        this.dosageStatus = dosageStatus;
    }
}
