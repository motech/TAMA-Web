package org.motechproject.tama.patient.service;

import org.apache.commons.collections.CollectionUtils;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentMatcher;
import org.mockito.Mock;
import org.motechproject.server.alerts.domain.Alert;
import org.motechproject.server.alerts.domain.AlertCriteria;
import org.motechproject.server.alerts.domain.AlertStatus;
import org.motechproject.server.alerts.domain.AlertType;
import org.motechproject.server.alerts.service.AlertService;
import org.motechproject.tama.patient.domain.PatientAlerts;
import org.motechproject.tama.patient.repository.AllPatients;
import org.motechproject.util.DateUtil;

import java.util.ArrayList;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import static org.mockito.Matchers.argThat;
import static org.mockito.Matchers.same;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.mockito.internal.verification.VerificationModeFactory.times;

public class PatientAlertSearchServiceTest {

    @Mock
    private AllPatients allPatients;
    @Mock
    private AlertService alertService;
    private PatientAlertSearchService patientAlertSearchService;
    private String testPatientId1;
    private String testPatientId2;
    private ArrayList<Alert> allAlerts;

    @Before
    public void setUp() {
        initMocks(this);
        testPatientId1 = "patientId1";
        testPatientId2 = "patientId2";
        allAlerts = new ArrayList<Alert>() {{
            add(new Alert(testPatientId1, AlertType.MEDIUM, AlertStatus.NEW, 2, null));
            add(new Alert(testPatientId1, AlertType.MEDIUM, AlertStatus.NEW, 2, null));
            add(new Alert(testPatientId2, AlertType.MEDIUM, AlertStatus.NEW, 2, null));
        }};
        patientAlertSearchService = new PatientAlertSearchService(alertService, allPatients);
    }

    @Test
    public void shouldReturnEmptyWhenNoAlertsFound() {
        final String patientId = "patientId";

        when(alertService.search(new AlertCriteria().byExternalId(same(patientId)))).thenReturn(null);
        assertTrue(CollectionUtils.isEmpty(patientAlertSearchService.search(patientId)));
    }

    @Test
    public void shouldReturnAllAlerts_ByPatientId() {
        allAlerts = new ArrayList<Alert>() {{
            add(new Alert(testPatientId1, AlertType.MEDIUM, AlertStatus.NEW, 2, null));
            add(new Alert(testPatientId1, AlertType.MEDIUM, AlertStatus.NEW, 2, null));
        }};
        when(alertService.search(argThat(new ArgumentMatcher<AlertCriteria>() {
            @Override
            public boolean matches(Object o) {
                return ((AlertCriteria) o).externalId().equals(testPatientId1) && ((AlertCriteria) o).alertStatus() == null;
            }
        }))).thenReturn(allAlerts);

        PatientAlerts unReadAlertsByPatientId = patientAlertSearchService.search(testPatientId1);
        assertEquals(allAlerts.size(), unReadAlertsByPatientId.size());
    }

    @Test
    public void shouldReturnAllAlerts_GivenAPatientIdDateRangeAndAlertStatus() {
        final DateTime fromDate = DateUtil.newDateTime(new LocalDate(2010, 10, 10), 0, 0, 0);
        final DateTime toDate = DateUtil.newDateTime(new LocalDate(2010, 10, 20), 0, 0, 0);

        when(alertService.search(argThat(new ArgumentMatcher<AlertCriteria>() {
            @Override
            public boolean matches(Object o) {
                AlertCriteria criteria = (AlertCriteria) o;
                return criteria.alertStatus().equals(AlertStatus.NEW) && criteria.fromDate().equals(fromDate) && criteria.toDate().equals(toDate);
            }
        }))).thenReturn(allAlerts);
        PatientAlerts patientAlerts = patientAlertSearchService.search(null, fromDate, toDate, AlertStatus.NEW);
        assertEquals(allAlerts.size(), patientAlerts.size());

        verify(allPatients, times(1)).get(testPatientId1);
        verify(allPatients, times(1)).get(testPatientId2);
    }
}
