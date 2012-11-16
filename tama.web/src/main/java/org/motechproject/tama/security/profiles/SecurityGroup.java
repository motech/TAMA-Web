package org.motechproject.tama.security.profiles;

import org.motechproject.tama.security.AuthenticatedUser;

public interface SecurityGroup {

    AuthenticatedUser getAuthenticatedUser(String username, String password);

}
