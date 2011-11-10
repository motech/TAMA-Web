package org.motechproject.tama.service;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentMatcher;
import org.mockito.Mock;
import org.motechproject.server.alerts.domain.Alert;
import org.motechproject.server.alerts.domain.AlertStatus;
import org.motechproject.server.alerts.domain.AlertType;
import org.motechproject.server.alerts.service.AlertService;
import org.motechproject.tama.domain.*;
import org.motechproject.tama.repository.AllPatients;
import org.motechproject.tama.web.command.EmptyMapMatcher;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.powermock.api.mockito.PowerMockito.when;

public class PatientAlertServiceTest {
    @Mock
    private AlertService alertService;
    @Mock
    private AllPatients allPatients;

    PatientAlertService patientAlertService;
    private EmptyMapMatcher emptyMapMatcher;

    @Before
    public void setUp() {
        initMocks(this);
        patientAlertService = new PatientAlertService(allPatients, alertService);
        emptyMapMatcher = new EmptyMapMatcher();
    }

    @Test
    public void shouldReturnCorrectAlert() {
        final String patientId = "patientId";
        final Alert correctAlert = new Alert(patientId, AlertType.MEDIUM, AlertStatus.NEW, 2, null);
        final String testId = "testId";
        correctAlert.setId(testId);
        correctAlert.setExternalId(testId);

        Patient patient = new Patient() {{
            setId(testId);
            setPatientId(testId);
        }};
        List<Alert> alerts = new ArrayList<Alert>() {{
            add(new Alert("externalId1", AlertType.MEDIUM, AlertStatus.NEW, 2, null));
            add(new Alert("externalId2", AlertType.MEDIUM, AlertStatus.NEW, 3, null));
            add(correctAlert);
        }};
        when(alertService.getBy(null, null, null, null, 100)).thenReturn(alerts);
        when(allPatients.get(testId)).thenReturn(patient);

        PatientAlert symptomReportingAlert = patientAlertService.getPatientAlert(testId);

        assertEquals(testId, symptomReportingAlert.getPatientId());
    }

    @Test
    public void shouldChangeStatusOfAlert() {
        final String patientId = "patientId";
        final Alert correctAlert = new Alert(patientId, AlertType.MEDIUM, AlertStatus.NEW, 2, null);
        String testId = "testId";
        correctAlert.setId(testId);

        List<Alert> alerts = new ArrayList<Alert>() {{
            add(new Alert("externalId1", AlertType.MEDIUM, AlertStatus.NEW, 2, null));
            add(new Alert("externalId2", AlertType.MEDIUM, AlertStatus.NEW, 3, null));
            add(correctAlert);
        }};
        when(alertService.getBy(null, null, null, null, 100)).thenReturn(alerts);

        patientAlertService.getPatientAlert(testId);

        verify(alertService, times(1)).changeStatus(testId, AlertStatus.READ);
    }


    @Test
    public void shouldReturnUnreadAlerts() {
        final String testPatientId = "testPatientId";
        final String testClinicId = "testClinicId";


        final Clinic clinic = new Clinic() {{
            setId(testClinicId);
        }};
        List<Patient> clinicPatients = new ArrayList<Patient>() {{
            add(new Patient() {{
                setClinic(clinic);
                setPatientId(testPatientId);
                setId(testPatientId);
            }});
        }};
        List<Alert> alerts = new ArrayList<Alert>() {{
            add(new Alert(testPatientId, AlertType.MEDIUM, AlertStatus.NEW, 2, null));
            add(new Alert(testPatientId, AlertType.MEDIUM, AlertStatus.NEW, 3, null));
            add(new Alert(testPatientId, AlertType.MEDIUM, AlertStatus.NEW, 3, null));
            add(new Alert(testPatientId, AlertType.MEDIUM, AlertStatus.NEW, 3, null));
        }};

        when(allPatients.findByClinic(testClinicId)).thenReturn(clinicPatients);
        when(alertService.getBy(testPatientId, null, AlertStatus.NEW, null, 100)).thenReturn(alerts);

        List<PatientAlert> unReadAlertsForClinic = patientAlertService.getUnreadAlertsForClinic(testClinicId);

        assertEquals(alerts.size(), unReadAlertsForClinic.size());
    }

    @Test
    public void shouldReturnReadAlerts() {
        final String testPatientId = "testPatientId";
        final String testClinicId = "testClinicId";


        final Clinic clinic = new Clinic() {{
            setId(testClinicId);
        }};
        List<Patient> clinicPatients = new ArrayList<Patient>() {{
            add(new Patient() {{
                setClinic(clinic);
                setPatientId(testPatientId);
                setId(testPatientId);
            }});
        }};
        List<Alert> alerts = new ArrayList<Alert>() {{
            add(new Alert(testPatientId, AlertType.MEDIUM, AlertStatus.READ, 2, null));
            add(new Alert(testPatientId, AlertType.MEDIUM, AlertStatus.READ, 3, null));
            add(new Alert(testPatientId, AlertType.MEDIUM, AlertStatus.READ, 3, null));
            add(new Alert(testPatientId, AlertType.MEDIUM, AlertStatus.READ, 3, null));
        }};

        when(allPatients.findByClinic(testClinicId)).thenReturn(clinicPatients);
        when(alertService.getBy(testPatientId, null, AlertStatus.READ, null, 100)).thenReturn(alerts);

        List<PatientAlert> readAlertsForClinic = patientAlertService.getReadAlertsForClinic(testClinicId);

        assertEquals(alerts.size(), readAlertsForClinic.size());
    }

    @Test
    public void shouldCreateSymptomReportingAlert() {
        final String testPatientId = "testPatientId";
        final String symptomReported = "some ugly rash";
        final String adviceGiven = "have a bath";

        final ArgumentMatcher<Alert> alertArgumentMatcher = new ArgumentMatcher<Alert>() {
            @Override
            public boolean matches(Object testAlert) {
                Alert alert = (Alert) testAlert;
                return alert.getAlertType().equals(AlertType.MEDIUM)
                        && alert.getExternalId().equals(testPatientId)
                        && (alert.getPriority() == 2)
                        && alert.getStatus().equals(AlertStatus.NEW)
                        && alert.getData().get(PatientAlert.SYMPTOMS_ALERT_STATUS).equals(SymptomsAlertStatus.Open.name())
                        && alert.getDescription().equals(symptomReported)
                        && alert.getData().get(PatientAlert.PATIENT_ALERT_TYPE).equals(PatientAlertType.SymptomReporting.name())
                        && alert.getName().equals(adviceGiven);
            }
        };
        patientAlertService.createAlert(testPatientId, 2, adviceGiven, symptomReported, PatientAlertType.SymptomReporting, new HashMap<String, String>());
        verify(alertService).createAlert(argThat(alertArgumentMatcher));
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

        final ArgumentMatcher<Alert> alertArgumentMatcher = new ArgumentMatcher<Alert>() {
            @Override
            public boolean matches(Object testAlert) {
                Alert alert = (Alert) testAlert;
                return alert.getAlertType().equals(AlertType.MEDIUM)
                        && alert.getExternalId().equals(testPatientId)
                        && (alert.getPriority() == 2)
                        && alert.getStatus().equals(AlertStatus.NEW)
                        && alert.getData().get(PatientAlert.SYMPTOMS_ALERT_STATUS) == null
                        && alert.getDescription().equals(symptomReported)
                        && alert.getData().get(PatientAlert.PATIENT_ALERT_TYPE).equals(PatientAlertType.AppointmentReminder.name())
                        && alert.getName().equals(adviceGiven);
            }
        };
        patientAlertService.createAlert(testPatientId, 2, adviceGiven, symptomReported, PatientAlertType.AppointmentReminder, new HashMap<String, String>());
        verify(alertService).createAlert(argThat(alertArgumentMatcher));
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

}
