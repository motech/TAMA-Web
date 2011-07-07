package org.motechproject.tama.security.profiles;

import org.springframework.security.core.GrantedAuthority;

import java.util.List;

public interface SecurityProfile {
    public List<GrantedAuthority> authoritiesFor(String username, String password);
}
