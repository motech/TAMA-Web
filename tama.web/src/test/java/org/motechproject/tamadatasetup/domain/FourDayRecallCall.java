package org.motechproject.tamadatasetup.domain;

import org.joda.time.DateTime;

public class FourDayRecallCall {
    private int numberOfDosageTaken;
    private DateTime callTime;

    public FourDayRecallCall(int numberOfDosageTaken, DateTime callTime) {
        this.numberOfDosageTaken = numberOfDosageTaken;
        this.callTime = callTime;
    }

    public DateTime callTime() {
        return callTime;
    }

    public int numberOfDosageTaken() {
        return numberOfDosageTaken;
    }
}
