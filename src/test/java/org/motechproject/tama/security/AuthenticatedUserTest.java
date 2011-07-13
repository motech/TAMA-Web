package org.motechproject.tama.security;

import org.junit.Test;
import org.motechproject.tama.domain.TAMAUser;

import java.util.Collections;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class AuthenticatedUserTest {

    @Test
    public void shouldDelegateToTamaUser() {
        TAMAUser tamaUser = mock(TAMAUser.class);
        String username = "username";
        String name = "name";
        String password = "password";
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
}
