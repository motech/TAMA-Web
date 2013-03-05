package org.motechproject.tama.patient.domain;

import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.motechproject.server.alerts.domain.Alert;
import org.motechproject.server.alerts.domain.AlertStatus;
import org.motechproject.tama.common.TAMAConstants;

import javax.persistence.Temporal;
import javax.persistence.TemporalType;

public class PatientAlert {

    public static final String PATIENT_ALERT_TYPE = "PatientAlertType";
    public static final String CONNECTED_TO_DOCTOR = "ConnectedToDoctor";
    public static final String DOCTOR_NAME = "DoctorName";
    public static final String NOTES = "Notes";
    public static final String DOCTORS_NOTES = "Doctor's Notes";
    public static final String SYMPTOMS_ALERT_STATUS = "Symptoms Alert Status";
    public static final String PATIENT_CALL_PREFERENCE = "Patient Call Preference";
    public static final String ADHERENCE = "Adherence";
    public static final String APPOINTMENT_DATE = "AppointmentDate";
    public static final String CONFIRMED_APPOINTMENT_DATE = "ConfirmedAppointmentDate";

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
        if (this.alert.getPriority() == 0) {
            return "n/a";
        } else if (PatientAlertType.SymptomReporting.name().equals(getType().name())) {
            return String.format("SYMPTOM PRIO-%d", this.alert.getPriority());
        } else {
            return String.valueOf(this.alert.getPriority());
        }
    }

    public String getGeneratedOn() {
        return getFormattedDateTime("dd/MM/yyyy h:mm aa");
    }

    public String getGeneratedOnDate() {
        return getFormattedDateTime("dd/MM/yyyy");
    }

    public String getGeneratedOnTime() {
        return getFormattedDateTime("h:mm aa");
    }

    private String getFormattedDateTime(String format){
        return DateTimeFormat.forPattern(format).print(getGeneratedOnAsDateTime());
    }

    public DateTime getGeneratedOnAsDateTime(){
        return this.alert.getDateTime();
    }

    public String getSymptomReported() {
        return this.alert.getDescription();
    }

    public String getDescription() {
        return this.alert.getDescription();
    }

    public String getAdviceGiven() {
        return this.alert.getName();
    }

    public String getPatientCallPreference() {
        return this.alert.getData().get(PATIENT_CALL_PREFERENCE);
    }

    public String getName() {
        return this.alert.getName();
    }

    public boolean isSymptomReportingAlert() {
        return PatientAlertType.SymptomReporting.name().equals(getType().name());
    }

    public PatientAlertType getType() {
        return PatientAlertType.valueOf(this.alert.getData().get(PATIENT_ALERT_TYPE));
    }

    public String getTypeName() {
        return getType().toString();
    }

    public String getNotes() {
        return this.alert.getData().get(NOTES);
    }

    public String setNotes(String notes) {
        return this.alert.getData().put(NOTES, notes);
    }

    public String getDoctorsNotes() {
        return this.alert.getData().get(DOCTORS_NOTES);
    }

    public String getAlertStatus() {
        return AlertStatus.NEW.equals(this.alert.getStatus()) ? TamaAlertStatus.Open.name() : TamaAlertStatus.Closed.name();
    }

    public String getSymptomAlertStatus() {
        return this.alert.getData().get(SYMPTOMS_ALERT_STATUS);
    }

    @Temporal(TemporalType.DATE)
    @org.springframework.format.annotation.DateTimeFormat(style = "S-", pattern = TAMAConstants.DATE_FORMAT)
    public LocalDate getAppointmentDueDate() {
        return new LocalDate(this.alert.getData().get(APPOINTMENT_DATE));
    }

    @Temporal(TemporalType.DATE)
    @org.springframework.format.annotation.DateTimeFormat(style = "S-", pattern = TAMAConstants.DATETIME_FORMAT)
    public DateTime getConfirmedAppointmentDateTime() {
        return new DateTime(this.alert.getData().get(CONFIRMED_APPOINTMENT_DATE));
    }

    public String getConnectedToDoctor() {
        String connectedtoDoctorStatus = this.alert.getData().get(CONNECTED_TO_DOCTOR);
        String doctorName = this.alert.getData().get(DOCTOR_NAME);
        if (StringUtils.isEmpty(connectedtoDoctorStatus)) {
            return TAMAConstants.ReportedType.NA.toString();
        }
        return StringUtils.isEmpty(doctorName) ? connectedtoDoctorStatus : doctorName;
    }

    public static PatientAlert newPatientAlert(Alert alert, org.motechproject.tama.patient.domain.Patient patient) {
        final PatientAlert patientAlert = new PatientAlert();
        patientAlert.setPatient(patient);
        patientAlert.setAlert(alert);
        return patientAlert;
    }

}