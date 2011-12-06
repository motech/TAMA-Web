package org.motechproject.tama.web;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.server.alerts.domain.Alert;
import org.motechproject.tama.security.AuthenticatedUser;
import org.motechproject.tama.security.LoginSuccessHandler;
import org.motechproject.tamacallflow.service.PatientAlertService;
import org.motechproject.tamadomain.domain.PatientAlert;
import org.motechproject.tamadomain.domain.PatientAlertType;
import org.motechproject.tamadomain.domain.PatientAlerts;
import org.springframework.ui.Model;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.HashMap;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

public class AlertsControllerTest {
    @Mock
    private Model uiModel;
    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpSession session;
    @Mock
    private AuthenticatedUser authenticatedUser;
    @Mock
    private PatientAlertService patientAlertService;

    private AlertsController alertsController;
    private String clinicId;

    private String alertId = "alertId";
    private String symptomsAlertStatus = "symptomsAlertStatus";
    private String notes = "notes";
    private String doctorsNotes = "doctorNotes";
    private String type = "type";

    private PatientAlerts patientAlerts = new PatientAlerts();
    private Alert alert;

    @Before
    public void setUp() {
        initMocks(this);
        clinicId = "loggedInClinicId";
        alert = new Alert("externalId", null, null, 0, null);
        when(authenticatedUser.getClinicId()).thenReturn(clinicId);
        when(request.getSession()).thenReturn(session);
        when(session.getAttribute(LoginSuccessHandler.LOGGED_IN_USER)).thenReturn(authenticatedUser);
        alertsController = new AlertsController(patientAlertService);
    }

    @Test
    public void shouldSetUnreadAlertsForDisplay() {
        when(patientAlertService.getUnreadAlertsForClinic(clinicId)).thenReturn(patientAlerts);
        assertEquals("alerts/unread", alertsController.unread(uiModel, request));
        verify(uiModel, times(1)).addAttribute("alerts", patientAlerts);
    }

    @Test
    public void shouldSetReadAlertsForDisplay() {
        when(patientAlertService.getReadAlertsForClinic(clinicId)).thenReturn(patientAlerts);
        assertEquals("alerts/read", alertsController.read(uiModel, request));
        verify(uiModel, times(1)).addAttribute("alerts", patientAlerts);
    }

    @Test
    public void shouldSetAlertForDisplay() {
        alert.setData(new HashMap<String, String>() {{
            put(PatientAlert.PATIENT_ALERT_TYPE, PatientAlertType.SymptomReporting.name());
        }});
        PatientAlert patientAlert = new PatientAlert();
        patientAlert.setAlert(alert);
        when(patientAlertService.getPatientAlert(alertId)).thenReturn(patientAlert);
        assertEquals("alerts/show" + PatientAlertType.SymptomReporting.name(), alertsController.show(alertId, uiModel, request));
        verify(uiModel, times(1)).addAttribute("alertInfo", patientAlert);
    }

    @Test
    public void shouldDisplayTheUpdateForm() {
        alert.setData(new HashMap<String, String>() {{
            put(PatientAlert.PATIENT_ALERT_TYPE, PatientAlertType.SymptomReporting.name());
        }});
        PatientAlert patientAlert = new PatientAlert();
        patientAlert.setAlert(alert);
        when(patientAlertService.getPatientAlert(alertId)).thenReturn(patientAlert);
        assertEquals("alerts/update" + PatientAlertType.SymptomReporting.name(), alertsController.updateForm(alertId, uiModel, request));
    }

    @Test
    public void shouldUpdate() {
        alertsController.update(uiModel, alertId, symptomsAlertStatus, notes, doctorsNotes, type, request);
        verify(patientAlertService).updateAlert(alertId, symptomsAlertStatus, notes, doctorsNotes, type);
    }

    @Test
    public void shouldReturnUpdateFormWhenUpdateUnSuccessful() {
        alert.setData(new HashMap<String, String>() {{
            put(PatientAlert.PATIENT_ALERT_TYPE, PatientAlertType.SymptomReporting.name());
        }});
        PatientAlert patientAlert = new PatientAlert();
        patientAlert.setAlert(alert);
        when(patientAlertService.getPatientAlert(alertId)).thenReturn(patientAlert);
        alertsController.update(uiModel, alertId, symptomsAlertStatus, notes, doctorsNotes, type, request);
        assertEquals("alerts/update" + PatientAlertType.SymptomReporting.name(), alertsController.updateForm(alertId, uiModel, request));
    }

}
