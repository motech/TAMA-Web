package org.motechproject.tamadatasetup.domain;

import org.joda.time.DateTime;

public class FourDayRecallPatientEvents {
    private int numberOfDosageTaken;
    private DateTime callTime;
    private boolean runFallingTrendJob;

    public FourDayRecallPatientEvents(int numberOfDosageTaken, DateTime callTime, boolean runFallingTrendJob) {
        this.numberOfDosageTaken = numberOfDosageTaken;
        this.callTime = callTime;
        this.runFallingTrendJob = runFallingTrendJob;
    }

    public DateTime callTime() {
        return callTime;
    }

    public int numberOfDosageTaken() {
        return numberOfDosageTaken;
    }

    public boolean runFallingTrendJob() {
        return runFallingTrendJob;
    }
}
