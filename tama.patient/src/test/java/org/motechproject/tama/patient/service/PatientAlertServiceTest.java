package org.motechproject.tama.patient.service;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.motechproject.server.alerts.domain.Alert;
import org.motechproject.server.alerts.domain.AlertStatus;
import org.motechproject.server.alerts.domain.AlertType;
import org.motechproject.server.alerts.service.AlertService;
import org.motechproject.tama.common.TAMAConstants;
import org.motechproject.tama.facility.domain.Clinic;
import org.motechproject.tama.patient.builder.PatientBuilder;
import org.motechproject.tama.patient.domain.*;
import org.motechproject.tama.patient.repository.AllPatients;
import org.motechproject.util.DateUtil;

import java.util.HashMap;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

public class PatientAlertServiceTest {

    @Mock
    private AlertService alertService;
    @Mock
    private AllPatients allPatients;
    @Mock
    private PatientAlertSearchService patientAlertSearchService;

    private PatientAlertService patientAlertService;

    private HashMap<String, String> symptomReportingData = new HashMap<String, String>();

    @Before
    public void setUp() {
        initMocks(this);
        patientAlertService = new PatientAlertService(allPatients, alertService, patientAlertSearchService);
        symptomReportingData.put(PatientAlert.PATIENT_ALERT_TYPE, PatientAlertType.SymptomReporting.name());
    }

    @Test
    public void shouldMarkTheAlertAsRead() {
        Patient patient = PatientBuilder.startRecording().withId("patientExternalId").withPatientId("patientId").build();

        final String alertId = "alertId";
        final Alert alertForPatient = new Alert(patient.getId(), AlertType.MEDIUM, AlertStatus.NEW, 2, null);
        alertForPatient.setId(alertId);
        alertForPatient.setExternalId(patient.getId());

        when(alertService.get(alertId)).thenReturn(alertForPatient);
        when(allPatients.get(patient.getId())).thenReturn(patient);

        PatientAlert symptomReportingAlert = patientAlertService.readAlert(alertId);
        assertEquals(patient.getPatientId(), symptomReportingAlert.getPatientId());
        verify(alertService, times(1)).changeStatus(alertId, AlertStatus.READ);
    }

    @Test
    public void shouldCreateSymptomReportingAlert() {
        final String testPatientId = "testPatientId";
        final String symptomReported = "some ugly rash";
        final String adviceGiven = "have a bath";

        Patient patient = new PatientBuilder().withDefaults().withCallPreference(CallPreference.DailyPillReminder).build();
        when(allPatients.get(testPatientId)).thenReturn(patient);

        patientAlertService.createAlert(testPatientId, 2, adviceGiven, symptomReported, PatientAlertType.SymptomReporting, new HashMap<String, String>());
        HashMap<String, String> data = new HashMap<String, String>() {{
            put(PatientAlert.PATIENT_ALERT_TYPE, PatientAlertType.SymptomReporting.name());
            put(PatientAlert.SYMPTOMS_ALERT_STATUS, SymptomsAlertStatus.Open.name());
            put(PatientAlert.PATIENT_CALL_PREFERENCE, CallPreference.DailyPillReminder.displayName());
        }};
        verify(alertService).create(testPatientId, adviceGiven, symptomReported, AlertType.MEDIUM, AlertStatus.NEW, 2, data);
    }

    @Test
    public void shouldUpdateSymptomReportingAlert() {
        final String testPatientId = "testPatientId";
        String doctorsNotes = "doctorsNotes";
        String notes = "notes";
        patientAlertService.updateAlert(testPatientId, "Open", notes, doctorsNotes, PatientAlertType.SymptomReporting.name());
        verify(alertService).setData(testPatientId, PatientAlert.SYMPTOMS_ALERT_STATUS, "Open");
        verify(alertService).setData(testPatientId, PatientAlert.DOCTORS_NOTES, doctorsNotes);
        verify(alertService).setData(testPatientId, PatientAlert.NOTES, notes);
    }

    @Test
    public void shouldCreateAppointmentReminderAlert() {
        final String testPatientId = "testPatientId";
        final String symptomReported = "some ugly rash";
        final String adviceGiven = "have a bath";

        patientAlertService.createAlert(testPatientId, 2, adviceGiven, symptomReported, PatientAlertType.AppointmentReminder, new HashMap<String, String>());
        HashMap<String, String> data = new HashMap<String, String>() {{
            put(PatientAlert.PATIENT_ALERT_TYPE, PatientAlertType.AppointmentReminder.name());
        }};
        verify(alertService).create(testPatientId, adviceGiven, symptomReported, AlertType.MEDIUM, AlertStatus.NEW, 2, data);
    }

    @Test
    public void shouldUpdateAppointmentReminderAlert() {
        final String testPatientId = "testPatientId";
        String doctorsNotes = "doctorsNotes";
        String notes = "notes";
        patientAlertService.updateAlert(testPatientId, "Open", notes, doctorsNotes, PatientAlertType.AppointmentReminder.name());
        verify(alertService, never()).setData(testPatientId, PatientAlert.SYMPTOMS_ALERT_STATUS, "Open");
        verify(alertService, never()).setData(testPatientId, PatientAlert.DOCTORS_NOTES, doctorsNotes);
        verify(alertService).setData(testPatientId, PatientAlert.NOTES, notes);
    }

    @Test
    public void shouldUpdateDoctorConnectedToDuringSymptomCall() {
        final String testPatientId = "testPatientId";
        final String alertId = "alertId";
        String doctorName = "kumarasamy";

        final Patient patient = new Patient() {{
            setPatientId(testPatientId);
            setId(testPatientId);
        }};
        PatientAlerts alerts = new PatientAlerts() {{
            add(PatientAlert.newPatientAlert(new Alert(testPatientId, AlertType.MEDIUM, AlertStatus.NEW, 2, null) {{
            setData(symptomReportingData);
            setId(alertId);
            }}, patient));
        }};

        when(allPatients.get(testPatientId)).thenReturn(patient);
        when(patientAlertSearchService.search(testPatientId)).thenReturn(alerts);

        patientAlertService.updateDoctorConnectedToDuringSymptomCall(testPatientId, doctorName);

        verify(alertService).setData(alertId, PatientAlert.CONNECTED_TO_DOCTOR, TAMAConstants.ReportedType.Yes.toString());
        verify(alertService).setData(alertId, PatientAlert.DOCTOR_NAME, doctorName);
    }

    @Test
    public void shouldGetFallingAdherenceAlerts() {
        final String patientId = "patientId";
        final DateTime startDate = DateUtil.now().minusDays(2);
        final DateTime endDate = DateUtil.now().plusDays(2);
        final Patient patient = new PatientBuilder().withId(patientId).build();
        when(allPatients.get(patientId)).thenReturn(patient);
        when(patientAlertSearchService.search(patientId, startDate, endDate, null)).thenReturn(new PatientAlerts() {{
            final HashMap<String, String> data = new HashMap<String, String>() {{
                put(PatientAlert.PATIENT_ALERT_TYPE, PatientAlertType.FallingAdherence.name());
            }};
            add(PatientAlert.newPatientAlert(new Alert(patientId, AlertType.MEDIUM, AlertStatus.NEW, 1, data), null));
            add(PatientAlert.newPatientAlert(new Alert(patientId, AlertType.MEDIUM, AlertStatus.NEW, 1, data), null));
            add(PatientAlert.newPatientAlert(new Alert(patientId, AlertType.MEDIUM, AlertStatus.NEW, 1, new HashMap<String, String>()), null));
            add(PatientAlert.newPatientAlert(new Alert(patientId, AlertType.MEDIUM, AlertStatus.NEW, 1, null), null));
        }});

        final PatientAlerts fallingAdherenceAlerts = patientAlertService.getFallingAdherenceAlerts(patientId, startDate, endDate);
        assertEquals(2, fallingAdherenceAlerts.size());
        assertEquals(1, fallingAdherenceAlerts.get(0).getAlert().getPriority());
        assertEquals(1, fallingAdherenceAlerts.get(1).getAlert().getPriority());
    }

    @Test
    public void shouldGetAdherenceInRedAlerts() {
        final String patientId = "patientId";
        final DateTime startDate = DateUtil.now().minusDays(2);
        final DateTime endDate = DateUtil.now().plusDays(2);
        final Patient patient = new PatientBuilder().withId(patientId).build();
        when(allPatients.get(patientId)).thenReturn(patient);
        when(patientAlertSearchService.search(patientId, startDate, endDate, null)).thenReturn(new PatientAlerts() {{
            final HashMap<String, String> data = new HashMap<String, String>() {{
                put(PatientAlert.PATIENT_ALERT_TYPE, PatientAlertType.AdherenceInRed.name());
            }};
            add(PatientAlert.newPatientAlert(new Alert(patientId, AlertType.MEDIUM, AlertStatus.NEW, 1, data), null));
            add(PatientAlert.newPatientAlert(new Alert(patientId, AlertType.MEDIUM, AlertStatus.NEW, 1, data), null));
            add(PatientAlert.newPatientAlert(new Alert(patientId, AlertType.MEDIUM, AlertStatus.NEW, 1, new HashMap<String, String>()), null));
            add(PatientAlert.newPatientAlert(new Alert(patientId, AlertType.MEDIUM, AlertStatus.NEW, 1, null), null));
        }});


        final PatientAlerts adherenceInRedAlerts = patientAlertService.getAdherenceInRedAlerts(patientId, startDate, endDate);
        assertEquals(2, adherenceInRedAlerts.size());
        assertEquals(1, adherenceInRedAlerts.get(0).getAlert().getPriority());
        assertEquals(1, adherenceInRedAlerts.get(1).getAlert().getPriority());
    }

    @Test
    public void shouldReturnFalseWhenUpdateUnSuccessful() {
        doThrow(new RuntimeException("update exception")).when(alertService).setData(anyString(), anyString(), anyString());
        assertFalse(patientAlertService.updateAlert(anyString(), anyString(), anyString(), anyString(), anyString()));
    }

    @Test
    public void shouldReturnReadAlerts_filteredByPatientId() {
        final Clinic clinic = new Clinic() {{
            setId("testClinicId");
        }};
        final Patient patient = PatientBuilder.startRecording().withClinic(clinic).withPatientId("patientId_1").build();

        PatientAlerts readAlerts = new PatientAlerts() {{
            add(PatientAlert.newPatientAlert(new Alert(patient.getId(), AlertType.MEDIUM, AlertStatus.READ, 2, null), patient));
            add(PatientAlert.newPatientAlert(new Alert(patient.getId(), AlertType.MEDIUM, AlertStatus.READ, 3, null), patient));
        }};

        when(allPatients.findByPatientIdAndClinicId(patient.getId(), "testClinicId")).thenReturn(patient);
        when(patientAlertSearchService.search(patient.getId(), null, null, AlertStatus.READ)).thenReturn(readAlerts);

        PatientAlerts readAlertsForClinic = patientAlertService.getReadAlertsFor("testClinicId", patient.getId(), null, null, null);

        assertEquals(2, readAlertsForClinic.size());
    }

    @Test
    public void shouldReturnUnreadAlerts_filteredByPatientAlertType() {
        final Clinic clinic = new Clinic() {{
            setId("testClinicId");
        }};
        final Patient patient_1 = PatientBuilder.startRecording().withClinic(clinic).withId("patientId_1").build();
        final Patient patient_2 = PatientBuilder.startRecording().withClinic(clinic).withId("patientId_2").build();

        final HashMap<String, String> adherenceInRedAlertData = new HashMap<String, String>() {{
            put(PatientAlert.PATIENT_ALERT_TYPE, PatientAlertType.AdherenceInRed.name());
        }};
        final HashMap<String, String> fallingAdherenceAlertData = new HashMap<String, String>() {{
            put(PatientAlert.PATIENT_ALERT_TYPE, PatientAlertType.FallingAdherence.name());
        }};
        PatientAlerts readAlerts = new PatientAlerts() {{
            add(PatientAlert.newPatientAlert(new Alert(patient_1.getId(), AlertType.MEDIUM, AlertStatus.READ, 2, adherenceInRedAlertData), patient_1));
            add(PatientAlert.newPatientAlert(new Alert(patient_1.getId(), AlertType.HIGH, AlertStatus.READ, 2, adherenceInRedAlertData), patient_1));
            add(PatientAlert.newPatientAlert(new Alert(patient_1.getId(), AlertType.MEDIUM, AlertStatus.READ, 3, fallingAdherenceAlertData), patient_1));
            add(PatientAlert.newPatientAlert(new Alert(patient_2.getId(), AlertType.MEDIUM, AlertStatus.READ, 2, adherenceInRedAlertData), patient_2));
            add(PatientAlert.newPatientAlert(new Alert(patient_2.getId(), AlertType.MEDIUM, AlertStatus.READ, 3, fallingAdherenceAlertData), patient_2));
        }};

        when(patientAlertSearchService.search(null, null, null, AlertStatus.NEW)).thenReturn(readAlerts);

        PatientAlerts readAlertsForClinic = patientAlertService.getUnreadAlertsFor("testClinicId", null, PatientAlertType.AdherenceInRed, null, null);

        assertEquals(3, readAlertsForClinic.size());
    }
}
