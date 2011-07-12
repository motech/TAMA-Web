package org.motechproject.tama.security;

import org.junit.Test;
import org.motechproject.tama.domain.TAMAUser;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class AuthenticatedUserTest {

    @Test
    public void shouldDelegateToTamaUser() {
        TAMAUser tamaUser = mock(TAMAUser.class);
        when(tamaUser.isAdmin()).thenReturn(true);

        AuthenticatedUser authenticatedUser = new AuthenticatedUser(null, "marker", tamaUser);
        assertTrue(authenticatedUser.isAdmin());
        assertEquals("marker", authenticatedUser.marker());
    }
}
