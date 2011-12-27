package org.motechproject.tama.patient.domain;

public enum PatientAlertType {
    SymptomReporting,
    AppointmentReminder,
    FallingAdherence,
    AdherenceInRed;

    @Override
    public String toString() {
        return this.name();
    }
}
