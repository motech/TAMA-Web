package org.motechproject.tama.security.profiles;

import org.junit.Before;
import org.junit.Test;
import org.motechproject.tama.security.Role;
import org.springframework.security.core.GrantedAuthority;

import java.util.Arrays;
import java.util.List;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static org.junit.Assert.assertArrayEquals;

public class AdminSecurityProfileTest {

    private AdminSecurityProfile profile;

    @Before
    public void setUp() {
        profile = new AdminSecurityProfile();
    }

    @Test
    public void shouldAuthenticateBasedOnUsernameAndPassword() {
        assertTrue(profile.authenticate("admin", "password"));
        assertFalse(profile.authenticate("admins", "bad"));
    }

    @Test
    public void shouldProvideProperAuthoritiesForClinician() {
        List<GrantedAuthority> authorities = profile.authoritiesFor(AdminSecurityProfile.ADMIN, AdminSecurityProfile.PASSWORD);
        List<GrantedAuthority> expectedAuthorities = Arrays.asList(Role.ADMIN.authority(), Role.CLINICIAN_DOCTOR.authority(), Role.CLINICIAN_STUDY_NURSE.authority());
        assertArrayEquals(expectedAuthorities.toArray(), authorities.toArray());
    }
}
