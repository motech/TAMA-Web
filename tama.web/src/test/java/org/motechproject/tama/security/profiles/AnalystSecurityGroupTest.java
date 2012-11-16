package org.motechproject.tama.security.profiles;


import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.tama.refdata.domain.Analyst;
import org.motechproject.tama.refdata.repository.AllAnalysts;
import org.motechproject.tama.security.Role;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class AnalystSecurityGroupTest {

    @Mock
    private AllAnalysts allAnalysts;
    private AnalystSecurityGroup analystSecurityGroup;

    @Before
    public void setup() {
        initMocks(this);
        analystSecurityGroup = new AnalystSecurityGroup(allAnalysts);
    }

    @Test
    public void shouldHaveAnalystRole() {
        assertEquals(Role.ANALYST, analystSecurityGroup.roles.get(0));
    }

    @Test
    public void shouldNotHaveAnyOtherRole() {
        assertEquals(1, analystSecurityGroup.roles.size());
    }

    @Test
    public void shouldAuthorizeUserByCredentials() {
        Analyst analyst = new Analyst("Analyst", "userName", "password");
        when(allAnalysts.findByUserNameAndPassword("userName", "password")).thenReturn(analyst);
        assertNotNull(analystSecurityGroup.getAuthenticatedUser("userName", "password"));
    }

    @Test
    public void shouldNotAuthorizeUserWithInvalidCredentials() {
        when(allAnalysts.findByUserNameAndPassword("userName", "password")).thenReturn(null);
        assertNull(analystSecurityGroup.getAuthenticatedUser("userName", "password"));
    }
}
