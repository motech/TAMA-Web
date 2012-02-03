package org.motechproject.tama.common.domain;

import org.joda.time.DateTime;

public class AdherenceSummaryForAWeek {
    private DateTime weekStartDate;
    private int taken;
    private int total;
    private double percentage;

    public int getTaken() {
        return taken;
    }

    public int getTotal() {
        return total;
    }

    public AdherenceSummaryForAWeek setTaken(int taken) {
        this.taken = taken;
        return this;
    }

    public AdherenceSummaryForAWeek setTotal(int total) {
        this.total = total;
        return this;
    }

    public DateTime getWeekStartDate() {
        return weekStartDate;
    }

    public AdherenceSummaryForAWeek setWeekStartDate(DateTime weekStartDate) {
        this.weekStartDate = weekStartDate;
        return this;
    }

    public double getPercentage() {
        return percentage;
    }

    public AdherenceSummaryForAWeek setPercentage(double percentage) {
        this.percentage = percentage;
        return this;
    }
}
