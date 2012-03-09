package org.motechproject.tama.security;

import org.motechproject.tama.security.repository.AllAccessEvents;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.security.web.authentication.logout.SimpleUrlLogoutSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class LogoutSuccessHandler extends SimpleUrlLogoutSuccessHandler {
    private AllAccessEvents allAccessEvents;

    @Autowired
    public LogoutSuccessHandler(AllAccessEvents allAccessEvents) {
        this.allAccessEvents = allAccessEvents;
    }

    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        WebAuthenticationDetails details = (WebAuthenticationDetails) authentication.getDetails();
        allAccessEvents.newLogoutEvent(authentication.getName(), details.getRemoteAddress(), details.getSessionId());
        super.onLogoutSuccess(request, response, authentication);
    }
}
