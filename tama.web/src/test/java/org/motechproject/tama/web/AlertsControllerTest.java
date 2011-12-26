package org.motechproject.tama.web;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.server.alerts.domain.Alert;
import org.motechproject.tama.patient.domain.PatientAlert;
import org.motechproject.tama.patient.domain.PatientAlertType;
import org.motechproject.tama.patient.domain.PatientAlerts;
import org.motechproject.tama.patient.service.PatientAlertService;
import org.motechproject.tama.security.AuthenticatedUser;
import org.motechproject.tama.security.LoginSuccessHandler;
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
        DateTime startDate = DateTime.now().minusDays(10);
        when(request.getParameter("patientId")).thenReturn("patientId");
        when(request.getParameter("patientAlertType")).thenReturn("");
        when(request.getParameter("startDate")).thenReturn(startDate.toString());
        when(request.getParameter("endDate")).thenReturn("");

        when(patientAlertService.getUnreadAlertsFor(clinicId, "patientId", null, startDate, null)).thenReturn(patientAlerts);
        assertEquals("alerts/unread", alertsController.unread(uiModel, request));
        verify(uiModel, times(1)).addAttribute("alerts", patientAlerts);
    }

    @Test
    public void shouldSetReadAlertsForDisplay() {
        DateTime endDate = DateTime.now().minusDays(5);
        PatientAlertType patientAlertType = PatientAlertType.AdherenceInRed;
        when(request.getParameter("patientId")).thenReturn("patientId");
        when(request.getParameter("patientAlertType")).thenReturn(patientAlertType.name());
        when(request.getParameter("startDate")).thenReturn("");
        when(request.getParameter("endDate")).thenReturn(endDate.toString());

        when(patientAlertService.getReadAlertsFor(clinicId, "patientId", patientAlertType, null, endDate)).thenReturn(patientAlerts);

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
