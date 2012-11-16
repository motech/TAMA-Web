package org.motechproject.tama.security.profiles;

import org.motechproject.tama.refdata.domain.Analyst;
import org.motechproject.tama.refdata.repository.AllAnalysts;
import org.motechproject.tama.security.AuthenticatedUser;
import org.motechproject.tama.security.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AnalystSecurityGroup extends AbstractSecurityGroup {

    @Autowired
    private AllAnalysts allAnalysts;

    public AnalystSecurityGroup() {
        add(Role.ANALYST);
    }

    public AnalystSecurityGroup(AllAnalysts allAnalysts) {
        this();
        this.allAnalysts = allAnalysts;
    }

    @Override
    public AuthenticatedUser getAuthenticatedUser(String username, String password) {
        Analyst analyst = allAnalysts.findByUserNameAndPassword(username, password);
        if (analyst == null) return null;
        return userFor(analyst);
    }
}
