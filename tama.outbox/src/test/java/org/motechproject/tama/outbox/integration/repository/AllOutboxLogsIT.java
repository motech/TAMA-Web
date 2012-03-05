package org.motechproject.tama.outbox.integration.repository;

import org.joda.time.DateTime;
import org.junit.Test;
import org.motechproject.tama.common.integration.repository.SpringIntegrationTest;
import org.motechproject.tama.outbox.domain.OutboxEventType;
import org.motechproject.tama.outbox.domain.OutboxMessageLog;
import org.motechproject.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import java.util.Arrays;
import java.util.List;

import static junit.framework.Assert.assertEquals;

@ContextConfiguration(locations = "classpath*:applicationOutboxContext.xml", inheritLocations = false)
public class AllOutboxLogsIT extends SpringIntegrationTest {

    public static final String TYPE_NAME = "Voice Message";
    @Autowired
    AllOutboxLogs allOutboxLogs;

    @Test
    public void shouldPersistOutboxLogs() {
        DateTime now = DateUtil.now();

        OutboxMessageLog messageLog = new OutboxMessageLog("patientDocId", "messageId", now, TYPE_NAME);
        allOutboxLogs.add(messageLog);

        markForDeletion(messageLog);
        assertEquals(messageLog, allOutboxLogs.get(messageLog.getId()));
    }

    @Test
    public void shouldListOutboxMessageLog() throws Exception {
        DateTime now = DateUtil.now();
        final String patientDocId = "patientDocId";

        OutboxMessageLog messageLog1 = new OutboxMessageLog(patientDocId, "messageId", now, TYPE_NAME).playedOn(now, Arrays.asList("a.wav"));
        OutboxMessageLog messageLog2 = new OutboxMessageLog(patientDocId, "messageId", now, TYPE_NAME);

        allOutboxLogs.add(messageLog1);
        allOutboxLogs.add(messageLog2);
        markForDeletion(messageLog1, messageLog2);

        final List<OutboxMessageLog> messages = allOutboxLogs.list(patientDocId, now.minusDays(1), now.plusMinutes(1));
        assertEquals(2, messages.size());
    }
}
