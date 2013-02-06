package org.motechproject.tama.outbox.service;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.tama.outbox.contract.OutboxMessageReport;
import org.motechproject.tama.outbox.domain.OutboxMessageSummary;
import org.motechproject.tama.outbox.integration.repository.AllOutboxMessageSummaries;
import org.motechproject.tama.patient.domain.PatientReports;
import org.motechproject.tama.patient.service.PatientService;
import org.motechproject.testing.utils.BaseUnitTest;
import org.motechproject.util.DateUtil;

import java.util.Arrays;
import java.util.List;

import static java.util.Arrays.asList;
import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.motechproject.tama.patient.builder.PatientReportBuilder.newPatientReport;

public class OutboxMessageReportServiceTest extends BaseUnitTest {

    public static final String A_IN_ENGLISH_TEXT = "a in English text";
    public static final String PATIENT_DOC_ID = "patientDocId";
    @Mock
    private AllOutboxMessageSummaries allOutboxMessageSummaries;

    @Mock
    private PatientService patientService;

    private OutboxMessageReportService outboxMessageReportService;
    private LocalDate today;
    private DateTime createdOnDate;

    @Before
    public void setup() {
        initMocks(this);
        outboxMessageReportService = new OutboxMessageReportService(allOutboxMessageSummaries, patientService);
        today = DateUtil.today();
        mockCurrentDate(today);
        createdOnDate = DateUtil.now().minusDays(1);
    }

    @Test
    public void shouldCreateNewJsonObjectForEveryLog() throws JSONException {
        OutboxMessageSummary summary1 = new OutboxMessageSummary(createdOnDate, "type");
        OutboxMessageSummary summary2 = new OutboxMessageSummary(createdOnDate, "type");
        when(allOutboxMessageSummaries.find(PATIENT_DOC_ID, today.minusDays(3), today)).thenReturn(Arrays.asList(summary1, summary2));

        JSONObject result = outboxMessageReportService.JSONReport(PATIENT_DOC_ID, today.minusDays(3), today);

        final JSONArray jsonLogsArray = result.getJSONArray("logs");
        assertEquals(2, jsonLogsArray.length());
        final JSONObject logJsonObject = (JSONObject) jsonLogsArray.get(0);
        final String createdOn = logJsonObject.getString("createdOn");
        assertEquals(createdOnDate.toLocalDate().toString(), createdOn);
    }

    @Test
    public void shouldCreateNewJsonObjectForEveryPlayedLog() throws JSONException {
        OutboxMessageSummary summary1 = new OutboxMessageSummary(createdOnDate, "type");
        summary1.playedOn(DateUtil.now(), "message1");
        OutboxMessageSummary summary2 = new OutboxMessageSummary(createdOnDate, "type");
        summary2.playedOn(DateUtil.now(), "message2");
        when(allOutboxMessageSummaries.find(PATIENT_DOC_ID, today.minusDays(3), today)).thenReturn(Arrays.asList(summary1, summary2));

        JSONObject result = outboxMessageReportService.JSONReport(PATIENT_DOC_ID, today.minusDays(3), today);
        final JSONArray logs = result.getJSONArray("logs");
        assertEquals(2, logs.length());
        final JSONObject jsonObject = (JSONObject) logs.get(0);
        assertEquals("message1", jsonObject.getString("playedFiles").trim());
    }

    @Test
    public void shouldCreateOutboxMessageReport(){
        OutboxMessageSummary summary1 = new OutboxMessageSummary(createdOnDate, "type");
        summary1.playedOn(DateUtil.now(), "message1");
        OutboxMessageSummary summary2 = new OutboxMessageSummary(createdOnDate, "type");
        summary2.playedOn(DateUtil.now(), "message2");
        List<OutboxMessageSummary> outboxMessageSummaries = allOutboxMessageSummaries.find(PATIENT_DOC_ID, today.minusDays(3), today);
        when(outboxMessageSummaries).thenReturn(Arrays.asList(summary1, summary2));

        String patientId = "patientId";
        PatientReports patientReports = new PatientReports(asList(newPatientReport().withPatientId(patientId).withPatientDocumentId("patientDocumentId").build()));
        when(patientService.getPatientReports(patientId)).thenReturn(patientReports);

        OutboxMessageReport outboxMessageReport = new OutboxMessageReport(patientReports, outboxMessageSummaries);
        assertEquals(outboxMessageReport, outboxMessageReportService.reports(patientId, today.minusDays(3), today));
    }
}
