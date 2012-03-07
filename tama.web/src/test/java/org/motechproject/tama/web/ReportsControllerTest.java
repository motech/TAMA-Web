package org.motechproject.tama.web;

import org.joda.time.LocalDate;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.tama.dailypillreminder.service.DailyPillReminderReportService;
import org.motechproject.tama.outbox.integration.repository.AllOutboxMessageSummaries;
import org.motechproject.tama.outbox.service.OutboxMessageReportService;
import org.motechproject.tama.patient.domain.PatientReport;
import org.motechproject.tama.patient.repository.AllPatients;
import org.motechproject.tama.patient.repository.AllTreatmentAdvices;
import org.motechproject.tama.patient.service.PatientService;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
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

    private ReportsController reportsController;

    @Before
    public void setUp() {
        initMocks(this);
        reportsController = new ReportsController(patientService, dailyPillReminderReportService, outboxReportService, allOutboxMessageSummaries);
    }

    @Test
    public void shouldReturnIndexPage() throws IOException {
        String patientDocumentId = "patientDocumentId";
        PatientReport patientReport = mock(PatientReport.class);
        when(patientService.getPatientReport(patientDocumentId)).thenReturn(patientReport);

        ModelAndView modelAndView = reportsController.index(patientDocumentId);

        assertEquals("reports/index", modelAndView.getViewName());
        Map<String, Object> model = modelAndView.getModel();
        assertNotNull(model.get("report"));
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
    public void shouldReturnDailyPillReminderExcelReport() throws JSONException {
        String patientDocumentId = "patientId";
        LocalDate day1 = new LocalDate(2011, 1, 1);
        LocalDate day2 = new LocalDate(2011, 1, 3);

        PatientReport patientReport = mock(PatientReport.class);
        when(patientService.getPatientReport(patientDocumentId)).thenReturn(patientReport);

        HttpServletResponse httpServletResponse = mock(HttpServletResponse.class);
        reportsController.buildDailyPillReminderExcelReport(patientDocumentId, day1, day2, httpServletResponse);

        verify(dailyPillReminderReportService).create(patientDocumentId, day1, day2);
        verify(patientService).getPatientReport(patientDocumentId);
    }

    @Test
    public void shouldBuildOutboxMessageExcelReport() {
        String patientDocumentId = "patientId";
        LocalDate day1 = new LocalDate(2011, 1, 1);
        LocalDate day2 = new LocalDate(2011, 1, 3);
        PatientReport patientReport = mock(PatientReport.class);

        when(patientService.getPatientReport(patientDocumentId)).thenReturn(patientReport);

        HttpServletResponse httpServletResponse = mock(HttpServletResponse.class);
        reportsController.buildOutboxMessageExcelReport(patientDocumentId, day1, day2, httpServletResponse);

        verify(allOutboxMessageSummaries).find(patientDocumentId, day1, day2);
        verify(patientService).getPatientReport(patientDocumentId);
    }

}
