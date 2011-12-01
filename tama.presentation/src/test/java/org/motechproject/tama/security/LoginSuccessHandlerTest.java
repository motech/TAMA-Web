package org.motechproject.tama.security;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.security.core.Authentication;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

public class LoginSuccessHandlerTest {
    private LoginSuccessHandler handler;
    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;
    @Mock
    private Authentication authentication;
    @Mock
    private HttpSession session;

    @Before
    public void setUp() {
        initMocks(this);
        handler = new LoginSuccessHandler();
    }

    @Test
    public void shouldSetClinicNameAndClinicianNameInSession() throws IOException, ServletException {
        AuthenticatedUser user = mock(AuthenticatedUser.class);
        when(authentication.getPrincipal()).thenReturn(user);
        when(request.getSession()).thenReturn(session);

        handler.onAuthenticationSuccess(request, response, authentication);

        verify(session).setAttribute("loggedInUser", user);
    }
}
