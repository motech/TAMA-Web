package org.motechproject.tama.security.profiles;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.tama.domain.Clinician;
import org.motechproject.tama.repository.Clinicians;
import org.motechproject.tama.security.Role;
import org.springframework.security.core.GrantedAuthority;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static org.junit.Assert.assertArrayEquals;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class ClinicianSecurityProfileTest {
    private ClinicianSecurityProfile profile;

    @Mock
    private Clinicians clinicians;

    @Before
    public void setUp() {
        initMocks(this);
        profile = new ClinicianSecurityProfile(clinicians);
    }

    @Test
    public void shouldReturnTrueWhenClinicianWithUsernameAndPasswordIsFound() {
        String username = "username";
        String password = "password";
        when(clinicians.findByUserNameAndPassword(username, password)).thenReturn(new Clinician());
        assertTrue(profile.authenticate(username, password));
    }

    @Test
    public void shouldReturnFalseWhenClinicianWithUserNameAndPasswordIsNotFound() {
        String username = "username";
        String password = "password";
        when(clinicians.findByUserNameAndPassword(username, password)).thenReturn(null);
        assertFalse(profile.authenticate(username, password));
    }

    @Test
    public void shouldProvideProperAuthoritiesForClinician() {
        String username = "username";
        String password = "password";
        when(clinicians.findByUserNameAndPassword(username, password)).thenReturn(new Clinician());
        List<GrantedAuthority> authorities = profile.authoritiesFor(username, password);
        List<GrantedAuthority> expectedAuthorities = Arrays.asList(Role.CLINICIAN_DOCTOR.authority(), Role.CLINICIAN_STUDY_NURSE.authority());
        assertArrayEquals(expectedAuthorities.toArray(), authorities.toArray());
    }

    @Test
    public void shouldProvideEmptyAuthoritiesWhenClinicianIsNotFound() {
        String username = "username";
        String password = "password";
        when(clinicians.findByUserNameAndPassword(username, password)).thenReturn(null);
        List<GrantedAuthority> authorities = profile.authoritiesFor(username, password);
        assertTrue(authorities.isEmpty());
    }
}
