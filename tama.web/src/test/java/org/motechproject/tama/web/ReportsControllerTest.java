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
import org.motechproject.tama.patient.builder.PatientBuilder;
import org.motechproject.tama.patient.domain.Patient;
import org.motechproject.tama.patient.repository.AllPatients;
import org.motechproject.tama.patient.service.PatientService;
import org.motechproject.tama.refdata.domain.Regimen;
import org.springframework.web.servlet.ModelAndView;

import java.io.IOException;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.powermock.api.mockito.PowerMockito.when;

public class ReportsControllerTest {

    @Mock
    private AllPatients allPatients;

    @Mock
    private PatientService patientService;

    @Mock
    private DailyPillReminderReportService dailyPillReminderReportService;

    private ReportsController reportsController;

    private Patient patient;

    private Clinic clinic;

    @Before
    public void setUp() {
        initMocks(this);
        clinic = ClinicBuilder.startRecording().withDefaults().build();
        patient = PatientBuilder.startRecording().withDefaults().withClinic(clinic).build();
        reportsController = new ReportsController(allPatients, patientService, dailyPillReminderReportService);
    }

    @Test
    public void shouldReturnIndexPage() throws IOException {
        Regimen regimen = mock(Regimen.class);
        when(allPatients.get("patientDocumentId")).thenReturn(patient);
        when(patientService.currentRegimen(patient)).thenReturn(regimen);

        ModelAndView patientReport = reportsController.index("patientDocumentId");
        assertEquals("reports/index", patientReport.getViewName());
    }

    @Test
    public void shouldReturnDailyPillReminderReport() throws JSONException {
        LocalDate day1 = new LocalDate(2011, 1, 1);
        LocalDate day2 = new LocalDate(2011, 1, 3);

        JSONObject jsonReport = new JSONObject();
        jsonReport.put("someKey", "someValue");
        when(dailyPillReminderReportService.JSONReport("patientId", day1, day2)).thenReturn(jsonReport);

        assertEquals(jsonReport.toString(), reportsController.dailyPillReminderReport("patientId", day1, day2));
    }
}
