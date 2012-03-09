package org.motechproject.tama.security;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.tama.security.repository.AllAccessEvents;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.WebAuthenticationDetails;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class LogoutSuccessHandlerTest {

    @Mock
    private AllAccessEvents allAccessEvents;
    @Mock
    HttpServletRequest request;
    @Mock
    HttpServletResponse response;
    @Mock
    private WebAuthenticationDetails details;

    private LogoutSuccessHandler logoutSuccessHandler;

    @Before
    public void setUp() {
        initMocks(this);
        logoutSuccessHandler = new LogoutSuccessHandler(allAccessEvents);
    }

    @Test
    public void shouldLogLogoutEvent() throws IOException, ServletException {
        Authentication authentication = mock(Authentication.class);

        when(authentication.getDetails()).thenReturn(details);
        when(authentication.getName()).thenReturn("jack");
        when(details.getRemoteAddress()).thenReturn("127.0.0.1");
        when(details.getSessionId()).thenReturn("sessionId");
        
        logoutSuccessHandler.onLogoutSuccess(request, response, authentication);

        verify(allAccessEvents).newLogoutEvent("jack", "127.0.0.1", "sessionId");

    }
}