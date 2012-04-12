package org.motechproject.tama.dailypillreminder.domain;

import org.joda.time.LocalDate;

import java.util.List;

public class DosageAdherenceLogPerDay implements Comparable<DosageAdherenceLogPerDay> {

    List<DosageAdherenceLogSummary> logs;
    LocalDate date;

    public DosageAdherenceLogPerDay setLogs(List<DosageAdherenceLogSummary> dosageAdherenceLogSummaries) {
        this.logs = dosageAdherenceLogSummaries;
        return this;
    }

    public List<DosageAdherenceLogSummary> getLogs() {
        return logs;
    }

    public DosageAdherenceLogPerDay setDate(LocalDate date) {
        this.date = date;
        return this;
    }

    public LocalDate getDate() {
        return date;
    }

    @Override
    public int compareTo(DosageAdherenceLogPerDay dosageAdherenceLogPerDay) {
        return dosageAdherenceLogPerDay.date.compareTo(date);
    }
}
