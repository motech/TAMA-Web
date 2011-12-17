package org.motechproject.tamadatasetup.domain;

import org.joda.time.DateTime;

public class DailyPatientEvents {
    private DateTime morningDosageDateTime;
    private DateTime eveningDosageDateTime;
    private boolean dosageTaken;
    private boolean runAdherenceTrendJob;
    private DateTime bestCallTime;

    public DailyPatientEvents dosageTaken(boolean dosageTaken) {
        this.dosageTaken = dosageTaken;
        return this;
    }

    public DailyPatientEvents runAdherenceTrendJob(boolean value) {
        runAdherenceTrendJob = value;
        return this;
    }

    public boolean runAdherenceTrendJob() {
        return runAdherenceTrendJob;
    }

    public boolean dosageTaken() {
        return dosageTaken;
    }

    public DateTime bestCallTime() {
        return bestCallTime;
    }

    public DailyPatientEvents bestCallTime(DateTime dateTime) {
        this.bestCallTime = dateTime;
        return this;
    }

    public DailyPatientEvents morningDosageDateTime(DateTime dateTime) {
        morningDosageDateTime = dateTime;
        return this;
    }

    public DailyPatientEvents eveningDosageDateTime(DateTime dateTime) {
        eveningDosageDateTime = dateTime;
        return this;
    }

    public DateTime morningDosageDateTime() {
        return morningDosageDateTime;
    }

    public DateTime eveningDosageDateTime() {
        return eveningDosageDateTime;
    }

    public DateTime dateTime() {
        return morningDosageDateTime() == null ? eveningDosageDateTime() : morningDosageDateTime();
    }
}
