package org.motechproject.tama.security;

import org.apache.commons.lang.StringUtils;
import org.motechproject.tama.security.profiles.SecurityGroup;
import org.motechproject.tama.security.repository.AllTAMAEvents;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.AbstractUserDetailsAuthenticationProvider;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetails;

import java.util.List;

public class AuthenticationProvider extends AbstractUserDetailsAuthenticationProvider {

    public static final String PLEASE_ENTER_PASSWORD = "Please enter password";
    public static final String USER_NOT_FOUND = "The username or password you entered is incorrect";
    public static final String FAILURE = "Failure";
    public static final String SUCCESS = "Success";

    private List<SecurityGroup> groups;
    private AllTAMAEvents allTAMAEvents;

    public AuthenticationProvider(List<SecurityGroup> groups, AllTAMAEvents allTAMAEvents) {
        this.groups = groups;
        this.allTAMAEvents = allTAMAEvents;
    }

    @Override
    protected void additionalAuthenticationChecks(UserDetails userDetails, UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken) throws AuthenticationException {
    }

    @Override
    protected UserDetails retrieveUser(String username, UsernamePasswordAuthenticationToken authentication) throws AuthenticationException {
        WebAuthenticationDetails details = (WebAuthenticationDetails) authentication.getDetails();
        String password = (String) authentication.getCredentials();
        String status = FAILURE;
        try {
            if (StringUtils.isEmpty(password)) throw new BadCredentialsException(PLEASE_ENTER_PASSWORD);
            for (SecurityGroup group : groups) {
                AuthenticatedUser authenticatedUser = group.getAuthenticatedUser(username, password);
                if (authenticatedUser != null) {
                    status = SUCCESS;
                    return authenticatedUser;
                }
            }
            throw new BadCredentialsException(USER_NOT_FOUND);
        } finally {
            allTAMAEvents.newLoginEvent(username, details.getRemoteAddress(), details.getSessionId(), status);
        }
    }

}
