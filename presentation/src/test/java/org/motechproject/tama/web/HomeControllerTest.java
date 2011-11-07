package org.motechproject.tama.web;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.tama.security.AuthenticatedUser;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class HomeControllerTest {

    @Mock
    private HttpSession httpSession;

    @Mock
    private HttpServletRequest httpServletRequest;

    @Mock
    private AuthenticatedUser authenticatedUser;

    @Before
    public void setUp(){
        initMocks(this);
        when(httpServletRequest.getSession()).thenReturn(httpSession);
        when(httpSession.getAttribute("loggedInUser")).thenReturn(authenticatedUser);
    }

    @Test
    public void shouldRedirectToPatientsListingPage_ForAClinician() {
        when(authenticatedUser.isAdministrator()).thenReturn(false);

        HomeController controller = new HomeController();
        String page = controller.homePage(httpServletRequest);
        assertEquals("redirect:/patients", page);
    }

    @Test
    public void shouldRedirectToClinicsListingPage_ForAnAdministrator() {
        when(authenticatedUser.isAdministrator()).thenReturn(true);

        HomeController controller = new HomeController();
        String page = controller.homePage(httpServletRequest);
        assertEquals("redirect:/clinics", page);
    }
}
