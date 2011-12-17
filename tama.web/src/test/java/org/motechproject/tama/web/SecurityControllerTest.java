package org.motechproject.tama.web;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.tama.common.domain.TAMAUser;
import org.motechproject.tama.facility.builder.ClinicianBuilder;
import org.motechproject.tama.facility.repository.AllTAMAUsers;
import org.motechproject.tama.security.AuthenticatedUser;
import org.motechproject.tama.security.LoginSuccessHandler;
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

    @Before
    public void setUp() {
        initMocks(this);
        securityController = new SecurityController(allTAMAUsers);
    }

    @Test
    public void testChangePasswordFormShouldRedirectToChangePasswordPage() throws Exception {
        assertEquals("redirect:/changePassword", securityController.changePasswordForm());
    }

    @Test
    public void testChangePasswordWithWrongOldPassword() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        Model uiModel = mock(Model.class);
        HttpSession session = mock(HttpSession.class);
        AuthenticatedUser authenticatedUser = mock(AuthenticatedUser.class);

        when(request.getSession()).thenReturn(session);
        when(session.getAttribute(LoginSuccessHandler.LOGGED_IN_USER)).thenReturn(authenticatedUser);
        when(authenticatedUser.getPassword()).thenReturn("oldPassword");


        String viewName = securityController.changePassword("old", "new", "new", uiModel, request);
        assertEquals("changePassword", viewName);
    }

    @Test
    public void testChangePassword() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        Model uiModel = mock(Model.class);
        HttpSession session = mock(HttpSession.class);
        AuthenticatedUser authenticatedUser = mock(AuthenticatedUser.class);
        TAMAUser tamaUser = ClinicianBuilder.startRecording().withDefaults().build();

        when(request.getSession()).thenReturn(session);
        when(session.getAttribute(LoginSuccessHandler.LOGGED_IN_USER)).thenReturn(authenticatedUser);
        when(authenticatedUser.getPassword()).thenReturn("oldPassword");
        when(authenticatedUser.getTAMAUser()).thenReturn(tamaUser);

        String viewName = securityController.changePassword("oldPassword", "new", "new", uiModel, request);
        verify(authenticatedUser, times(1)).setPassword("new");
        verify(allTAMAUsers, times(1)).update(tamaUser);
        assertEquals("passwordReset", viewName);
    }
}
