package org.motechproject.tama.web;

import org.joda.time.LocalDate;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.tama.dailypillreminder.service.DailyPillReminderReportService;
import org.motechproject.tama.facility.builder.ClinicBuilder;
import org.motechproject.tama.facility.domain.Clinic;
import org.motechproject.tama.outbox.service.OutboxMessageReportService;
import org.motechproject.tama.patient.builder.PatientBuilder;
import org.motechproject.tama.patient.domain.Patient;
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

    private ReportsController reportsController;

    private Patient patient;

    private Clinic clinic;


    @Before
    public void setUp() {
        initMocks(this);
        clinic = ClinicBuilder.startRecording().withDefaults().build();
        patient = PatientBuilder.startRecording().withDefaults().withClinic(clinic).build();
        reportsController = new ReportsController(allPatients, allTreatmentAdvices, patientService, dailyPillReminderReportService, outboxReportService);
    }

    @Test
    public void shouldReturnIndexPage() throws IOException {
        String patientDocumentId = "patientDocumentId";

        when(allPatients.get(patientDocumentId)).thenReturn(patient);

        ModelAndView modelAndView = reportsController.index(patientDocumentId);
        assertEquals("reports/index", modelAndView.getViewName());
        Map<String, Object> model = modelAndView.getModel();
        assertNotNull(model.get("report"));

        verify(allPatients).get(patientDocumentId);
        verify(patientService).currentRegimen(patient);
        verify(allTreatmentAdvices).earliestTreatmentAdvice(patientDocumentId);
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

        when(allPatients.get(patientDocumentId)).thenReturn(patient);

        HttpServletResponse httpServletResponse = mock(HttpServletResponse.class);
        reportsController.buildDailyPillReminderExcelReport(patientDocumentId, day1, day2, httpServletResponse);

        verify(dailyPillReminderReportService).create(patientDocumentId, day1, day2);
        verify(allPatients).get(patientDocumentId);
        verify(patientService).currentRegimen(patient);
        verify(allTreatmentAdvices).earliestTreatmentAdvice(patientDocumentId);

    }
}
