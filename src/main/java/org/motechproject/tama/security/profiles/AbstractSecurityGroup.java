package org.motechproject.tama.security.profiles;

import org.motechproject.tama.security.AuthenticatedUser;
import org.motechproject.tama.security.Role;
import org.springframework.security.core.GrantedAuthority;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractSecurityGroup implements SecurityGroup {
    protected List<Role> roles = new ArrayList<Role>();

    protected void add(Role... roles) {
        for (Role role : roles) this.roles.add(role);
    }

    protected List<GrantedAuthority> authorities() {
        List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
        for (Role role : roles) authorities.add(role.authority());
        return authorities;
    }

    protected AuthenticatedUser createUser(String username, String password, String marker) {
        return new AuthenticatedUser(username, password, true, true, true, true, authorities(), marker);
    }


}
