package org.motechproject.tama.security;

import org.motechproject.tama.security.repository.AllTAMAEvents;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

public class TamaSessionListener implements HttpSessionListener {

    private final String sourceAddress = "Session Timeout";

    @Override
    public void sessionCreated(HttpSessionEvent se) {
        //do nothing
    }

    @Override
    public void sessionDestroyed(HttpSessionEvent se) {
        final HttpSession session = se.getSession();
        if (session == null) return;
        Object userObject = session.getAttribute(LoginSuccessHandler.LOGGED_IN_USER);
        if (userObject instanceof AuthenticatedUser){
            AuthenticatedUser authenticatedUser = (AuthenticatedUser)userObject;
            WebApplicationContext ctx = WebApplicationContextUtils.getWebApplicationContext(session.getServletContext());
            AllTAMAEvents allTAMAEvents = (AllTAMAEvents)ctx.getBean("allTAMAEvents");
            allTAMAEvents.newLogoutEvent(authenticatedUser.getTAMAUser().getUsername(), sourceAddress, session.getId());
        }
    }
}
