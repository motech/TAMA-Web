package org.motechproject.tama.clinicvisits.domain;

public enum TypeOfVisit {
    Baseline,
    Scheduled,
    Unscheduled;

    public String toLowerCase() {
        return toString().toLowerCase();
    }

    public boolean isBaselineVisit() {
        return this.equals(Baseline);
    }

    public boolean isScheduledVisit() {
        return this.equals(Scheduled);
    }
}
