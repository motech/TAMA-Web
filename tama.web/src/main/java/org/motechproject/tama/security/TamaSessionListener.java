package org.motechproject.tama.security;

import org.motechproject.tama.security.repository.AllTAMAEvents;
import org.springframework.security.web.session.HttpSessionEventPublisher;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;

public class TamaSessionListener extends HttpSessionEventPublisher {

    private final String sourceAddress = "Session Timeout";

    @Override
    public void sessionCreated(HttpSessionEvent se) {
        //do nothing
    }

    @Override
    public void sessionDestroyed(HttpSessionEvent se) {
        HttpSession session = se.getSession();
        if (session == null) return;
        Object userObject = session.getAttribute(LoginSuccessHandler.LOGGED_IN_USER);
        if (userObject instanceof AuthenticatedUser) {
            AuthenticatedUser authenticatedUser = (AuthenticatedUser) userObject;
            WebApplicationContext ctx = WebApplicationContextUtils.getWebApplicationContext(session.getServletContext());
            AllTAMAEvents allTAMAEvents = (AllTAMAEvents) ctx.getBean("allTAMAEvents");
            allTAMAEvents.newLogoutEvent(authenticatedUser.getTAMAUser().getUsername(), sourceAddress, session.getId());
            allTAMAEvents = null;
            userObject = null;
        }
        super.sessionDestroyed(se);
    }
}
