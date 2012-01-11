package org.motechproject.tama.patient.service;

import org.apache.commons.collections.CollectionUtils;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentMatcher;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.motechproject.server.alerts.domain.Alert;
import org.motechproject.server.alerts.domain.AlertCriteria;
import org.motechproject.server.alerts.domain.AlertStatus;
import org.motechproject.server.alerts.domain.AlertType;
import org.motechproject.server.alerts.service.AlertService;
import org.motechproject.tama.common.TAMAConstants;
import org.motechproject.tama.facility.domain.Clinic;
import org.motechproject.tama.patient.builder.PatientBuilder;
import org.motechproject.tama.patient.domain.*;
import org.motechproject.tama.patient.repository.AllPatients;
import org.motechproject.util.DateUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static junit.framework.Assert.*;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

public class PatientAlertServiceTest {

    @Mock
    private AlertService alertService;
    @Mock
    private AllPatients allPatients;

    private PatientAlertService patientAlertService;

    private HashMap<String, String> symptomReportingData = new HashMap<String, String>();

    @Before
    public void setUp() {
        initMocks(this);
        patientAlertService = new PatientAlertService(allPatients, alertService);
        symptomReportingData.put(PatientAlert.PATIENT_ALERT_TYPE, PatientAlertType.SymptomReporting.name());
    }

    @Test
    public void shouldReturnEmptyWhenNoAlertsFound() {
        String patientId = "patientId";
        Patient patient = PatientBuilder.startRecording().withDefaults().withPatientId(patientId).build();

        when(alertService.search(new AlertCriteria().byExternalId(same(patientId)))).thenReturn(null);
        when(allPatients.get(patientId)).thenReturn(patient);
        assertTrue(CollectionUtils.isEmpty(patientAlertService.getAllAlertsBy(patientId)));
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
    public void shouldReturnUnreadAlerts() {
        PatientAlerts patientAlerts = mock(PatientAlerts.class);
        patientAlertService = Mockito.spy(patientAlertService);
        Mockito.doReturn(patientAlerts).when(patientAlertService).getAlertsOfSpecificTypeAndStatusAndDateRange("clinicId", "patientId", AlertStatus.NEW, PatientAlertType.AdherenceInRed, null, null);

        PatientAlerts unReadAlerts = patientAlertService.getUnreadAlertsFor("clinicId", "patientId", PatientAlertType.AdherenceInRed, null, null);

        assertEquals(patientAlerts, unReadAlerts);
    }

    @Test
    public void shouldReturnAllAlerts_ByPatientId() {
        final String testPatientId1 = "testPatientId1";

        Patient patient1 = new Patient() {{
            setPatientId(testPatientId1);
            setId(testPatientId1);
        }};
        List<Alert> alerts = new ArrayList<Alert>() {{
            add(new Alert(testPatientId1, AlertType.MEDIUM, AlertStatus.NEW, 2, null));
            add(new Alert(testPatientId1, AlertType.MEDIUM, AlertStatus.READ, 2, null));
        }};

        when(allPatients.get(testPatientId1)).thenReturn(patient1);
        when(alertService.search(argThat(new ArgumentMatcher<AlertCriteria>() {
            @Override
            public boolean matches(Object o) {
                return ((AlertCriteria)o).externalId().equals(testPatientId1) && ((AlertCriteria)o).alertStatus() == null;
            }
        }))).thenReturn(alerts);

        PatientAlerts unReadAlertsByPatientId = patientAlertService.getAllAlertsBy(testPatientId1);
        assertEquals(alerts.size(), unReadAlertsByPatientId.size());
    }

    @Test
    public void shouldReturnReadAlerts() {
        PatientAlerts patientAlerts = mock(PatientAlerts.class);
        patientAlertService = Mockito.spy(patientAlertService);
        Mockito.doReturn(patientAlerts).when(patientAlertService).getAlertsOfSpecificTypeAndStatusAndDateRange("clinicId", "patientId", AlertStatus.READ, PatientAlertType.AdherenceInRed, null, null);

        PatientAlerts readAlerts = patientAlertService.getReadAlertsFor("clinicId", "patientId", PatientAlertType.AdherenceInRed, null, null);

        assertEquals(patientAlerts, readAlerts);
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

        Patient patient = new Patient() {{
            setPatientId(testPatientId);
            setId(testPatientId);
        }};
        List<Alert> alerts = new ArrayList<Alert>() {{
            add(new Alert(testPatientId, AlertType.MEDIUM, AlertStatus.NEW, 2, null) {{
                setData(symptomReportingData);
                setId(alertId);
            }});
        }};

        when(allPatients.get(testPatientId)).thenReturn(patient);
        when(alertService.search(argThat(new ArgumentMatcher<AlertCriteria>() {
            @Override
            public boolean matches(Object o) {
                return ((AlertCriteria)o).externalId().equals(testPatientId);
            }
        }))).thenReturn(alerts);

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
        when(alertService.search(argThat(new ArgumentMatcher<AlertCriteria>() {
            @Override
            public boolean matches(Object o) {
                return ((AlertCriteria)o).externalId().equals(patient.getId());
            }
        }))).thenReturn(new ArrayList<Alert>() {{
            final HashMap<String, String> data = new HashMap<String, String>() {{
                put(PatientAlert.PATIENT_ALERT_TYPE, PatientAlertType.FallingAdherence.name());
            }};
            add(new Alert(patientId, AlertType.MEDIUM, AlertStatus.NEW, 2, data) {{
                setDateTime(DateUtil.now().plusDays(7));
            }});
            add(new Alert(patientId, AlertType.MEDIUM, AlertStatus.NEW, 2, data) {{
                setDateTime(DateUtil.now().minusDays(7));
            }});
            add(new Alert(patientId, AlertType.MEDIUM, AlertStatus.NEW, 1, data));
            add(new Alert(patientId, AlertType.MEDIUM, AlertStatus.NEW, 1, data));
            add(new Alert(patientId, AlertType.MEDIUM, AlertStatus.NEW, 1, new HashMap<String, String>()));
            add(new Alert(patientId, AlertType.MEDIUM, AlertStatus.NEW, 1, null));
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
        when(alertService.search(argThat(new ArgumentMatcher<AlertCriteria>() {
            @Override
            public boolean matches(Object o) {
                return ((AlertCriteria)o).externalId().equals(patient.getId());
            }
        }))).thenReturn(new ArrayList<Alert>() {{
            final HashMap<String, String> data = new HashMap<String, String>() {{
                put(PatientAlert.PATIENT_ALERT_TYPE, PatientAlertType.AdherenceInRed.name());
            }};
            add(new Alert(patientId, AlertType.MEDIUM, AlertStatus.NEW, 2, data) {{
                setDateTime(DateUtil.now().plusDays(7));
            }});
            add(new Alert(patientId, AlertType.MEDIUM, AlertStatus.NEW, 2, data) {{
                setDateTime(DateUtil.now().minusDays(7));
            }});
            add(new Alert(patientId, AlertType.MEDIUM, AlertStatus.NEW, 1, data));
            add(new Alert(patientId, AlertType.MEDIUM, AlertStatus.NEW, 1, data));
            add(new Alert(patientId, AlertType.MEDIUM, AlertStatus.NEW, 1, new HashMap<String, String>()));
            add(new Alert(patientId, AlertType.MEDIUM, AlertStatus.NEW, 1, null));
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

        List<Alert> readAlerts = new ArrayList<Alert>() {{
            add(new Alert(patient.getId(), AlertType.MEDIUM, AlertStatus.READ, 2, null));
            add(new Alert(patient.getId(), AlertType.MEDIUM, AlertStatus.READ, 3, null));
        }};

        when(allPatients.findByPatientIdAndClinicId(patient.getPatientId(), "testClinicId")).thenReturn(patient);
        when(alertService.search(argThat(new ArgumentMatcher<AlertCriteria>() {
            @Override
            public boolean matches(Object o) {
                return ((AlertCriteria)o).externalId() == patient.getId() && ((AlertCriteria)o).alertStatus().equals(AlertStatus.READ);
            }
        }))).thenReturn(readAlerts);

        PatientAlerts readAlertsForClinic = patientAlertService.getAlertsOfSpecificTypeAndStatusAndDateRange("testClinicId", patient.getPatientId(), AlertStatus.READ, null, null, null);

        assertEquals(2, readAlertsForClinic.size());
    }

    @Test
    public void shouldReturnReadAlerts_filteredByPatientAlertType() {
        final Clinic clinic = new Clinic() {{
            setId("testClinicId");
        }};
        final Patient patient_1 = PatientBuilder.startRecording().withClinic(clinic).withId("patientId_1").build();
        final Patient patient_2 = PatientBuilder.startRecording().withClinic(clinic).withId("patientId_2").build();
        final List<Patient> registeredPatients = new ArrayList<Patient>();
        registeredPatients.add(patient_1);
        registeredPatients.add(patient_2);

        final HashMap<String, String> adherenceInRedAlertdata = new HashMap<String, String>() {{
            put(PatientAlert.PATIENT_ALERT_TYPE, PatientAlertType.AdherenceInRed.name());
        }};
        final HashMap<String, String> fallingAdherenceAlertdata = new HashMap<String, String>() {{
            put(PatientAlert.PATIENT_ALERT_TYPE, PatientAlertType.FallingAdherence.name());
        }};
        List<Alert> readAlertsForPatient_1 = new ArrayList<Alert>() {{
            add(new Alert(patient_1.getId(), AlertType.MEDIUM, AlertStatus.READ, 2, adherenceInRedAlertdata));
            add(new Alert(patient_1.getId(), AlertType.HIGH, AlertStatus.READ, 2, adherenceInRedAlertdata));
            add(new Alert(patient_1.getId(), AlertType.MEDIUM, AlertStatus.READ, 3, fallingAdherenceAlertdata));
        }};
        List<Alert> readAlertsForPatient_2 = new ArrayList<Alert>() {{
            add(new Alert(patient_2.getId(), AlertType.MEDIUM, AlertStatus.READ, 2, adherenceInRedAlertdata));
            add(new Alert(patient_2.getId(), AlertType.MEDIUM, AlertStatus.READ, 3, fallingAdherenceAlertdata));
        }};

        when(allPatients.get(patient_1.getId())).thenReturn(patient_1);
        when(allPatients.get(patient_2.getId())).thenReturn(patient_2);
        when(allPatients.findByClinic("testClinicId")).thenReturn(registeredPatients);
        when(alertService.search(argThat(new ArgumentMatcher<AlertCriteria>() {
            @Override
            public boolean matches(Object o) {
                return ((AlertCriteria)o).alertStatus().equals(AlertStatus.READ);
            }
        }))).thenReturn(readAlertsForPatient_1).thenReturn(readAlertsForPatient_2);

        PatientAlerts readAlertsForClinic = patientAlertService.getAlertsOfSpecificTypeAndStatusAndDateRange("testClinicId", null, AlertStatus.READ, PatientAlertType.AdherenceInRed, null, null);

        assertEquals(3, readAlertsForClinic.size());
    }

    @Test
    public void shouldReturnReadAlerts_filteredByStartDate() {
        final Clinic clinic = new Clinic() {{
            setId("testClinicId");
        }};
        final Patient patient = PatientBuilder.startRecording().withClinic(clinic).withId("patientId_1").build();
        final List<Patient> registeredPatients = new ArrayList<Patient>();
        registeredPatients.add(patient);

        final Alert alert_10_days_ago = new Alert(patient.getId(), AlertType.MEDIUM, AlertStatus.READ, 2, null);
        alert_10_days_ago.setDateTime(DateTime.now().minusDays(10));
        final Alert alert_5_days_ago = new Alert(patient.getId(), AlertType.HIGH, AlertStatus.READ, 2, null);
        alert_5_days_ago.setDateTime(DateTime.now().minusDays(5));
        final Alert alert_today = new Alert(patient.getId(), AlertType.MEDIUM, AlertStatus.READ, 3, null);
        alert_today.setDateTime(DateTime.now());

        List<Alert> readAlerts = new ArrayList<Alert>() {{
            add(alert_10_days_ago);
            add(alert_5_days_ago);
            add(alert_today);
        }};

        when(allPatients.get(patient.getId())).thenReturn(patient);
        when(allPatients.findByClinic("testClinicId")).thenReturn(registeredPatients);
        when(alertService.search(argThat(new ArgumentMatcher<AlertCriteria>() {
            @Override
            public boolean matches(Object o) {
                return ((AlertCriteria)o).externalId().equals(patient.getId()) && ((AlertCriteria)o).alertStatus().equals(AlertStatus.READ);
            }
        }))).thenReturn(readAlerts);

        DateTime startDate = DateTime.now().minusDays(8);
        PatientAlerts readAlertsForClinic = patientAlertService.getAlertsOfSpecificTypeAndStatusAndDateRange("testClinicId", null, AlertStatus.READ, null, startDate, null);

        System.out.println(readAlertsForClinic);
        assertEquals(2, readAlertsForClinic.size());
    }

    @Test
    public void shouldReturnReadAlerts_filteredByEndDate() {
        final Clinic clinic = new Clinic() {{
            setId("testClinicId");
        }};
        final Patient patient = PatientBuilder.startRecording().withClinic(clinic).withId("patientId_1").build();
        final List<Patient> registeredPatients = new ArrayList<Patient>();
        registeredPatients.add(patient);
        DateTime now = DateUtil.newDateTime(new LocalDate(2011, 12, 12), 0, 0, 0);

        final Alert alert_10_days_ago = new Alert(patient.getId(), AlertType.MEDIUM, AlertStatus.READ, 2, null);
        alert_10_days_ago.setDateTime(now.minusDays(10));
        final Alert alert_5_days_ago = new Alert(patient.getId(), AlertType.HIGH, AlertStatus.READ, 2, null);
        alert_5_days_ago.setDateTime(now.minusDays(5).plusMinutes(30));
        final Alert alert_today = new Alert(patient.getId(), AlertType.MEDIUM, AlertStatus.READ, 3, null);
        alert_today.setDateTime(now);

        List<Alert> readAlerts = new ArrayList<Alert>() {{
            add(alert_10_days_ago);
            add(alert_5_days_ago);
            add(alert_today);
        }};

        when(allPatients.get(patient.getId())).thenReturn(patient);
        when(allPatients.findByClinic("testClinicId")).thenReturn(registeredPatients);
        when(alertService.search(argThat(new ArgumentMatcher<AlertCriteria>() {
            @Override
            public boolean matches(Object o) {
                return ((AlertCriteria)o).externalId().equals(patient.getId()) && ((AlertCriteria)o).alertStatus().equals(AlertStatus.READ);
            }
        }))).thenReturn(readAlerts);

        DateTime endDate = now.minusDays(5);
        PatientAlerts readAlertsForClinic = patientAlertService.getAlertsOfSpecificTypeAndStatusAndDateRange("testClinicId", null, AlertStatus.READ, null, null, endDate);

        System.out.println(readAlertsForClinic);
        assertEquals(2, readAlertsForClinic.size());
    }
}
