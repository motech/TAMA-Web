package org.motechproject.tama.security;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.motechproject.tama.security.profiles.SecurityGroup;
import org.motechproject.tama.security.repository.AllTAMAEvents;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.web.authentication.WebAuthenticationDetails;

import java.util.Arrays;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;


public class AuthenticationProviderTest {

    private AuthenticationProvider authenticationProvider;
    @Mock
    private SecurityGroup group;
    @Mock
    private UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken;
    @Mock
    private AllTAMAEvents allTAMAEvents;
    @Mock
    private WebAuthenticationDetails details;

    @Before
    public void setUp() {
        initMocks(this);
        authenticationProvider = new AuthenticationProvider(Arrays.asList(group), allTAMAEvents);
        when(allTAMAEvents.newLoginEvent(Matchers.<String>any(), Matchers.<String>any(), Matchers.<String>any(), Matchers.<String>any())).thenReturn("loginEventDocumentId");
        when(allTAMAEvents.newLogoutEvent(Matchers.<String>any(), Matchers.<String>any(), Matchers.<String>any())).thenReturn("logoutEventDocumentId");
        when(usernamePasswordAuthenticationToken.getDetails()).thenReturn(details);
        when(details.getRemoteAddress()).thenReturn("127.0.0.1");
        when(details.getSessionId()).thenReturn("sessionId");

    }

    @Test(expected = BadCredentialsException.class)
    public void shouldThrowMissingPasswordExceptionWhenPasswordIsEmpty() {
        String userName = "jack";
        when(usernamePasswordAuthenticationToken.getCredentials()).thenReturn("");

        authenticationProvider.retrieveUser(userName, usernamePasswordAuthenticationToken);
    }

    @Test(expected = BadCredentialsException.class)
    public void shouldThrowMissingPasswordExceptionWhenPasswordIsNull() {
        String userName = "jack";
        when(usernamePasswordAuthenticationToken.getCredentials()).thenReturn(null);

        authenticationProvider.retrieveUser(userName, usernamePasswordAuthenticationToken);
    }

    @Test
    public void shouldReturnAnAuthenticUser() {
        String username = "jack";
        String password = "samurai";
        AuthenticatedUser expectedAuthenticatedUser = mock(AuthenticatedUser.class);
        when(usernamePasswordAuthenticationToken.getCredentials()).thenReturn(password);
        when(group.getAuthenticatedUser(username, password)).thenReturn(expectedAuthenticatedUser);

        AuthenticatedUser authenticatedUser = (AuthenticatedUser) authenticationProvider.retrieveUser(username, usernamePasswordAuthenticationToken);
        assertEquals(expectedAuthenticatedUser, authenticatedUser);
    }


    @Test(expected = BadCredentialsException.class)
    public void shouldThrowExceptionForUserNotFound() {
        String username = "jack";
        String password = "samurai";
        when(usernamePasswordAuthenticationToken.getCredentials()).thenReturn(password);
        when(group.getAuthenticatedUser(username, password)).thenReturn(null);

        authenticationProvider.retrieveUser(username, usernamePasswordAuthenticationToken);
    }

    @Test
    public void shouldLogSuccessfulLoginEvent() {
        String username = "jack";
        String password = "samurai";
        AuthenticatedUser expectedAuthenticatedUser = mock(AuthenticatedUser.class);
        when(usernamePasswordAuthenticationToken.getCredentials()).thenReturn(password);
        when(group.getAuthenticatedUser(username, password)).thenReturn(expectedAuthenticatedUser);

        authenticationProvider.retrieveUser(username, usernamePasswordAuthenticationToken);

        verify(allTAMAEvents).newLoginEvent("jack", "127.0.0.1", "sessionId", "Success");
    }

    @Test(expected = BadCredentialsException.class)
    public void shouldLogFailedLoginEvent() {
        String username = "jack";
        String password = "samurai";
        AuthenticatedUser expectedAuthenticatedUser = mock(AuthenticatedUser.class);
        when(usernamePasswordAuthenticationToken.getCredentials()).thenReturn(password);
        when(group.getAuthenticatedUser(username, password)).thenReturn(null);

        authenticationProvider.retrieveUser(username, usernamePasswordAuthenticationToken);

        verify(allTAMAEvents).newLoginEvent("jack", "127.0.0.1", "sessionId", "Failure");
    }

}
