package org.motechproject.tama.security;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.tama.security.profiles.SecurityProfile;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;


public class AuthenticationProviderTest {

    private AuthenticationProvider authenticationProvider;
    @Mock
    private SecurityProfile profile;
    @Mock
    private UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken;

    @Before
    public void setUp() {
        initMocks(this);
        authenticationProvider = new AuthenticationProvider(Arrays.asList(profile));
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
    public void shouldReturnAUserWithProperAuthorities() {
        String username = "jack";
        String password = "samurai";
        GrantedAuthority authority = mock(GrantedAuthority.class);
        List<GrantedAuthority> authorities = Arrays.asList(authority);
        when(usernamePasswordAuthenticationToken.getCredentials()).thenReturn(password);
        when(profile.authoritiesFor(username, password)).thenReturn(authorities);

        UserDetails userDetails = authenticationProvider.retrieveUser(username, usernamePasswordAuthenticationToken);
        verify(profile, times(1)).authoritiesFor(username, password);

        assertEquals(username, userDetails.getUsername());
        assertEquals(password, userDetails.getPassword());
        assertEquals(1, userDetails.getAuthorities().size());
        assertTrue(userDetails.isAccountNonExpired());
        assertTrue(userDetails.isAccountNonLocked());
        assertTrue(userDetails.isCredentialsNonExpired());
        assertTrue(userDetails.isEnabled());
    }

    @Test(expected = BadCredentialsException.class)
    public void shouldThrowExceptionForUserNotFound() {
        String username = "jack";
        String password = "samurai";
        when(usernamePasswordAuthenticationToken.getCredentials()).thenReturn(password);
        when(profile.authoritiesFor(username, password)).thenReturn(Collections.EMPTY_LIST);

        authenticationProvider.retrieveUser(username, usernamePasswordAuthenticationToken);
    }
}
