package org.motechproject.tama.web;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class HomeControllerTest {

    @Test
    public void shouldRedirectToPatientListingPage() {
        HomeController controller = new HomeController();
        String page = controller.homePage();
        assertEquals("redirect:/patients", page);
    }
}
