package org.motechproject.tama.patient.service;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentMatcher;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.motechproject.server.alerts.contract.UpdateCriteria;
import org.motechproject.server.alerts.domain.Alert;
import org.motechproject.server.alerts.domain.AlertType;
import org.motechproject.server.alerts.contract.AlertService;
import org.motechproject.tama.common.repository.AllAuditEvents;
import org.motechproject.tama.facility.domain.Clinic;
import org.motechproject.tama.patient.builder.PatientBuilder;
import org.motechproject.tama.patient.domain.*;
import org.motechproject.tama.patient.repository.AllPatients;
import org.motechproject.util.DateUtil;

import java.util.HashMap;
import java.util.Map;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

public class PatientAlertServiceTest {

    @Mock
    private AlertService alertService;
    @Mock
    private AllPatients allPatients;
    @Mock
    private PatientAlertSearchService patientAlertSearchService;
    @Mock
    private AllAuditEvents allAuditEvents;

    private PatientAlertService patientAlertService;
    private static final String USER_NAME = "userName";

    @Before
    public void setUp() {
        initMocks(this);
        patientAlertService = new PatientAlertService(allPatients, alertService, patientAlertSearchService, allAuditEvents);
    }

    @Test
    public void shouldMarkTheAlertAsRead() {
        Patient patient = PatientBuilder.startRecording().withId("patientExternalId").withPatientId("patientId").build();

        final String alertId = "alertId";
        final Alert alertForPatient = new Alert(patient.getId(), AlertType.MEDIUM, org.motechproject.server.alerts.domain.AlertStatus.NEW, 2, null);
        alertForPatient.setId(alertId);
        alertForPatient.setExternalId(patient.getId());

        when(alertService.get(alertId)).thenReturn(alertForPatient);
        when(allPatients.get(patient.getId())).thenReturn(patient);

        PatientAlert symptomReportingAlert = patientAlertService.readAlert(alertId, USER_NAME);
        assertEquals(patient.getPatientId(), symptomReportingAlert.getPatientId());
        verify(alertService, times(1)).update(eq(alertId), argThat(new UpdateCriteriaMatcher(new UpdateCriteria().status(org.motechproject.server.alerts.domain.AlertStatus.READ))));
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
            put(PatientAlert.ALERT_STATUS, TamaAlertStatus.Open.name());
            put(PatientAlert.PATIENT_CALL_PREFERENCE, CallPreference.DailyPillReminder.displayName());
        }};
        verify(alertService).create(testPatientId, adviceGiven, symptomReported, AlertType.MEDIUM, org.motechproject.server.alerts.domain.AlertStatus.NEW, 2, data);
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
        verify(alertService).create(testPatientId, adviceGiven, symptomReported, AlertType.MEDIUM, org.motechproject.server.alerts.domain.AlertStatus.NEW, 2, data);
    }

    @Test
    public void shouldUpdateSymptomReportingAlert() {
        final String alertId = "alertId";
        when(alertService.get(alertId)).thenReturn(new Alert(alertId, AlertType.MEDIUM, org.motechproject.server.alerts.domain.AlertStatus.NEW, 2, null) {{
            setId(alertId);
        }});
        String doctorsNotes = "doctorsNotes";
        String notes = "notes";
        patientAlertService.updateAlertData(alertId, "Open", notes, doctorsNotes, PatientAlertType.SymptomReporting.toString(), USER_NAME);

        Map<String, String> data = new HashMap<String, String>();
        data.put(PatientAlert.ALERT_STATUS, "Open");
        data.put(PatientAlert.DOCTORS_NOTES, doctorsNotes);
        data.put(PatientAlert.NOTES, notes);
        verify(alertService, times(1)).update(eq(alertId), argThat(new UpdateCriteriaMatcher(new UpdateCriteria().data(data))));
    }

    @Test
    public void shouldUpdateAppointmentReminderAlert() {
        final String alertId = "alertId";
        when(alertService.get(alertId)).thenReturn(new Alert(alertId, AlertType.MEDIUM, org.motechproject.server.alerts.domain.AlertStatus.NEW, 2, null) {{
            setId(alertId);
        }});

        String doctorsNotes = "doctorsNotes";
        String notes = "notes";
        patientAlertService.updateAlertData(alertId, "Open", notes, doctorsNotes, PatientAlertType.AppointmentReminder.name(), USER_NAME);

        Map<String, String> data = new HashMap<String, String>();
        data.put(PatientAlert.NOTES, notes);
        verify(alertService, times(1)).update(eq(alertId), argThat(new UpdateCriteriaMatcher(new UpdateCriteria().data(data))));
    }

    @Test
    public void shouldNotUpdateAlertDataWhenThereAreNoChanges() {
        final String alertId = "alertId";
        String doctorsNotes = "doctorsNotes";
        String notes = "notes";
        final String symptomsAlertStatus = "Open";

        final Map<String, String> data = new HashMap<String, String>();
        data.put(PatientAlert.ALERT_STATUS, symptomsAlertStatus);
        data.put(PatientAlert.DOCTORS_NOTES, doctorsNotes);
        data.put(PatientAlert.NOTES, notes);

        when(alertService.get(alertId)).thenReturn(new Alert(alertId, AlertType.MEDIUM, org.motechproject.server.alerts.domain.AlertStatus.NEW, 2, null) {{
            setData(data);
            setId(alertId);
        }});

        patientAlertService.updateAlertData(alertId, symptomsAlertStatus, notes, doctorsNotes, PatientAlertType.SymptomReporting.name(), USER_NAME);

        verify(alertService, never()).update(Matchers.<String>any(), Matchers.<UpdateCriteria>any());
    }

    @Test
    public void shouldNotUpdateAlertDataWhenValuesAreNullOrEmpty() {
        final String alertId = "alertId";

        when(alertService.get(alertId)).thenReturn(new Alert(alertId, AlertType.MEDIUM, org.motechproject.server.alerts.domain.AlertStatus.NEW, 2, null) {{
            setData(new HashMap<String, String>());
            setId(alertId);
        }});

        patientAlertService.updateAlertData(alertId, "", "", "", PatientAlertType.SymptomReporting.name(), USER_NAME);

        verify(alertService, never()).update(Matchers.<String>any(), Matchers.<UpdateCriteria>any());
    }

    @Test
    public void shouldReturnFalseWhenUpdateUnSuccessful() {
        when(alertService.get(anyString())).thenReturn(new Alert());
        doThrow(new RuntimeException("update exception")).when(alertService).update(Matchers.<String>any(), Matchers.<UpdateCriteria>any());
        boolean condition = patientAlertService.updateAlertData("", "", "notes", "", "", "");
        assertFalse(condition);
    }

    @Test
    public void shouldGetFallingAdherenceAlerts() {
        final String patientId = "patientId";
        final DateTime startDate = DateUtil.now().minusDays(2);
        final DateTime endDate = DateUtil.now().plusDays(2);
        final Patient patient = new PatientBuilder().withId(patientId).build();
        when(allPatients.get(patientId)).thenReturn(patient);
        when(patientAlertSearchService.search(patientId, startDate, endDate, null)).thenReturn(new PatientAlerts() {{
            final HashMap<String, String> adherenceFallingAlertTypeData = new HashMap<String, String>() {{
                put(PatientAlert.PATIENT_ALERT_TYPE, PatientAlertType.FallingAdherence.name());
            }};

            final HashMap<String, String> symptomReportingAlertTypeData = new HashMap<String, String>() {{
                put(PatientAlert.PATIENT_ALERT_TYPE, PatientAlertType.SymptomReporting.name());
            }};
            add(PatientAlert.newPatientAlert(new Alert(patientId, AlertType.MEDIUM, org.motechproject.server.alerts.domain.AlertStatus.NEW, 1, adherenceFallingAlertTypeData), null));
            add(PatientAlert.newPatientAlert(new Alert(patientId, AlertType.MEDIUM, org.motechproject.server.alerts.domain.AlertStatus.NEW, 1, adherenceFallingAlertTypeData), null));
            add(PatientAlert.newPatientAlert(new Alert(patientId, AlertType.MEDIUM, org.motechproject.server.alerts.domain.AlertStatus.NEW, 1, symptomReportingAlertTypeData), null));
            add(PatientAlert.newPatientAlert(new Alert(patientId, AlertType.MEDIUM, org.motechproject.server.alerts.domain.AlertStatus.NEW, 1, symptomReportingAlertTypeData), null));
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
            final HashMap<String, String> symptomReportingAlertTypeData = new HashMap<String, String>() {{
                put(PatientAlert.PATIENT_ALERT_TYPE, PatientAlertType.SymptomReporting.name());
            }};
            add(PatientAlert.newPatientAlert(new Alert(patientId, AlertType.MEDIUM, org.motechproject.server.alerts.domain.AlertStatus.NEW, 1, data), null));
            add(PatientAlert.newPatientAlert(new Alert(patientId, AlertType.MEDIUM, org.motechproject.server.alerts.domain.AlertStatus.NEW, 1, data), null));
            add(PatientAlert.newPatientAlert(new Alert(patientId, AlertType.MEDIUM, org.motechproject.server.alerts.domain.AlertStatus.NEW, 1, symptomReportingAlertTypeData), null));
            add(PatientAlert.newPatientAlert(new Alert(patientId, AlertType.MEDIUM, org.motechproject.server.alerts.domain.AlertStatus.NEW, 1, symptomReportingAlertTypeData), null));
        }});


        final PatientAlerts adherenceInRedAlerts = patientAlertService.getAdherenceInRedAlerts(patientId, startDate, endDate);
        assertEquals(2, adherenceInRedAlerts.size());
        assertEquals(1, adherenceInRedAlerts.get(0).getAlert().getPriority());
        assertEquals(1, adherenceInRedAlerts.get(1).getAlert().getPriority());
    }

    @Test
    public void shouldReturnReadAlerts_filteredByPatientId() {
        final Clinic clinic = new Clinic() {{
            setId("testClinicId");
        }};
        final Patient patient = PatientBuilder.startRecording().withClinic(clinic).withPatientId("patientId_1").build();

        PatientAlerts readAlerts = new PatientAlerts() {{
            add(PatientAlert.newPatientAlert(new Alert(patient.getId(), AlertType.MEDIUM, org.motechproject.server.alerts.domain.AlertStatus.READ, 2, null), patient));
            add(PatientAlert.newPatientAlert(new Alert(patient.getId(), AlertType.MEDIUM, org.motechproject.server.alerts.domain.AlertStatus.READ, 3, null), patient));
        }};

        when(allPatients.findByPatientIdAndClinicId("patientId_1", "testClinicId")).thenReturn(patient);
        when(patientAlertSearchService.search(patient.getId(), null, null, org.motechproject.server.alerts.domain.AlertStatus.READ)).thenReturn(readAlerts);

        PatientAlerts readAlertsForClinic = patientAlertService.getReadAlertsFor("testClinicId", "patientId_1", null, null, null);

        assertEquals(2, readAlertsForClinic.size());
    }

    @Test
    public void shouldReturnAllAlerts_filteredByPatientId() {
        final Clinic clinic = new Clinic() {{
            setId("testClinicId");
        }};
        final Patient patient = PatientBuilder.startRecording().withClinic(clinic).withPatientId("patientId_1").build();

        PatientAlerts readAlerts = new PatientAlerts() {{
            add(PatientAlert.newPatientAlert(new Alert(patient.getId(), AlertType.MEDIUM, org.motechproject.server.alerts.domain.AlertStatus.READ, 2, null), patient));
            add(PatientAlert.newPatientAlert(new Alert(patient.getId(), AlertType.MEDIUM, org.motechproject.server.alerts.domain.AlertStatus.READ, 3, null), patient));
        }};

        when(allPatients.findByPatientIdAndClinicId("patientId_1", "testClinicId")).thenReturn(patient);
        when(patientAlertSearchService.search(patient.getId(), null, null, null)).thenReturn(readAlerts);

        PatientAlerts readAlertsForClinic = patientAlertService.getAllAlertsFor("testClinicId", "patientId_1", null, null, null);

        assertEquals(2, readAlertsForClinic.size());
    }

    @Test
    public void shouldReturnNoReadAlerts_filteredByAnInvalidPatientId() {
        when(allPatients.findByPatientIdAndClinicId("invalidPatientId", "testClinicId")).thenReturn(null);

        PatientAlerts readAlertsForClinic = patientAlertService.getReadAlertsFor("testClinicId", "invalidPatientId", null, null, null);

        assertEquals(0, readAlertsForClinic.size());
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
            add(PatientAlert.newPatientAlert(new Alert(patient_1.getId(), AlertType.MEDIUM, org.motechproject.server.alerts.domain.AlertStatus.READ, 2, adherenceInRedAlertData), patient_1));
            add(PatientAlert.newPatientAlert(new Alert(patient_1.getId(), AlertType.HIGH, org.motechproject.server.alerts.domain.AlertStatus.READ, 2, adherenceInRedAlertData), patient_1));
            add(PatientAlert.newPatientAlert(new Alert(patient_1.getId(), AlertType.MEDIUM, org.motechproject.server.alerts.domain.AlertStatus.READ, 3, fallingAdherenceAlertData), patient_1));
            add(PatientAlert.newPatientAlert(new Alert(patient_2.getId(), AlertType.MEDIUM, org.motechproject.server.alerts.domain.AlertStatus.READ, 2, adherenceInRedAlertData), patient_2));
            add(PatientAlert.newPatientAlert(new Alert(patient_2.getId(), AlertType.MEDIUM, org.motechproject.server.alerts.domain.AlertStatus.READ, 3, fallingAdherenceAlertData), patient_2));
        }};

        when(patientAlertSearchService.search(null, null, null, org.motechproject.server.alerts.domain.AlertStatus.NEW)).thenReturn(readAlerts);

        PatientAlerts readAlertsForClinic = patientAlertService.getUnreadAlertsFor("testClinicId", null, PatientAlertType.AdherenceInRed, null, null);

        assertEquals(3, readAlertsForClinic.size());
    }

    private class UpdateCriteriaMatcher extends ArgumentMatcher<UpdateCriteria> {
        private UpdateCriteria updateCriteria;

        private UpdateCriteriaMatcher(UpdateCriteria updateCriteria) {
            this.updateCriteria = updateCriteria;
        }

        @Override
        public boolean matches(Object argument) {
            UpdateCriteria other = (UpdateCriteria) argument;
            return updateCriteria.getAll().equals(other.getAll());
        }
    }
}
