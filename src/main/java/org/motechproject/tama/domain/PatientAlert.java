package org.motechproject.tama.domain;


import org.joda.time.format.DateTimeFormat;
import org.motechproject.server.alerts.domain.Alert;

public class PatientAlert {

    public static String SYMPTOM_ALERT_TYPE = "Symptoms";
    public static final String NOTES = "Notes";
    public static final String DOCTORS_NOTES = "Doctor's Notes";
    public static final String SYMPTOMS_ALERT_STATUS = "Symptoms Alert Status";

    private Patient patient;

    private Alert alert;

    public Patient getPatient() {
        return patient;
    }

    public void setPatient(Patient patient) {
        this.patient = patient;
    }

    public Alert getAlert() {
        return alert;
    }

    public void setAlert(Alert alert) {
        this.alert = alert;
    }

    public String getPatientId() {
        return this.patient.getPatientId();
    }

    public String getPatientPhoneNumber() {
        return this.patient.getMobilePhoneNumber();
    }

    public String getAlertId() {
        return this.alert.getId();
    }

    public String getAlertPriority() {
        return String.format("SYMPTOM PRIO-%d", this.alert.getPriority());
    }

    public String getGeneratedOn() {
        return DateTimeFormat.forPattern("dd/MM/yyyy h:mm aa").print(this.alert.getDateTime());
    }

    public String getSymptomReported() {
        return this.alert.getDescription() ;
    }

    public String getAdviceGiven() {
        return this.alert.getName();
    }

    public String getType() {
        return SYMPTOM_ALERT_TYPE;
    }

    public String getNotes() {
        return this.alert.getData().get(NOTES);
    }

    public String getDoctorsNotes() {
        return this.alert.getData().get(DOCTORS_NOTES);
    }

    public String getSymptomsAlertStatus() {
        return this.alert.getData().get(SYMPTOMS_ALERT_STATUS);
    }

    public static PatientAlert newPatientAlert(Alert alert, Patient patient) {
        final PatientAlert patientAlert = new PatientAlert();
        patientAlert.setPatient(patient);
        patientAlert.setAlert(alert);
        return patientAlert;
    }
}