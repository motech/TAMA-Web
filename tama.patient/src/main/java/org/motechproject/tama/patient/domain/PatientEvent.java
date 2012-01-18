package org.motechproject.tama.patient.domain;

public enum PatientEvent {
    Suspension, Activation, Temporary_Deactivation;

    @Override
    public String toString() {
        return this.name();
    }
}
