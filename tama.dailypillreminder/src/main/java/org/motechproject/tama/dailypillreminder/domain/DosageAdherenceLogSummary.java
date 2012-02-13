package org.motechproject.tama.dailypillreminder.domain;

import org.motechproject.model.Time;

public class DosageAdherenceLogSummary {
    private String id;
    private DosageStatus dosageStatus;
    private Time dosageTime;

    public String getId() {
        return id;
    }

    public DosageAdherenceLogSummary setId(String id) {
        this.id = id;
        return this;
    }

    public DosageStatus getDosageStatus() {
        return dosageStatus;
    }

    public DosageAdherenceLogSummary setDosageStatus(DosageStatus dosageStatus) {
        this.dosageStatus = dosageStatus;
        return this;
    }

    public Time getDosageTime() {
        return dosageTime;
    }

    public DosageAdherenceLogSummary setDosageTime(Time dosageTime) {
        this.dosageTime = dosageTime;
        return this;
    }
}
