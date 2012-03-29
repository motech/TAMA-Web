package org.motechproject.tama.patient.domain;

public enum PatientEvent {

    Suspension, Activation, Temporary_Deactivation, Call_Plan_Changed, Day_Of_Weekly_Call_Changed, Best_Call_Time_Changed;

    @Override
    public String toString() {
        return this.name();
    }
}
