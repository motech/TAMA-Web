package org.motechproject.tama.outbox.service;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.motechproject.tama.outbox.domain.OutboxMessageLog;
import org.motechproject.tama.outbox.domain.OutboxSummary;
import org.motechproject.tama.outbox.integration.repository.AllOutboxLogs;
import org.motechproject.testing.utils.BaseUnitTest;
import org.motechproject.util.DateUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class OutboxMessageReportServiceTest extends BaseUnitTest {

    public static final String A_IN_ENGLISH_TEXT = "a in English text";
    public static final String PATIENT_DOC_ID = "patientDocId";
    @Mock
    private AllOutboxLogs allOutboxLogs;
    private OutboxMessageReportService outboxMessageReportService;
    private LocalDate today;
    private DateTime createdOnDate;
    private Properties outboxWaveFileToTextMapping = new Properties();

    @Before
    public void setup() {
        outboxWaveFileToTextMapping.put("a", A_IN_ENGLISH_TEXT);
        initMocks(this);
        outboxMessageReportService = new OutboxMessageReportService(allOutboxLogs, outboxWaveFileToTextMapping);
        today = DateUtil.today();
        mockCurrentDate(today);
        createdOnDate = DateUtil.now().minusDays(1);
    }

    @Test
    public void shouldCreateNewJsonObjectForEveryLog() throws JSONException {
        List<OutboxMessageLog> logs = Arrays.asList(
                newLog(PATIENT_DOC_ID),
                newLog(PATIENT_DOC_ID)
        );

        when(allOutboxLogs.list(PATIENT_DOC_ID,
                DateUtil.newDateTime(today.minusDays(3), 0, 0, 0),
                DateUtil.newDateTime(today, 0, 0, 0))).thenReturn(logs);

        JSONObject result = outboxMessageReportService.JSONReport(PATIENT_DOC_ID, today.minusDays(3), today);
        final JSONArray jsonLogsArray = result.getJSONArray("logs");
        assertEquals(logs.size(), jsonLogsArray.length());
        final JSONObject logJsonObject = (JSONObject) jsonLogsArray.get(0);
        final String createdOn = logJsonObject.getString("createdOn");
        assertEquals(createdOnDate.toLocalDate().toString(), createdOn);
    }

    @Test
    public void shouldCreateNewJsonObjectForEveryPlayedLog() throws JSONException {
        OutboxMessageLog messageLog = new OutboxMessageLog();
        List<OutboxMessageLog> messageLogs = Arrays.asList(messageLog);
        addPlayedLog(messageLog);
        addPlayedLog(messageLog);

        when(allOutboxLogs.list(PATIENT_DOC_ID,
                DateUtil.newDateTime(today.minusDays(3), 0, 0, 0),
                DateUtil.newDateTime(today, 0, 0, 0))).thenReturn(messageLogs);

        JSONObject result = outboxMessageReportService.JSONReport(PATIENT_DOC_ID, today.minusDays(3), today);
        final JSONArray logs = result.getJSONArray("logs");
        assertEquals(2, logs.length());
        final JSONObject jsonObject = (JSONObject) logs.get(0);
        assertEquals(A_IN_ENGLISH_TEXT, jsonObject.getString("playedFiles"));
    }

    @Test
    public void shouldReturnOutboxSummaryWhenOutboxMessageWasPlayed() {
        LocalDate startDate = DateUtil.today().minusWeeks(1);
        LocalDate endDate = DateUtil.today();
        ArrayList<OutboxMessageLog> outboxMessageLogs = outboxMessages(2);
        when(allOutboxLogs.list(eq(PATIENT_DOC_ID), Matchers.<DateTime>any(), Matchers.<DateTime>any())).thenReturn(outboxMessageLogs);
        List<OutboxSummary> outboxSummaries = outboxMessageReportService.create(PATIENT_DOC_ID, startDate, endDate);
        assertEquals(4, outboxSummaries.size());
        assertEquals("outboxMessageId1", outboxSummaries.get(0).getMessageId());
        assertEquals(outboxMessageReportService.formatDate(createdOnDate.plusDays(1)), outboxSummaries.get(0).getCreatedOn());
        assertEquals("VoiceMessage", outboxSummaries.get(0).getTypeName());
        assertEquals(outboxMessageReportService.formatDateTime(createdOnDate.plusDays(2)), outboxSummaries.get(0).getPlayedOn());
        assertNotNull(outboxSummaries.get(0).getPlayedFiles());
        assertEquals("outboxMessageId1", outboxSummaries.get(1).getMessageId());
        assertEquals("outboxMessageId2", outboxSummaries.get(2).getMessageId());
        assertEquals("outboxMessageId2", outboxSummaries.get(3).getMessageId());
    }

    private ArrayList<OutboxMessageLog> outboxMessages(int numberOfMessages) {
        ArrayList<OutboxMessageLog> outboxMessageLogs = new ArrayList<OutboxMessageLog>();
        for (int i = 1; i <= numberOfMessages; i++) {
            final OutboxMessageLog messageLog = new OutboxMessageLog(PATIENT_DOC_ID, "outboxMessageId" + i, createdOnDate.plusDays(i), "VoiceMessage");
            messageLog.playedOn(messageLog.getCreatedOn().plusDays(1), Arrays.asList("file1", "file2"));
            messageLog.playedOn(messageLog.getCreatedOn().plusDays(2), Arrays.asList("file1", "file2"));
            outboxMessageLogs.add(messageLog);
        }
        return outboxMessageLogs;
    }

    private OutboxMessageLog newLog(String patientDocId) {
        return new OutboxMessageLog(patientDocId, "outboxMessageId", createdOnDate, "Voice Message");
    }

    private void addPlayedLog(OutboxMessageLog outboxMessageLog) {
        outboxMessageLog.playedOn(DateUtil.now().minusDays(1), Arrays.asList("a"));
    }
}
