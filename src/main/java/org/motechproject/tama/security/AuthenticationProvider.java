package org.motechproject.tama.security;

import org.apache.commons.lang.StringUtils;
import org.motechproject.tama.security.profiles.SecurityProfile;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.AbstractUserDetailsAuthenticationProvider;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.List;

public class AuthenticationProvider extends AbstractUserDetailsAuthenticationProvider {
    public static final String PLEASE_ENTER_PASSWORD = "Please enter password";
    public static final String USER_NOT_FOUND = "User not found";
    private List<SecurityProfile> profiles;

    public AuthenticationProvider(List<SecurityProfile> profiles) {
        this.profiles = profiles;
    }

    @Override
    protected void additionalAuthenticationChecks(UserDetails userDetails, UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken) throws AuthenticationException {
    }

    @Override
    protected UserDetails retrieveUser(String username, UsernamePasswordAuthenticationToken authentication) throws AuthenticationException {
        String password = (String) authentication.getCredentials();
        if (StringUtils.isEmpty(password)) throw new BadCredentialsException(PLEASE_ENTER_PASSWORD);

        List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
        for (SecurityProfile profile : profiles)
            authorities.addAll(profile.authoritiesFor(username, password));

        if(authorities.isEmpty()) throw new BadCredentialsException(USER_NOT_FOUND);
        return new User(username, password, true, true, true, true, authorities);
    }
}
