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
    private HomeController homeController;

    @Before
    public void setUp() {
        initMocks(this);
        when(httpServletRequest.getSession()).thenReturn(httpSession);
        when(httpSession.getAttribute("loggedInUser")).thenReturn(authenticatedUser);
        homeController = new HomeController();
    }

    @Test
    public void shouldRedirectToDataDashboardForAnAnalyst() {
        when(authenticatedUser.isAnalyst()).thenReturn(true);
        String page = homeController.homePage(httpServletRequest);
        assertEquals("redirect:/analysisData", page);
    }

    @Test
    public void shouldRedirectToAlertssListingPage_ForAClinician() {
        when(authenticatedUser.isAdministrator()).thenReturn(false);

        String page = homeController.homePage(httpServletRequest);
        assertEquals("redirect:/alerts/list", page);
    }

    @Test
    public void shouldRedirectToClinicsListingPage_ForAnAdministrator() {
        when(authenticatedUser.isAdministrator()).thenReturn(true);

        String page = homeController.homePage(httpServletRequest);
        assertEquals("redirect:/clinics", page);
    }
}
