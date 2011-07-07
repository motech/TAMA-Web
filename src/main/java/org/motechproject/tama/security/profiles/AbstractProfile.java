package org.motechproject.tama.security.profiles;

import org.motechproject.tama.security.Role;
import org.springframework.security.core.GrantedAuthority;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class AbstractProfile implements SecurityProfile {
    protected List<Role> roles = new ArrayList<Role>();

    @Override
    public List<GrantedAuthority> authoritiesFor(String username, String password) {
        if (authenticate(username, password)) {
            List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
            for (Role role : roles) authorities.add(role.authority());
            return authorities;
        }
        return Collections.EMPTY_LIST;
    }

    protected abstract boolean authenticate(String username, String password);

    protected void addRoles(Role... roles) {
        for (Role role : roles) this.roles.add(role);
    }

}
