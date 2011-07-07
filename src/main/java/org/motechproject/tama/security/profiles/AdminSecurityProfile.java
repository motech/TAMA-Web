package org.motechproject.tama.security.profiles;

import org.motechproject.tama.security.Role;
import org.springframework.security.core.GrantedAuthority;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class AdminSecurityProfile extends AbstractProfile {
    public static final String ADMIN = "admin";
    public static final String PASSWORD = "password";

    public AdminSecurityProfile() {
        addRoles(Role.ADMIN, Role.CLINICIAN_DOCTOR, Role.CLINICIAN_STUDY_NURSE);
    }

    @Override
    protected boolean authenticate(String username, String password) {
        return ADMIN.equals(username) && PASSWORD.equals(password);
    }
}
