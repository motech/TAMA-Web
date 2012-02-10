package org.motechproject.tama.dailypillreminder.domain;

public class DosageAdherenceLogSummary {
    private String id;
    private String dosageId;
    private String dosageStatus;

    public String getId() {
        return id;
    }

    public DosageAdherenceLogSummary setId(String id) {
        this.id = id;
        return this;
    }

    public String getDosageId() {
        return dosageId;
    }

    public DosageAdherenceLogSummary setDosageId(String dosageId) {
        this.dosageId = dosageId;
        return this;
    }

    public String getDosageStatus() {
        return dosageStatus;
    }

    public DosageAdherenceLogSummary setDosageStatus(String dosageStatus) {
        this.dosageStatus = dosageStatus;
        return this;
    }
}
