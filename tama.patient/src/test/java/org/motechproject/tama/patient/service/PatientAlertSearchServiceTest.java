package org.motechproject.tama.patient.service;

import org.apache.commons.collections.CollectionUtils;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentMatcher;
import org.mockito.Mock;
import org.motechproject.server.alerts.domain.Alert;
import org.motechproject.server.alerts.domain.AlertCriteria;
import org.motechproject.server.alerts.domain.AlertStatus;
import org.motechproject.server.alerts.domain.AlertType;
import org.motechproject.server.alerts.service.AlertService;
import org.motechproject.tama.patient.builder.PatientBuilder;
import org.motechproject.tama.patient.domain.Patient;
import org.motechproject.tama.patient.domain.PatientAlerts;

import java.util.ArrayList;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import static org.mockito.Matchers.argThat;
import static org.mockito.Matchers.same;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class PatientAlertSearchServiceTest {

    @Mock
    private AlertService alertService;
    private PatientAlertSearchService patientAlertSearchService;

    @Before
    public void setUp() {
        initMocks(this);
        patientAlertSearchService = new PatientAlertSearchService(alertService);
    }

    @Test
    public void shouldReturnEmptyWhenNoAlertsFound() {
        final String patientId = "patientId";
        List<Patient> patients = new ArrayList<Patient>() {{
            add(PatientBuilder.startRecording().withDefaults().withPatientId(patientId).build());
        }};

        when(alertService.search(new AlertCriteria().byExternalId(same(patientId)))).thenReturn(null);
        assertTrue(CollectionUtils.isEmpty(patientAlertSearchService.search(patients, null)));
    }

    @Test
    public void shouldReturnAllAlerts_ByPatientId() {
        final String testPatientId1 = "testPatientId1";

        final Patient patient1 = new Patient() {{ setPatientId(testPatientId1); setId(testPatientId1); }};
        final List<Alert> alerts = new ArrayList<Alert>() {{
            add(new Alert(testPatientId1, AlertType.MEDIUM, AlertStatus.NEW, 2, null));
            add(new Alert(testPatientId1, AlertType.MEDIUM, AlertStatus.READ, 2, null));
        }};

        when(alertService.search(argThat(new ArgumentMatcher<AlertCriteria>() {
            @Override
            public boolean matches(Object o) {
                return ((AlertCriteria)o).externalId().equals(testPatientId1) && ((AlertCriteria)o).alertStatus() == null;
            }
        }))).thenReturn(alerts);

        PatientAlerts unReadAlertsByPatientId = patientAlertSearchService.search(new ArrayList<Patient>() {{add(patient1);}}, null);
        assertEquals(alerts.size(), unReadAlertsByPatientId.size());
    }
}
