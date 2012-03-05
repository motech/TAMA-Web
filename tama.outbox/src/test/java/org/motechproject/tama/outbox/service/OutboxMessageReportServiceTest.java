package org.motechproject.tama.outbox.service;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.tama.common.TAMAConstants;
import org.motechproject.tama.outbox.domain.OutboxMessageLog;
import org.motechproject.tama.outbox.integration.repository.AllOutboxLogs;
import org.motechproject.testing.utils.BaseUnitTest;
import org.motechproject.util.DateUtil;

import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class OutboxMessageReportServiceTest extends BaseUnitTest {

    public static final String A_IN_ENGLISH_TEXT = "a in English text";
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
        final String patientDocId = "patientDocId";

        List<OutboxMessageLog> logs = Arrays.asList(
                newLog(patientDocId),
                newLog(patientDocId)
        );

        when(allOutboxLogs.list("patientDocId",
                DateUtil.newDateTime(today.minusDays(3), 0, 0, 0),
                DateUtil.newDateTime(today, 0, 0, 0))).thenReturn(logs);

        JSONObject result = outboxMessageReportService.JSONReport(patientDocId, today.minusDays(3), today);
        final JSONArray jsonLogsArray = result.getJSONArray("logs");
        assertEquals(logs.size(), jsonLogsArray.length());
        final JSONObject logJsonObject = (JSONObject) jsonLogsArray.get(0);
        final String createdOn = logJsonObject.getString("createdOn");
        assertEquals(createdOnDate.toLocalDate().toString(), createdOn);
    }

    @Test
    public void shouldCreateNewJsonObjectForEveryPlayedLog() throws JSONException {
        final String patientDocId = "patientDocId";

        final OutboxMessageLog messageLog = new OutboxMessageLog();
        List<OutboxMessageLog> messageLogs = Arrays.asList(messageLog);
        addPlayedLog(messageLog);
        addPlayedLog(messageLog);

        when(allOutboxLogs.list("patientDocId",
                DateUtil.newDateTime(today.minusDays(3), 0, 0, 0),
                DateUtil.newDateTime(today, 0, 0, 0))).thenReturn(messageLogs);

        JSONObject result = outboxMessageReportService.JSONReport(patientDocId, today.minusDays(3), today);
        final JSONArray logs = result.getJSONArray("logs");
        assertEquals(2, logs.length());
        final JSONObject jsonObject = (JSONObject) logs.get(0);
        assertEquals(A_IN_ENGLISH_TEXT, jsonObject.getString("playedFiles"));
    }

    private OutboxMessageLog newLog(String patientDocId) {
        return new OutboxMessageLog(patientDocId, "outboxMessageId", createdOnDate, "Voice Message");
    }

    private void addPlayedLog(OutboxMessageLog outboxMessageLog) {
        outboxMessageLog.playedOn(DateUtil.now().minusDays(1), Arrays.asList("a"));
    }
}
