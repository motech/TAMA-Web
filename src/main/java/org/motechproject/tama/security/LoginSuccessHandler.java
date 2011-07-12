package org.motechproject.tama.security;

import org.apache.log4j.Logger;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class LoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    private static final String LOGGED_IN_USER_ATTR = "loggedInUser";

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        AuthenticatedUser user = (AuthenticatedUser) authentication.getPrincipal();
        request.getSession().setAttribute(LOGGED_IN_USER_ATTR, user);
        super.onAuthenticationSuccess(request, response, authentication);
    }

}
