package org.motechproject.tama.web;


import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.tama.clinicvisits.service.AppointmentCalenderReportService;
import org.motechproject.tama.reporting.properties.ReportingProperties;
import org.springframework.ui.Model;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.openqa.selenium.support.testing.Assertions.assertEquals;

public class AnalysisDataControllerTest {

    @Mock
    private CallSummaryController callSummaryController;
    @Mock
    private ReportingProperties reportingProperties;
    @Mock
    private Model model;
    @Mock
    private AppointmentCalenderReportService appointmentCalenderReportService;

    private AnalysisDataController analysisDataController;

    @Before
    public void setup() {
        initMocks(this);
        analysisDataController = new AnalysisDataController(callSummaryController, reportingProperties, appointmentCalenderReportService);
    }

    @Test
    public void shouldShowCallLogsFilterAsTheLandingPage() throws Exception {
        when(reportingProperties.reportingURL()).thenReturn("url");
        assertEquals("analysisData/show", analysisDataController.show(model));
        verify(model).addAttribute("reports_url", "url");
    }

    @Test
    public void shouldDownloadAppointmentCalendarGivenPatientId() throws IOException {
        String patientId = "patientDocId";
        HttpServletResponse response = mock(HttpServletResponse.class);

        analysisDataController.downloadAppointmentCalenderReport(patientId, response);
        verify(response).setContentType("application/vnd.ms-excel");
        verify(response).setHeader("Content-Disposition", "inline; filename=AppointmentCalendarReport.xls");
    }
}
