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

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class OutboxMessageReportServiceTest extends BaseUnitTest {

    @Mock
    private AllOutboxLogs allOutboxLogs;
    private OutboxMessageReportService outboxMessageReportService;
    private LocalDate today;
    private DateTime createdOnDate;

    @Before
    public void setup() {
        initMocks(this);
        outboxMessageReportService = new OutboxMessageReportService(allOutboxLogs);
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
        assertEquals(createdOnDate.toString(), createdOn);
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
        assertEquals(2, result.getJSONArray("logs").length());
    }

    private OutboxMessageLog newLog(String patientDocId) {
        return new OutboxMessageLog(patientDocId, "outboxMessageId", createdOnDate);
    }

    private void addPlayedLog(OutboxMessageLog outboxMessageLog) {
        outboxMessageLog.playedOn(DateUtil.now().minusDays(1), Arrays.asList("a", "b"));
    }
}
