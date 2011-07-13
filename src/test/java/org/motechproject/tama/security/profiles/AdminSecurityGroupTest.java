package org.motechproject.tama.security.profiles;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.tama.domain.Administrator;
import org.motechproject.tama.repository.Administrators;
import org.motechproject.tama.security.AuthenticatedUser;
import org.motechproject.tama.security.Role;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

import java.util.Arrays;

public class AdminSecurityGroupTest extends SecurityGroupTest {
    private AdminSecurityGroup group;
    @Mock
    private Administrators administrators;

    @Before
    public void setUp() {
        initMocks(this);
        group = new AdminSecurityGroup(administrators);
    }

    @Test
    public void shouldAuthenticateBasedOnUsernameAndPassword() {
        String username = "username";
        String password = "password";
        String adminName = "adminName";
        String clinicId = "dummy";
        Administrator administrator = mock(Administrator.class);

        when(administrator.getName()).thenReturn(adminName);
        when(administrator.getUsername()).thenReturn(username);
        when(administrator.getPassword()).thenReturn(password);
        when(administrator.getClinicId()).thenReturn(clinicId);

        when(administrators.findByUserNameAndPassword(username, password)).thenReturn(administrator);

        AuthenticatedUser authenticatedUser = group.getAuthenticatedUser(username, password);

        assertUsernameAndPassword(username, password, authenticatedUser);
        assertEquals(clinicId, authenticatedUser.getClinicId());
        assertUserAccount(authenticatedUser);
        assertUserAuthorities(authenticatedUser.getAuthorities(), Arrays.asList(Role.ADMIN));

    }
}
