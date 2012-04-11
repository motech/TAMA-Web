package org.motechproject.tama.dailypillreminder.domain;

import org.joda.time.LocalDate;
import org.motechproject.model.Time;

public class DosageAdherenceLogSummary {
    private LocalDate dosageDate;
    private DosageStatus dosageStatus;
    private Time dosageTime;

    public LocalDate getDosageDate() {
        return dosageDate;
    }

    public DosageAdherenceLogSummary setDosageDate(LocalDate dosageDate) {
        this.dosageDate = dosageDate;
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
