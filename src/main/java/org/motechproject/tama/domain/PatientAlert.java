package org.motechproject.tama.domain;


import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;
import org.ektorp.support.TypeDiscriminator;
import org.joda.time.format.DateTimeFormat;
import org.motechproject.server.alerts.domain.Alert;

@TypeDiscriminator("doc.documentType == 'PatientAlert'")
public class PatientAlert extends CouchEntity {

    public static String SYMPTOM_ALERT_TYPE = "Symptoms";

    private Patient patient;
    private Alert alert;

    @JsonProperty()
    private String patientId;

    @JsonProperty()
    private String alertId;

    @JsonProperty()
    private SymptomsAlertStatus symptomsAlertStatus;

    @JsonIgnore
    public Patient getPatient() {
        return patient;
    }

    public void setPatient(Patient patient) {
        this.patient = patient;
    }

    @JsonIgnore
    public Alert getAlert() {
        return alert;
    }

    public void setAlert(Alert alert) {
        this.alert = alert;
    }

    @JsonIgnore
    public String getPatientId() {
        return this.patient.getPatientId();
    }

    @JsonIgnore
    public String getPatientPhoneNumber() {
        return this.patient.getMobilePhoneNumber();
    }

    @JsonIgnore
    public String getAlertId() {
        return this.alert.getId();
    }

    @JsonIgnore
    public String getAlertPriority() {
        return String.format("SYMPTOM PRIO-%d", this.alert.getPriority());
    }

    @JsonIgnore
    public String getGeneratedOn() {
        return DateTimeFormat.forPattern("dd/MM/yyyy h:mm aa").print(this.alert.getDateTime());
    }

    @JsonIgnore
    public String getSymptomReported() {
        return this.alert.getDescription() ;
    }

    @JsonIgnore
    public String getAdviceGiven() {
        return this.alert.getName();
    }

    public String getType() {
        return SYMPTOM_ALERT_TYPE;
    }

    public static PatientAlert newPatientAlert(Alert alert, Patient patient) {
        final PatientAlert patientAlert = new PatientAlert();
        patientAlert.setPatient(patient);
        patientAlert.setAlert(alert);
        return patientAlert;
    }
}