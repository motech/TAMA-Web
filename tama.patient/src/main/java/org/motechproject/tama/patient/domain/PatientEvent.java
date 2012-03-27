package org.motechproject.tama.patient.domain;

public enum PatientEvent {

    Suspension, Activation, Temporary_Deactivation, Switched_To_Daily_Pill_Reminder, Switched_To_Weekly_Adherence;

    @Override
    public String toString() {
        return this.name();
    }
}
