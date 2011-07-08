package org.motechproject.tama.security;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.tama.security.profiles.SecurityGroup;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import java.util.Arrays;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;


public class AuthenticationProviderTest {

    private AuthenticationProvider authenticationProvider;
    @Mock
    private SecurityGroup group;
    @Mock
    private UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken;

    @Before
    public void setUp() {
        initMocks(this);
        authenticationProvider = new AuthenticationProvider(Arrays.asList(group));
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
}
