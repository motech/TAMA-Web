package org.motechproject.tama.security.profiles;

import org.motechproject.tama.domain.Administrator;
import org.motechproject.tama.repository.Administrators;
import org.motechproject.tama.security.AuthenticatedUser;
import org.motechproject.tama.security.Role;
import org.springframework.beans.factory.annotation.Autowired;

public class AdminSecurityGroup extends AbstractSecurityGroup {
    @Autowired
    private Administrators administrators;

    public AdminSecurityGroup() {
        add(Role.ADMIN);
    }

    public AdminSecurityGroup(Administrators administrators) {
        this();
        this.administrators = administrators;
    }

    @Override
    public AuthenticatedUser getAuthenticatedUser(String username, String password) {
        Administrator administrator = administrators.findByUserNameAndPassword(username, password);
        if (administrator == null) return null;
        return createUser(administrator, administrator.getName());
    }
}
