package org.motechproject.tama.patient.domain;


import junit.framework.Assert;
import org.joda.time.DateTime;
import org.junit.Test;
import org.motechproject.server.alerts.domain.Alert;
import org.motechproject.server.alerts.domain.AlertStatus;
import org.motechproject.server.alerts.domain.AlertType;
import org.motechproject.tama.common.TAMAConstants;
import org.motechproject.tama.patient.builder.PatientBuilder;
import org.motechproject.util.DateUtil;

import java.util.HashMap;

import static junit.framework.Assert.assertEquals;

public class PatientAlertTest {

    @Test
    public void shouldFormatDateCorrectly() {
        Alert alert = new Alert();
        final DateTime dateTime = DateUtil.newDateTime(DateUtil.newDate(2011, 9, 26), 12, 5, 30);
        alert.setDateTime(dateTime);

        final PatientAlert patientAlert = new PatientAlert();
        patientAlert.setAlert(alert);
        Assert.assertEquals("26/09/2011", patientAlert.getGeneratedOnDate());
    }

    @Test
    public void shouldFormatTimeCorrectly() {
        Alert alert = new Alert();
        final DateTime dateTime = DateUtil.newDateTime(DateUtil.newDate(2011, 9, 26), 12, 5, 30);
        alert.setDateTime(dateTime);

        final PatientAlert patientAlert = new PatientAlert();
        patientAlert.setAlert(alert);
        Assert.assertEquals("12:05 PM", patientAlert.getGeneratedOnTime());
    }

    @Test
    public void shouldFormatDateTimeCorrectly() {
        Alert alert = new Alert();
        final DateTime dateTime = DateUtil.newDateTime(DateUtil.newDate(2011, 9, 26), 12, 5, 30);
        alert.setDateTime(dateTime);

        final PatientAlert patientAlert = new PatientAlert();
        patientAlert.setAlert(alert);
        Assert.assertEquals("26/09/2011 12:05 PM", patientAlert.getGeneratedOn());
    }

    @Test
    public void shouldReturnConnectedToDoctor_ReportedType() {
        HashMap<String, String> data = new HashMap<String, String>();
        data.put(PatientAlert.CONNECTED_TO_DOCTOR, TAMAConstants.ReportedType.Yes.toString());
        data.put(PatientAlert.DOCTOR_NAME, "kumarasamy");
        Alert alert = new Alert("externalId", AlertType.MEDIUM, AlertStatus.NEW, 1, data);

        final PatientAlert patientAlert = new PatientAlert();
        patientAlert.setAlert(alert);
        Assert.assertEquals("kumarasamy", patientAlert.getConnectedToDoctor());
    }

    @Test
    public void shouldReturnConnectedToDoctor_ReportedType_WhenNoDataExists() {
        HashMap<String, String> empty = new HashMap<String, String>();
        Alert alert = new Alert("externalId", AlertType.MEDIUM, AlertStatus.NEW, 1, empty);

        final PatientAlert patientAlert = new PatientAlert();
        patientAlert.setAlert(alert);
        Assert.assertEquals("n/a", patientAlert.getConnectedToDoctor());
    }

    @Test
    public void shouldReturnEmptyStringWhenPriorityIsZero() {
        Alert alert = new Alert("externalId", AlertType.MEDIUM, AlertStatus.NEW, 0, new HashMap<String, String>());

        final PatientAlert patientAlert = new PatientAlert();
        patientAlert.setAlert(alert);
        Assert.assertEquals("n/a", patientAlert.getAlertPriority());
    }

    @Test
    public void shouldReturnPatientSummaryLink() {
        PatientAlert patientAlert = new PatientAlert();
        Patient patient = PatientBuilder.startRecording().withId("id").build();
        patientAlert.setPatient(patient);

        String patientSummaryLink = patientAlert.getPatientSummaryLink();
        assertEquals("patients/summary/id", patientSummaryLink);
    }

}
