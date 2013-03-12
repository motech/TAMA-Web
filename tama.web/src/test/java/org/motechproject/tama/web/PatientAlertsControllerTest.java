package org.motechproject.tama.web;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.tama.patient.builder.PatientBuilder;
import org.motechproject.tama.patient.domain.Patient;
import org.motechproject.tama.patient.repository.AllPatients;
import org.motechproject.tama.security.AuthenticatedUser;
import org.motechproject.tama.security.LoginSuccessHandler;
import org.motechproject.tama.web.model.PatientViewModel;
import org.motechproject.tama.web.view.AlertFilter;
import org.springframework.ui.Model;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

public class PatientAlertsControllerTest {

    @Mock
    private AlertsController altersController;
    @Mock
    private Model uiModel;
    @Mock
    private HttpServletRequest request;
    @Mock
    private AllPatients allPatients;

    private Patient patient;
    private PatientViewModel patientViewModel;

    private PatientAlertsController patientAltersController;

    @Before
    public void setup() {
        initMocks(this);
        patient = PatientBuilder.startRecording().withDefaults().withId("id").build();
        patientViewModel = new PatientViewModel(patient);

        when(allPatients.findByIdAndClinicId(anyString(), anyString())).thenReturn(patient);
        setupSession();
        patientAltersController = new PatientAlertsController(altersController, allPatients);
    }

    @Test
    public void shouldFilterAllAltersForPatient() {
        String patientId = "patientId";

        when(altersController.list(any(AlertFilter.class), eq(uiModel), eq(request))).thenReturn("alerts/list");
        String view = patientAltersController.list(patientId, uiModel, request);
        verify(altersController).list(any(AlertFilter.class), eq(uiModel), eq(request));
        assertEquals("patients/alerts/list", view);
    }

    private void setupSession() {
        HttpSession httpSession = mock(HttpSession.class);
        AuthenticatedUser user = mock(AuthenticatedUser.class);

        when(request.getSession()).thenReturn(httpSession);
        when(httpSession.getAttribute(LoginSuccessHandler.LOGGED_IN_USER)).thenReturn(user);
    }

    @After
    public void tearDown() {
        verify(uiModel).addAttribute("patient", patientViewModel);
    }
}
