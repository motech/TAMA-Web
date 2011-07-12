package org.motechproject.tama.security;

import org.motechproject.tama.domain.TAMAUser;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;
import java.util.List;

public class AuthenticatedUser extends User {
    private String marker;
    private TAMAUser tamaUser;

    public AuthenticatedUser(List<GrantedAuthority> authorities, String marker, TAMAUser user) {
        super(user.getUsername(), user.getPassword(), true, true, true, true, authorities);
        this.tamaUser = user;
        this.marker = marker;
    }

    public String marker() {
        return marker;
    }

    public String getName() {
        return tamaUser.getName();
    }

    public String getClinic() {
        return tamaUser.getClinicName();
    }


}
