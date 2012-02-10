package org.motechproject.tama.dailypillreminder.domain;

public class DosageAdherenceLogSummary {
    private String id;
    private String dosageId;
    private String dosageStatus;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDosageId() {
        return dosageId;
    }

    public void setDosageId(String dosageId) {
        this.dosageId = dosageId;
    }

    public String getDosageStatus() {
        return dosageStatus;
    }

    public void setDosageStatus(String dosageStatus) {
        this.dosageStatus = dosageStatus;
    }
}
