package org.motechproject.tama.service;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.server.alerts.domain.Alert;
import org.motechproject.server.alerts.domain.AlertStatus;
import org.motechproject.server.alerts.domain.AlertType;
import org.motechproject.server.alerts.service.AlertService;
import org.motechproject.tama.domain.Clinic;
import org.motechproject.tama.domain.Patient;
import org.motechproject.tama.domain.PatientAlert;
import org.motechproject.tama.repository.AllPatients;

import java.util.ArrayList;
import java.util.List;

import static junit.framework.Assert.assertEquals;
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

    @Before
    public void setUp() {
        initMocks(this);
        patientAlertService = new PatientAlertService(allPatients, alertService);
    }

    @Test
    public void shouldReturnCorrectAlert() {
        final String patientId = "patientId";
        final Alert correctAlert = new Alert(patientId, AlertType.MEDIUM, AlertStatus.NEW, 2);
        final String testId = "testId";
        correctAlert.setId(testId);
        correctAlert.setExternalId(testId);

        Patient patient = new Patient() {{
            setId(testId);
            setPatientId(testId);
        }};
        List<Alert> alerts = new ArrayList<Alert>() {{
            add(new Alert("externalId1", AlertType.MEDIUM, AlertStatus.NEW, 2));
            add(new Alert("externalId2", AlertType.MEDIUM, AlertStatus.NEW, 3));
            add(correctAlert);
        }};
        when(alertService.getBy(null, null, null, null, 100)).thenReturn(alerts);
        when(allPatients.get(testId)).thenReturn(patient);

        PatientAlert symptomReportingAlert = patientAlertService.getSymptomReportingAlert(testId);

        assertEquals(testId, symptomReportingAlert.getPatientId());
    }

    @Test
    public void shouldChangeStatusOfAlert() {
        final String patientId = "patientId";
        final Alert correctAlert = new Alert(patientId, AlertType.MEDIUM, AlertStatus.NEW, 2);
        String testId = "testId";
        correctAlert.setId(testId);

        List<Alert> alerts = new ArrayList<Alert>() {{
            add(new Alert("externalId1", AlertType.MEDIUM, AlertStatus.NEW, 2));
            add(new Alert("externalId2", AlertType.MEDIUM, AlertStatus.NEW, 3));
            add(correctAlert);
        }};
        when(alertService.getBy(null, null, null, null, 100)).thenReturn(alerts);

        patientAlertService.getSymptomReportingAlert(testId);

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
            add(new Alert(testPatientId, AlertType.MEDIUM, AlertStatus.NEW, 2));
            add(new Alert(testPatientId, AlertType.MEDIUM, AlertStatus.NEW, 3));
            add(new Alert(testPatientId, AlertType.MEDIUM, AlertStatus.NEW, 3));
            add(new Alert(testPatientId, AlertType.MEDIUM, AlertStatus.NEW, 3));
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
            add(new Alert(testPatientId, AlertType.MEDIUM, AlertStatus.READ, 2));
            add(new Alert(testPatientId, AlertType.MEDIUM, AlertStatus.READ, 3));
            add(new Alert(testPatientId, AlertType.MEDIUM, AlertStatus.READ, 3));
            add(new Alert(testPatientId, AlertType.MEDIUM, AlertStatus.READ, 3));
        }};

        when(allPatients.findByClinic(testClinicId)).thenReturn(clinicPatients);
        when(alertService.getBy(testPatientId, null, AlertStatus.READ, null, 100)).thenReturn(alerts);

        List<PatientAlert> readAlertsForClinic = patientAlertService.getReadAlertsForClinic(testClinicId);

        assertEquals(alerts.size(), readAlertsForClinic.size());

    }

}
