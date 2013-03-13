package org.motechproject.tama.patient.domain;


import junit.framework.Assert;
import org.drools.core.util.StringUtils;
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
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

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
        assertEquals("/tama/patients/summary/id", patientSummaryLink);
    }

    @Test
    public void shouldReturnSymptomReportedIfSymptomReportingAlertDescriptionIsNotEmpty() {
        HashMap<String, String> data = new HashMap<String, String>();
        data.put(PatientAlert.PATIENT_ALERT_TYPE, PatientAlertType.SymptomReporting.name());
        Alert alert = mock(Alert.class);
        PatientAlert patientAlert = new PatientAlert();
        patientAlert.setAlert(alert);

        when(alert.getData()).thenReturn(data);
        when(alert.getDescription()).thenReturn("alert description");

        assertEquals("alert description", patientAlert.getSymptomReported());
    }

    @Test
    public void shouldReturnSymptomReportedAsEmptyIfSymptomReportingAlertDescriptionIsEmpty() {
        HashMap<String, String> data = new HashMap<String, String>();
        data.put(PatientAlert.PATIENT_ALERT_TYPE, PatientAlertType.SymptomReporting.name());
        Alert alert = mock(Alert.class);
        PatientAlert patientAlert = new PatientAlert();
        patientAlert.setAlert(alert);

        when(alert.getData()).thenReturn(data);
        when(alert.getDescription()).thenReturn("");

        assertEquals(StringUtils.EMPTY, patientAlert.getSymptomReported());
    }
}
