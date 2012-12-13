package org.motechproject.tama.web;


import org.junit.Before;
import org.junit.Test;

import static org.openqa.selenium.support.testing.Assertions.assertEquals;

public class AnalysisDataControllerTest {

    private AnalysisDataController analysisDataController;

    @Before
    public void setup() {
        analysisDataController = new AnalysisDataController();
    }

    @Test
    public void shouldShowCallLogsFilterAsTheLandingPage() throws Exception {
        assertEquals("redirect:/callsummary?form", analysisDataController.show());
    }
}
