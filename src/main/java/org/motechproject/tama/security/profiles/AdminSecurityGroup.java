package org.motechproject.tama.security.profiles;

import org.motechproject.tama.security.AuthenticatedUser;
import org.motechproject.tama.security.Role;

public class AdminSecurityGroup extends AbstractSecurityGroup {
    public static final String ADMIN = "admin";
    public static final String PASSWORD = "password";
    public static final String TAMA_ADMIN = "tama_admin";

    public AdminSecurityGroup() {
        add(Role.ADMIN);
    }

    @Override
    public AuthenticatedUser getAuthenticatedUser(String username, String password) {
        if (ADMIN.equals(username) && PASSWORD.equals(password))
            return createUser(username, password, TAMA_ADMIN);
        return null;
    }
}
