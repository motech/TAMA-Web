package org.motechproject.tama.security;

import org.motechproject.tama.domain.TAMAUser;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.List;

public class AuthenticatedUser extends User {
    private TAMAUser tamaUser;

    public AuthenticatedUser(List<GrantedAuthority> authorities, TAMAUser user) {
        super(user.getUsername(), user.getPassword(), true, true, true, true, authorities);
        this.tamaUser = user;
    }

    public String getName() {
        return tamaUser.getName();
    }

    public TAMAUser getTAMAUser(){
        return tamaUser;
    }

    public String getClinicId() {
        return tamaUser.getClinicId();
    }

    public String getClinicName() {
        return tamaUser.getClinicName();
    }


    public void setPassword(String newPassword) {
        tamaUser.setPassword(newPassword);
    }
}
