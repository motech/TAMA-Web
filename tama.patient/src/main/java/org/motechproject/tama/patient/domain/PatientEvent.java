package org.motechproject.tama.patient.domain;

public enum PatientEvent {

    Suspension("Suspension"),
    Activation("Activation"),
    Temporary_Deactivation("Temporary Deactivation"),
    Call_Plan_Changed("Call Plan Changed"),
    Day_Of_Weekly_Call_Changed("Day of Weekly Call Changed"),
    Best_Call_Time_Changed("Best Call Time Changed");

    private String displayName;

    PatientEvent(String displayName) {
        this.displayName = displayName;
    }

    @Override
    public String toString() {
        return this.name();
    }

    public String getDisplayName() {
        return this.displayName;
    }
}
