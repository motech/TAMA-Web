package org.motechproject.tama.patient.domain;

public enum PatientAlertType {
    SymptomReporting("Symptom Reporting"),
    AppointmentReminder("Appointment Reminder"),
    AppointmentConfirmationMissed("Appointment Lost"),
    VisitMissed("Appointment Missed"),
    FallingAdherence("Falling Adherence"),
    AdherenceInRed("Adherence In Red");
    private String displayName;

    PatientAlertType(String displayName) {
        this.displayName = displayName;
    }

    @Override
    public String toString() {
        if (displayName != null) return  displayName;
        return this.name();
    }

    public String getDisplayName() {
        return this.displayName;
    }


}
