package org.motechproject.tamacallflow.domain;

public class DeclinedDosageLog {

    private DosageAdherenceLog adherenceLog;

    public DeclinedDosageLog(DosageAdherenceLog log, DosageNotTakenReason dosageNotTakenReason) {
        this.adherenceLog = log;
        this.adherenceLog.setReason(dosageNotTakenReason);
    }

    public DosageAdherenceLog getAdherenceLog() {
        return adherenceLog;
    }
}
