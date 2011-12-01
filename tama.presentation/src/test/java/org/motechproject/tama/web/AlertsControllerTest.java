package org.motechproject.tama.web;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.tamadomain.domain.PatientAlert;
import org.motechproject.tamadomain.domain.PatientAlerts;
import org.motechproject.tamacallflow.service.PatientAlertService;
import org.springframework.ui.Model;

import javax.servlet.http.HttpServletRequest;

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
    private PatientAlertService patientAlertService;


    private AlertsController alertsController;
    private String clinicId;


    @Before
    public void setUp() {
        initMocks(this);
        clinicId = "loggedInClinicId";
        alertsController = new AlertsController(patientAlertService) {
            protected String loggedInClinic(HttpServletRequest request) {
                return clinicId;
            }
        };
    }

    @Test
    public void shouldSetUnreadAlertsForDisplay() {
        PatientAlerts patientAlerts = new PatientAlerts();
        when(patientAlertService.getUnreadAlertsForClinic(clinicId)).thenReturn(patientAlerts);

        String unreadControllerString = alertsController.unread(uiModel, request);

        verify(uiModel, times(1)).addAttribute("alerts", patientAlerts);
        assertEquals("alerts/unread", unreadControllerString);
    }

    @Test
    public void shouldSetReadAlertsForDisplay() {
        PatientAlerts patientAlerts = new PatientAlerts();
        when(patientAlertService.getReadAlertsForClinic(clinicId)).thenReturn(patientAlerts);

        String readControllerString = alertsController.read(uiModel, request);

        verify(uiModel, times(1)).addAttribute("alerts", patientAlerts);
        assertEquals("alerts/read", readControllerString);
    }

    @Test
    public void shouldSetAlertForDisplay() {
        String id = "alertId";
        PatientAlert alert = new PatientAlert() ;
        when(patientAlertService.getPatientAlert(id)).thenReturn(alert);

        String returnString = alertsController.show(id, uiModel, request);

        verify(uiModel, times(1)).addAttribute("alertInfo", alert);
        assertEquals("alerts/show", returnString);


    }
}
