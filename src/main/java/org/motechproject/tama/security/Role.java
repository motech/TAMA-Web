package org.motechproject.tama.security;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.GrantedAuthorityImpl;

public enum Role {
    CLINICIAN_DOCTOR,
    CLINICIAN_STUDY_NURSE,
    ADMIN;

    public GrantedAuthority authority() {
        return new GrantedAuthorityImpl(name());
    }

}
