package org.motechproject.tama.web;

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
import org.motechproject.tama.web.view.AlertFilter;
import org.springframework.ui.Model;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Date;
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

    private PatientAlerts patientAlerts;
    private Alert alert;
    private final String USER_NAME = "userName";

    @Before
    public void setUp() {
        initMocks(this);
        clinicId = "loggedInClinicId";
        patientAlerts = new PatientAlerts();
        alert = new Alert("externalId", null, null, 0, null);

        when(request.getSession()).thenReturn(session);
        when(session.getAttribute(LoginSuccessHandler.LOGGED_IN_USER)).thenReturn(authenticatedUser);
        when(authenticatedUser.getClinicId()).thenReturn(clinicId);
        when(authenticatedUser.getUsername()).thenReturn(USER_NAME);
        alertsController = new AlertsController(patientAlertService);
    }

    @Test
    public void shouldSetUnreadAlertsForDisplay() {
        AlertFilter filter = new AlertFilter().setPatientId("patientId").setAlertStatus(AlertFilter.STATUS_UNREAD).setAlertType(PatientAlertType.AdherenceInRed.toString()).setStartDate(new Date());

        when(patientAlertService.getUnreadAlertsFor(clinicId, filter.getPatientId(), PatientAlertType.AdherenceInRed, filter.getStartDateTime(), filter.getEndDateTime())).thenReturn(patientAlerts);
        assertEquals("alerts/list", alertsController.list(filter, uiModel, request));
        verify(uiModel, times(1)).addAttribute("alerts", patientAlerts);
    }

    @Test
    public void shouldSetAllAlertsForDisplay() {
        AlertFilter filter = new AlertFilter().setPatientId("patientId").setAlertStatus(AlertFilter.STATUS_ALL).setAlertType(PatientAlertType.AdherenceInRed.toString()).setStartDate(new Date());

        when(patientAlertService.getAllAlertsFor(clinicId, filter.getPatientId(), PatientAlertType.AdherenceInRed, filter.getStartDateTime(), filter.getEndDateTime())).thenReturn(patientAlerts);
        assertEquals("alerts/list", alertsController.list(filter, uiModel, request));
        verify(uiModel, times(1)).addAttribute("alerts", patientAlerts);
    }

    @Test
    public void shouldSetFilteredReadAlertsForDisplay() {
        PatientAlertType patientAlertType = PatientAlertType.AdherenceInRed;
        AlertFilter filter = new AlertFilter().setPatientId("patientId").setAlertStatus(AlertFilter.STATUS_READ).setAlertType(patientAlertType.toString()).setEndDate(new Date());
        PatientAlerts filteredReadAlerts = patientAlerts;

        when(patientAlertService.getReadAlertsFor(clinicId, "patientId", patientAlertType, filter.getStartDateTime(), filter.getEndDateTime())).thenReturn(filteredReadAlerts);
        assertEquals("alerts/list", alertsController.list(filter, uiModel, request));
        verify(uiModel, times(1)).addAttribute("alerts", filteredReadAlerts);
    }

    @Test
    public void shouldSetAlertForDisplay() {
        alert.setData(new HashMap<String, String>() {{
            put(PatientAlert.PATIENT_ALERT_TYPE, PatientAlertType.SymptomReporting.name());
        }});
        PatientAlert patientAlert = new PatientAlert();
        patientAlert.setAlert(alert);

        when(patientAlertService.readAlert(alertId, USER_NAME)).thenReturn(patientAlert);
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
        when(patientAlertService.readAlert(alertId, USER_NAME)).thenReturn(patientAlert);
        assertEquals("alerts/update" + PatientAlertType.SymptomReporting.name(), alertsController.updateForm(alertId, uiModel, request));
    }

    @Test
    public void shouldUpdate() {
        alertsController.update(uiModel, alertId, symptomsAlertStatus, notes, doctorsNotes, type, request);
        verify(patientAlertService).updateAlertData(alertId, symptomsAlertStatus, notes, doctorsNotes, type, USER_NAME);
    }

    @Test
    public void shouldReturnUpdateFormWhenUpdateUnSuccessful() {
        alert.setData(new HashMap<String, String>() {{
            put(PatientAlert.PATIENT_ALERT_TYPE, PatientAlertType.SymptomReporting.name());
        }});
        PatientAlert patientAlert = new PatientAlert();
        patientAlert.setAlert(alert);
        when(patientAlertService.readAlert(alertId, USER_NAME)).thenReturn(patientAlert);
        alertsController.update(uiModel, alertId, symptomsAlertStatus, notes, doctorsNotes, type, request);
        assertEquals("alerts/update" + PatientAlertType.SymptomReporting.name(), alertsController.updateForm(alertId, uiModel, request));
    }
}
