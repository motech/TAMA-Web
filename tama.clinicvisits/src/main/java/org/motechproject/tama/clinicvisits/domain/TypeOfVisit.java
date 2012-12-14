package org.motechproject.tama.clinicvisits.domain;

public enum TypeOfVisit {
    Baseline,
    Scheduled,
    Unscheduled,
    UnscheduledWithAppointment;

    public String toLowerCase() {
        return toString().toLowerCase();
    }

    public boolean isBaselineVisit() {
        return this.equals(Baseline);
    }

    public boolean isScheduledVisit() {
        return this.equals(Scheduled);
    }

    public boolean isUnscheduledWithAppointment() {
        return this.equals(UnscheduledWithAppointment);
    }
}
