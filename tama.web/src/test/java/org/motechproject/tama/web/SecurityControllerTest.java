package org.motechproject.tama.web;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.tama.common.domain.TAMAUser;
import org.motechproject.tama.facility.builder.ClinicianBuilder;
import org.motechproject.tama.facility.domain.Clinician;
import org.motechproject.tama.facility.repository.AllTAMAUsers;
import org.motechproject.tama.security.AuthenticatedUser;
import org.motechproject.tama.security.LoginSuccessHandler;
import org.motechproject.tama.security.repository.AllTAMAEvents;
import org.springframework.ui.Model;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;


public class SecurityControllerTest {

    private SecurityController securityController;
    @Mock
    private AllTAMAUsers allTAMAUsers;
    @Mock
    private AllTAMAEvents allTAMAEvents;
    @Mock
    private Model uiModel;
    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpSession session;

    @Before
    public void setUp() {
        initMocks(this);
        securityController = new SecurityController(allTAMAUsers, allTAMAEvents);
    }

    @Test
    public void testChangePasswordFormShouldRedirectToChangePasswordPage() throws Exception {
        assertEquals("redirect:/changePassword", securityController.changePasswordForm());
    }

    @Test
    public void testChangePasswordWithWrongOldPassword() {
        AuthenticatedUser authenticatedUser = mock(AuthenticatedUser.class);

        when(request.getSession()).thenReturn(session);
        when(session.getAttribute(LoginSuccessHandler.LOGGED_IN_USER)).thenReturn(authenticatedUser);
        when(authenticatedUser.getPassword()).thenReturn("oldPassword");

        String viewName = securityController.changePassword("old", "new", uiModel, request);
        assertEquals("changePassword", viewName);
    }

    @Test
    public void testChangePassword_AndLoggingOfEvent() {
        AuthenticatedUser authenticatedUser = mock(AuthenticatedUser.class);
        TAMAUser tamaUser = ClinicianBuilder.startRecording().withDefaults().build();

        when(request.getSession()).thenReturn(session);
        when(session.getAttribute(LoginSuccessHandler.LOGGED_IN_USER)).thenReturn(authenticatedUser);
        when(authenticatedUser.getPassword()).thenReturn("oldPassword");
        when(authenticatedUser.getTAMAUser()).thenReturn(tamaUser);
        when(authenticatedUser.getName()).thenReturn(tamaUser.getName());
        when(authenticatedUser.getUsername()).thenReturn(tamaUser.getUsername());
        when(authenticatedUser.getClinicName()).thenReturn(tamaUser.getClinicName());


        String viewName = securityController.changePassword("oldPassword", "new", uiModel, request);
        verify(authenticatedUser, times(1)).setPassword("new");
        verify(allTAMAUsers, times(1)).update(tamaUser, tamaUser.getUsername());
        verify(allTAMAEvents, times(1)).newChangePasswordEvent(tamaUser.getName(), tamaUser.getClinicName(), tamaUser.getClinicId(), tamaUser.getUsername());
        assertEquals("passwordReset", viewName);
    }

    @Test
    public void shouldNotResetClinicianPassword_WhenNewPasswordAndConfirmPasswordMismatch(){
        String viewName = securityController.resetClinicianPasswordPost("someClinicianId", "newPassword", "newPasswordConfirm", uiModel, request);
        assertEquals("setClinicianPassword", viewName);
        verify(uiModel).addAttribute("clinicianId", "someClinicianId");
        verify(uiModel).addAttribute(eq("errors"), any());
    }

    @Test
    public void shouldResetClinicianPassword_AndLogAsTAMAEvent(){
        Clinician clinician = mock(Clinician.class);
        AuthenticatedUser authenticatedUser = mock(AuthenticatedUser.class);

        when(request.getSession()).thenReturn(session);
        when(session.getAttribute(LoginSuccessHandler.LOGGED_IN_USER)).thenReturn(authenticatedUser);
        when(authenticatedUser.getUsername()).thenReturn("admin");

        when(clinician.getName()).thenReturn("clinician0");
        when(clinician.getClinicName()).thenReturn("clinic0");
        when(clinician.getClinicId()).thenReturn("clinic_0_id");

        when(allTAMAUsers.getClinician("someClinicianId")).thenReturn(clinician);

        String viewName = securityController.resetClinicianPasswordPost("someClinicianId", "newPassword", "newPassword", uiModel, request);
        assertEquals("setClinicianPasswordSuccess", viewName);
        verify(clinician).setPassword("newPassword");
        verify(allTAMAUsers).update(clinician, "admin");
        verify(allTAMAEvents, times(1)).newChangePasswordEvent("clinician0", "clinic0", "clinic_0_id", "admin");
    }
}
