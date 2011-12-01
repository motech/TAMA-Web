package org.motechproject.tama.security.profiles;

import org.motechproject.tamadomain.domain.TAMAUser;
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

    protected AuthenticatedUser userFor(TAMAUser tamaUser) {
        List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
        for (Role role : roles) authorities.add(role.authority());
        return new AuthenticatedUser(authorities, tamaUser);
    }

}
