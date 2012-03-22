package org.motechproject.tama.web;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.LocalTime;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.motechproject.tama.dailypillreminder.service.DailyPillReminderReportService;
import org.motechproject.tama.ivr.repository.AllSMSLogs;
import org.motechproject.tama.outbox.integration.repository.AllOutboxMessageSummaries;
import org.motechproject.tama.outbox.service.OutboxMessageReportService;
import org.motechproject.tama.patient.domain.PatientReport;
import org.motechproject.tama.patient.repository.AllPatients;
import org.motechproject.tama.patient.repository.AllTreatmentAdvices;
import org.motechproject.tama.patient.service.PatientService;
import org.motechproject.tama.web.model.CallLogSummary;
import org.motechproject.tama.web.service.AllCallLogSummaries;
import org.motechproject.util.DateUtil;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.powermock.api.mockito.PowerMockito.when;

public class ReportsControllerTest {

    @Mock
    private AllPatients allPatients;
    @Mock
    private AllTreatmentAdvices allTreatmentAdvices;
    @Mock
    private PatientService patientService;
    @Mock
    private DailyPillReminderReportService dailyPillReminderReportService;
    @Mock
    private OutboxMessageReportService outboxReportService;
    @Mock
    private AllOutboxMessageSummaries allOutboxMessageSummaries;
    @Mock
    private AllCallLogSummaries allCallLogSummaries;
    @Mock
    private AllSMSLogs allSMSLogs;

    private ReportsController reportsController;

    @Before
    public void setUp() {
        initMocks(this);
        reportsController = new ReportsController(patientService, dailyPillReminderReportService, outboxReportService, allOutboxMessageSummaries, allCallLogSummaries, allSMSLogs);
    }

    private void initializePatientService(String patientDocumentId) {
        PatientReport patientReport = mock(PatientReport.class);
        when(patientService.getPatientReport(patientDocumentId)).thenReturn(patientReport);
        when(patientReport.getARTStartedOn()).thenReturn(DateUtil.today().toDate());
        when(patientReport.getPatientId()).thenReturn("patientId");
        when(patientReport.getClinicName()).thenReturn("clinicName");
        when(patientReport.getCurrentRegimenName()).thenReturn("currentRegimen");
        when(patientReport.getCurrentRegimenStartDate()).thenReturn(DateUtil.today().toDate());
    }

    @Test
    public void shouldReturnIndexPage() throws IOException {
        String patientDocumentId = "patientDocumentId";
        initializePatientService(patientDocumentId);
        ModelAndView modelAndView = reportsController.index(patientDocumentId);

        assertEquals("reports/index", modelAndView.getViewName());
        assertNotNull(modelAndView.getModel().get("report"));
        verify(patientService).getPatientReport(patientDocumentId);
    }

    @Test
    public void shouldReturnDailyPillReminderJsonReport() throws JSONException {
        LocalDate day1 = new LocalDate(2011, 1, 1);
        LocalDate day2 = new LocalDate(2011, 1, 3);
        JSONObject jsonReport = new JSONObject();
        jsonReport.put("someKey", "someValue");

        when(dailyPillReminderReportService.JSONReport("patientId", day1, day2)).thenReturn(jsonReport);

        assertEquals(jsonReport.toString(), reportsController.dailyPillReminderReport("patientId", day1, day2));
    }

    @Test
    public void shouldServeOutboxMessageJsonReport() throws JSONException {
        LocalDate day1 = new LocalDate(2011, 1, 1);
        LocalDate day2 = new LocalDate(2011, 1, 3);
        JSONObject jsonReport = new JSONObject();
        jsonReport.put("someKey", "someValue");

        when(outboxReportService.JSONReport("patientId", day1, day2)).thenReturn(jsonReport);

        assertEquals(jsonReport.toString(), reportsController.outboxMessageReport("patientId", day1, day2));
    }

    @Test
    public void shouldReturnDailyPillReminderExcelReport() throws JSONException, IOException {
        String patientDocumentId = "patientId";
        LocalDate day1 = new LocalDate(2011, 1, 1);
        LocalDate day2 = new LocalDate(2011, 1, 3);
        initializePatientService(patientDocumentId);
        HttpServletResponse httpServletResponse = initializeServletResponse();

        reportsController.buildDailyPillReminderExcelReport(patientDocumentId, day1, day2, httpServletResponse);

        verify(dailyPillReminderReportService).create(patientDocumentId, day1, day2);
        verify(patientService).getPatientReport(patientDocumentId);
    }

    @Test
    public void shouldBuildOutboxMessageExcelReport() throws IOException {
        String patientDocumentId = "patientId";
        LocalDate day1 = new LocalDate(2011, 1, 1);
        LocalDate day2 = new LocalDate(2011, 1, 3);
        HttpServletResponse httpServletResponse = initializeServletResponse();
        initializePatientService(patientDocumentId);

        reportsController.buildOutboxMessageExcelReport(patientDocumentId, day1, day2, httpServletResponse);

        verify(allOutboxMessageSummaries).find(patientDocumentId, day1, day2);
        verify(patientService).getPatientReport(patientDocumentId);
    }

    @Test
    public void shouldBuildCallLogsExcelReport() throws IOException {
        LocalDate startDate = new LocalDate(2011, 1, 1);
        LocalDate endDate = new LocalDate(2011, 1, 3);
        List<CallLogSummary> callLogSummaries = new ArrayList<CallLogSummary>();
        HttpServletResponse httpServletResponse = initializeServletResponse();

        when(allCallLogSummaries.getAllCallLogSummariesBetween(startDate, endDate)).thenReturn(callLogSummaries);

        reportsController.buildCallLogExcelReport(startDate, endDate, httpServletResponse);

        verify(allCallLogSummaries).getAllCallLogSummariesBetween(startDate, endDate);
    }

    @Test
    public void shouldBuildSMSExcelReport() throws IOException {
        HttpServletResponse httpServletResponse = initializeServletResponse();
        LocalDate startDate = DateUtil.newDate(2010, 10, 10);
        LocalDate endDate = DateUtil.newDate(2010, 10, 20);

        reportsController.buildSMSExcelReport(startDate, endDate, httpServletResponse);

        ArgumentCaptor<DateTime> dateTimeArgumentCaptor = ArgumentCaptor.forClass(DateTime.class);

        verify(allSMSLogs).findAllSMSLogsForDateRange(dateTimeArgumentCaptor.capture(), dateTimeArgumentCaptor.capture());
        assertEquals(startDate, dateTimeArgumentCaptor.getAllValues().get(0).toLocalDate());
        assertEquals(new LocalTime(0, 0, 0), dateTimeArgumentCaptor.getAllValues().get(0).toLocalTime());
        assertEquals(endDate, dateTimeArgumentCaptor.getAllValues().get(1).toLocalDate());
        assertEquals(new LocalTime(23, 59, 59), dateTimeArgumentCaptor.getAllValues().get(1).toLocalTime());
        verify(httpServletResponse.getOutputStream(), atLeastOnce()).write(Matchers.<byte[]>any());
    }

    private HttpServletResponse initializeServletResponse() throws IOException {
        HttpServletResponse httpServletResponse = mock(HttpServletResponse.class);
        ServletOutputStream outputStream = mock(ServletOutputStream.class);
        when(httpServletResponse.getOutputStream()).thenReturn(outputStream);
        return httpServletResponse;
    }

}
