package org.motechproject.tama.dailypillreminder.domain;

import org.joda.time.LocalDate;

import java.util.List;

public class DosageAdherenceLogPerDay implements Comparable<DosageAdherenceLogPerDay> {

    List<DosageAdherenceLogSummary> logs;
    LocalDate date;
    private String patientDocId;

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

    public DosageAdherenceLogPerDay setPatientDocId(String patientDocId) {
        this.patientDocId = patientDocId;
        return this;
    }

    public String getPatientDocId() {
        return patientDocId;
    }

    @Override
    public int compareTo(DosageAdherenceLogPerDay dosageAdherenceLogPerDay) {
        return date.compareTo(dosageAdherenceLogPerDay.date);
    }
}
