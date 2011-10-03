package org.motechproject.tama.web;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.server.alerts.domain.Alert;
import org.motechproject.tama.domain.PatientAlert;
import org.motechproject.tama.repository.AllSymptomReportingAlerts;
import org.springframework.ui.Model;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.powermock.api.mockito.PowerMockito.when;

public class AlertsControllerTest {
    @Mock
    private Model uiModel;
    @Mock
    private HttpServletRequest request;

    @Mock
    private AllSymptomReportingAlerts allSymptomReportingAlerts;


    private AlertsController alertsController;
    private String clinicId;


    @Before
    public void setUp() {
        initMocks(this);
        clinicId = "loggedInClinicId";
        alertsController = new AlertsController(allSymptomReportingAlerts) {
            protected String loggedInClinic(HttpServletRequest request) {
                return clinicId;
            }
        };
    }

    @Test
    public void shouldSetUnreadAlertsForDisplay() {
        List<PatientAlert> patientAlerts = new ArrayList<PatientAlert>();
        when(allSymptomReportingAlerts.getUnreadAlertsForClinic(clinicId)).thenReturn(patientAlerts);

        String unreadControllerString = alertsController.unread(uiModel, request);

        verify(uiModel, times(1)).addAttribute("alerts", patientAlerts);
        assertEquals("alerts/unread", unreadControllerString);
    }

    @Test
    public void shouldSetReadAlertsForDisplay() {
        List<PatientAlert> patientAlerts = new ArrayList<PatientAlert>();
        when(allSymptomReportingAlerts.getReadAlertsForClinic(clinicId)).thenReturn(patientAlerts);

        String readControllerString = alertsController.read(uiModel, request);

        verify(uiModel, times(1)).addAttribute("alerts", patientAlerts);
        assertEquals("alerts/read", readControllerString);
    }

    @Test
    public void shouldSetAlertForDisplay() {
        String id = "alertId";
        PatientAlert alert = new PatientAlert() ;
        when(allSymptomReportingAlerts.getSymptomReportingAlert(id)).thenReturn(alert);

        String returnString = alertsController.show(id, uiModel, request);

        verify(uiModel, times(1)).addAttribute("alertInfo", alert);
        assertEquals("alerts/show", returnString);


    }
}
