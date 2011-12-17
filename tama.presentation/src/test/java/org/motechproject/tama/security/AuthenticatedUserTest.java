package org.motechproject.tama.security;

import org.junit.Before;
import org.junit.Test;
import org.motechproject.tama.common.domain.TAMAUser;
import org.motechproject.tama.refdata.domain.Administrator;

import java.util.Collections;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class AuthenticatedUserTest {

    private String username;
    private String password;

    @Before
    public void setUp() {
        username = "username";
        password = "password";
    }

    @Test
    public void shouldDelegateToTamaUser() {
        TAMAUser tamaUser = mock(TAMAUser.class);
        String name = "name";
        String clinicName = "clinicName";
        String clinicId = "clinicId";
        when(tamaUser.getUsername()).thenReturn(username);
        when(tamaUser.getPassword()).thenReturn(password);
        when(tamaUser.getName()).thenReturn(name);
        when(tamaUser.getClinicName()).thenReturn(clinicName);
        when(tamaUser.getClinicId()).thenReturn(clinicId);

        AuthenticatedUser authenticatedUser = new AuthenticatedUser(Collections.EMPTY_LIST, tamaUser);

        assertEquals(clinicId, authenticatedUser.getClinicId());
        assertEquals(name, authenticatedUser.getName());
        assertEquals(clinicName, authenticatedUser.getClinicName());
    }

    @Test
    public void shouldCheckIfUserIsAdmin() {
        TAMAUser tamaUser = mock(Administrator.class);
        when(tamaUser.getUsername()).thenReturn(username);
        when(tamaUser.getPassword()).thenReturn(password);

        AuthenticatedUser authenticatedUser = new AuthenticatedUser(Collections.EMPTY_LIST, tamaUser);
        assertThat(authenticatedUser.isAdministrator(), is(true));
    }
}
