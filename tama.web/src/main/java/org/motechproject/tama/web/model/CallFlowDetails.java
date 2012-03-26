package org.motechproject.tama.web.model;

public class CallFlowDetails {

    private int numberOfTimesAccessed;
    private String individualAccessDurations;
    private int totalAccessDuration;

    public int getNumberOfTimesAccessed() {
        return numberOfTimesAccessed;
    }

    public void setNumberOfTimesAccessed(int numberOfTimesAccessed) {
        this.numberOfTimesAccessed = numberOfTimesAccessed;
    }

    public String getIndividualAccessDurations() {
        return individualAccessDurations;
    }

    public void setIndividualAccessDurations(String individualAccessDurations) {
        this.individualAccessDurations = individualAccessDurations;
    }

    public int getTotalAccessDuration() {
        return totalAccessDuration;
    }

    public void setTotalAccessDuration(int totalAccessDuration) {
        this.totalAccessDuration = totalAccessDuration;
    }
}
