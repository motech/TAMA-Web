package org.motechproject.tama.security.profiles;

import org.junit.Before;
import org.junit.Test;
import org.motechproject.tama.security.AuthenticatedUser;
import org.motechproject.tama.security.Role;
import org.springframework.security.core.GrantedAuthority;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static org.junit.Assert.assertArrayEquals;

public class AdminSecurityGroupTest extends SecurityGroupTest{

    private AdminSecurityGroup group;

    @Before
    public void setUp() {
        group = new AdminSecurityGroup();
    }

    @Test
    public void shouldAuthenticateBasedOnUsernameAndPassword() {
        AuthenticatedUser authenticatedUser = group.getAuthenticatedUser("admin", "password");
        assertUsernameAndPassword("admin","password",authenticatedUser);
        assertEquals(AdminSecurityGroup.TAMA_ADMIN, authenticatedUser.marker());
        assertUserAccount(authenticatedUser);
        assertUserAuthorities(authenticatedUser.getAuthorities(),Arrays.asList(Role.ADMIN,Role.CLINICIAN_DOCTOR, Role.CLINICIAN_STUDY_NURSE));

    }
}
