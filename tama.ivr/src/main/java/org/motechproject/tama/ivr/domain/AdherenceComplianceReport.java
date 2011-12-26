package org.motechproject.tama.ivr.domain;

public class AdherenceComplianceReport {
    private boolean late;
    private boolean missed;

    public AdherenceComplianceReport(boolean late, boolean missed) {
        this.late = late;
        this.missed = missed;
    }

    public boolean late() {
        return late;
    }

    public boolean missed() {
        return missed;
    }
}
