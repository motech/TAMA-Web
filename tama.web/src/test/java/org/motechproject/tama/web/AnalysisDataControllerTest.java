package org.motechproject.tama.web;


import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.tama.clinicvisits.contract.AppointmentCalenderReport;
import org.motechproject.tama.clinicvisits.service.AppointmentCalenderReportService;
import org.motechproject.tama.dailypillreminder.contract.DailyPillReminderReport;
import org.motechproject.tama.dailypillreminder.service.DailyPillReminderReportService;
import org.motechproject.tama.outbox.contract.OutboxMessageReport;
import org.motechproject.tama.outbox.service.OutboxMessageReportService;
import org.motechproject.tama.reporting.properties.ReportingProperties;
import org.motechproject.tama.web.model.FilterWithPatientIDAndDateRange;
import org.motechproject.util.DateUtil;
import org.springframework.ui.Model;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static org.mockito.Mockito.*;
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
    @Mock
    private OutboxMessageReportService outboxMessageReportService;
    @Mock
    private DailyPillReminderReportService dailyPillReminderReportService;

    private AnalysisDataController analysisDataController;

    @Before
    public void setup() {
        initMocks(this);
        analysisDataController = new AnalysisDataController(callSummaryController, reportingProperties, appointmentCalenderReportService, outboxMessageReportService, dailyPillReminderReportService);
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
        when(appointmentCalenderReportService.appointmentCalendarReport(patientId)).thenReturn(new AppointmentCalenderReport(null, null));

        analysisDataController.downloadAppointmentCalenderReport(patientId, response);
        verify(response).setContentType("application/vnd.ms-excel");
        verify(response).setHeader("Content-Disposition", "inline; filename=AppointmentCalendarReport.xls");
    }

    @Test
    public void shouldAddOutboxFilterModel() {
        when(reportingProperties.reportingURL()).thenReturn("url");
        assertEquals("analysisData/show", analysisDataController.show(model));
        verify(model).addAttribute(eq("outboxMessageReportFilter"), any(FilterWithPatientIDAndDateRange.class));
    }

    @Test
    public void shouldAddWarningMessageOnErrorForOutboxReport() {
        HttpServletResponse response = mock(HttpServletResponse.class);

        FilterWithPatientIDAndDateRange filter = mock(FilterWithPatientIDAndDateRange.class);
        when(filter.isMoreThanOneYear()).thenReturn(true);

        String view = analysisDataController.downloadOutboxMessageReport(filter, model, response);
        verify(model).addAttribute(eq("warning"), anyString());
        assertEquals("analysisData/show", view);
    }

    @Test
    public void shouldAddWarningMessageOnErrorForDailyPillReminderReport() {
        HttpServletResponse response = mock(HttpServletResponse.class);

        FilterWithPatientIDAndDateRange filter = mock(FilterWithPatientIDAndDateRange.class);
        when(filter.isMoreThanOneYear()).thenReturn(true);

        String view = analysisDataController.downloadDailyPillReminderReport(filter, model, response);
        verify(model).addAttribute(eq("warning"), anyString());
        assertEquals("analysisData/show", view);
    }

    @Test
    public void shouldDownloadOutboxReportGivenPatientId() throws IOException {
        LocalDate startDate = DateUtil.today().minusDays(3);
        LocalDate endDate = DateUtil.today();
        String patientId = "patientDocId";

        FilterWithPatientIDAndDateRange filter = new FilterWithPatientIDAndDateRange();
        filter.setPatientId(patientId);
        filter.setStartDate(startDate);
        filter.setEndDate(endDate);

        HttpServletResponse response = mock(HttpServletResponse.class);
        when(outboxMessageReportService.reports(patientId, startDate, endDate)).thenReturn(new OutboxMessageReport(null, null));

        analysisDataController.downloadOutboxMessageReport(filter, model, response);
        verify(response).setContentType("application/vnd.ms-excel");
        verify(response).setHeader("Content-Disposition", "inline; filename=OutboxSummaryReport.xls");
    }

    @Test
    public void shouldDownloadDailyPillReminderReportGivenPatientId() throws IOException {
        LocalDate startDate = DateUtil.today().minusDays(3);
        LocalDate endDate = DateUtil.today();
        String patientId = "patientDocId";

        FilterWithPatientIDAndDateRange filter = new FilterWithPatientIDAndDateRange();
        filter.setPatientId(patientId);
        filter.setStartDate(startDate);
        filter.setEndDate(endDate);

        HttpServletResponse response = mock(HttpServletResponse.class);
        when(dailyPillReminderReportService.reports(patientId, startDate, endDate)).thenReturn(new DailyPillReminderReport(null,null));

        analysisDataController.downloadDailyPillReminderReport(filter, model, response);
        verify(response).setContentType("application/vnd.ms-excel");
        verify(response).setHeader("Content-Disposition", "inline; filename=DailyPillReminderReport.xls");
    }
}
