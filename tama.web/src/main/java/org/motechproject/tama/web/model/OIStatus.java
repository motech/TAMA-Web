package org.motechproject.tama.web.model;

public class OIStatus {

    private String opportunisticInfection;

    private boolean reported;

    public OIStatus() {
    }

    public String getOpportunisticInfection() {
        return opportunisticInfection;
    }

    public void setOpportunisticInfection(String opportunisticInfection) {
        this.opportunisticInfection = opportunisticInfection;
    }

    public boolean getReported() {
        return reported;
    }

    public void setReported(boolean reported) {
        this.reported = reported;
    }
}
