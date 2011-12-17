package org.motechproject.tama.security;

import org.apache.commons.lang.StringUtils;
import org.motechproject.tama.security.profiles.SecurityGroup;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.AbstractUserDetailsAuthenticationProvider;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;

public class AuthenticationProvider extends AbstractUserDetailsAuthenticationProvider {
    public static final String PLEASE_ENTER_PASSWORD = "Please enter password";
    public static final String USER_NOT_FOUND = "The username or password you entered is incorrect";

    private List<SecurityGroup> groups;

    public AuthenticationProvider(List<SecurityGroup> groups) {
        this.groups = groups;
    }

    @Override
    protected void additionalAuthenticationChecks(UserDetails userDetails, UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken) throws AuthenticationException {
    }

    @Override
    protected UserDetails retrieveUser(String username, UsernamePasswordAuthenticationToken authentication) throws AuthenticationException {
        String password = (String) authentication.getCredentials();
        if (StringUtils.isEmpty(password)) throw new BadCredentialsException(PLEASE_ENTER_PASSWORD);
        for (SecurityGroup group : groups) {
            AuthenticatedUser authenticatedUser = group.getAuthenticatedUser(username, password);
            if (authenticatedUser != null) return authenticatedUser;
        }
        throw new BadCredentialsException(USER_NOT_FOUND);
    }
}
