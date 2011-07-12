package org.motechproject.tama.security.profiles;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.tama.domain.Clinician;
import org.motechproject.tama.repository.Clinicians;
import org.motechproject.tama.security.AuthenticatedUser;
import org.motechproject.tama.security.Role;

import java.util.Arrays;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class ClinicianSecurityGroupTest extends SecurityGroupTest{
    private ClinicianSecurityGroup group;
    @Mock
    private Clinicians clinicians;

    @Before
    public void setUp() {
        initMocks(this);
        group = new ClinicianSecurityGroup(clinicians);
    }

    @Test
    public void shouldReturnAuthenticatedUserWhenClinicianWithUsernameAndPasswordIsFound() {
        String username = "username";
        String password = "password";
        String marker = "123";
        Clinician clinician = mock(Clinician.class);

        when(clinician.getClinicId()).thenReturn(marker);
        when(clinician.getUsername()).thenReturn(username);
        when(clinician.getPassword()).thenReturn(password);
        when(clinicians.findByUserNameAndPassword(username, password)).thenReturn(clinician);

        AuthenticatedUser authenticatedUser = group.getAuthenticatedUser(username, password);

        assertEquals(marker, authenticatedUser.marker());
        assertUsernameAndPassword(username, password, authenticatedUser);
        assertUserAccount(authenticatedUser);
        assertUserAuthorities(authenticatedUser.getAuthorities(), Arrays.asList(Role.CLINICIAN_STUDY_NURSE, Role.CLINICIAN_DOCTOR));
    }

}
