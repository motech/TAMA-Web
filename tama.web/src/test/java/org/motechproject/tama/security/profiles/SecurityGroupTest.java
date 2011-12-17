package org.motechproject.tama.security.profiles;

import org.motechproject.tama.security.AuthenticatedUser;
import org.motechproject.tama.security.Role;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

public class SecurityGroupTest {
    protected void assertUserAuthorities(Collection<GrantedAuthority> authorities, Collection<Role> expectedRoles) {
        for (GrantedAuthority authority : authorities) {
            boolean contained = false;
            for (Role expectedRole : expectedRoles) {
                if (authority.getAuthority().equals(expectedRole.name()))
                    contained = true;
            }
            assertTrue(contained);
        }
    }

    protected void assertUserAccount(AuthenticatedUser authenticatedUser) {
        assertTrue(authenticatedUser.isAccountNonExpired());
        assertTrue(authenticatedUser.isAccountNonLocked());
        assertTrue(authenticatedUser.isCredentialsNonExpired());
        assertTrue(authenticatedUser.isEnabled());
    }

    protected void assertUsernameAndPassword(String username, String password, AuthenticatedUser authenticatedUser) {
        assertEquals(username, authenticatedUser.getUsername());
        assertEquals(password, authenticatedUser.getPassword());
    }
}
