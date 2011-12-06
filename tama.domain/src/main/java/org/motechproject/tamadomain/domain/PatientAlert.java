package org.motechproject.tamadomain.domain;


import org.apache.commons.lang.StringUtils;
import org.joda.time.format.DateTimeFormat;
import org.motechproject.server.alerts.domain.Alert;
import org.motechproject.tamacommon.TAMAConstants;

public class PatientAlert {

    public static final String PATIENT_ALERT_TYPE = "PatientAlertType";
    public static final String CONNECTED_TO_DOCTOR = "ConnectedToDoctor";
    public static final String DOCTOR_NAME = "DoctorName";
    public static final String NOTES = "Notes";
    public static final String DOCTORS_NOTES = "Doctor's Notes";
    public static final String SYMPTOMS_ALERT_STATUS = "Symptoms Alert Status";
    public static final String PATIENT_CALL_PREFERENCE = "Patient Call Preference";
    public static final String ADHERENCE = "Adherence";

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
        if (PatientAlertType.SymptomReporting.name().equals(getType()))
            return String.format("SYMPTOM PRIO-%d", this.alert.getPriority());
        if (this.alert.getPriority() > 0) return String.valueOf(this.alert.getPriority());
        return StringUtils.EMPTY;
    }

    public String getGeneratedOn() {
        return DateTimeFormat.forPattern("dd/MM/yyyy h:mm aa").print(this.alert.getDateTime());
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

    public String getPatientCallPreference(){
        return this.alert.getData().get(PATIENT_CALL_PREFERENCE);
    }

    public String getName() {
        return this.alert.getName();
    }

    public boolean isSymptomReportingAlert() {
        return PatientAlertType.SymptomReporting.name().equals(getType());
    }

    public String getType() {
        return this.alert.getData().get(PATIENT_ALERT_TYPE);
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

    public String getSymptomsAlertStatus() {
        return this.alert.getData().get(SYMPTOMS_ALERT_STATUS);
    }

    public String getConnectedToDoctor() {
        String connectedtoDoctorStatus = this.alert.getData().get(CONNECTED_TO_DOCTOR);
        String doctorName = this.alert.getData().get(DOCTOR_NAME);
        if (StringUtils.isEmpty(connectedtoDoctorStatus))  {
            return TAMAConstants.ReportedType.NA.toString();
        }
        return StringUtils.isEmpty(doctorName) ? connectedtoDoctorStatus : doctorName;
    }

    public static PatientAlert newPatientAlert(Alert alert, Patient patient) {
        final PatientAlert patientAlert = new PatientAlert();
        patientAlert.setPatient(patient);
        patientAlert.setAlert(alert);
        return patientAlert;
    }
}