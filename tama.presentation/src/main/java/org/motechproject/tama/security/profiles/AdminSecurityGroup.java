package org.motechproject.tama.security.profiles;

import org.motechproject.tama.security.AuthenticatedUser;
import org.motechproject.tama.security.Role;
import org.motechproject.tamadomain.domain.Administrator;
import org.motechproject.tamadomain.repository.AllAdministrators;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AdminSecurityGroup extends AbstractSecurityGroup {
    @Autowired
    private AllAdministrators allAdministrators;

    public AdminSecurityGroup() {
        add(Role.ADMIN);
    }

    public AdminSecurityGroup(AllAdministrators allAdministrators) {
        this();
        this.allAdministrators = allAdministrators;
    }

    @Override
    public AuthenticatedUser getAuthenticatedUser(String username, String password) {
        Administrator administrator = allAdministrators.findByUserNameAndPassword(username, password);
        if (administrator == null) return null;
        return userFor(administrator);
    }
}
