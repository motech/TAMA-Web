package org.motechproject.tama.outbox.integration.repository;

import org.joda.time.DateTime;
import org.junit.Test;
import org.motechproject.tama.common.integration.repository.SpringIntegrationTest;
import org.motechproject.tama.outbox.domain.OutboxEventType;
import org.motechproject.tama.outbox.domain.OutboxMessageLog;
import org.motechproject.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import static junit.framework.Assert.assertEquals;

@ContextConfiguration(locations = "classpath*:applicationOutboxContext.xml", inheritLocations = false)
public class AllOutboxLogsIT extends SpringIntegrationTest {

    @Autowired
    AllOutboxLogs allOutboxLogs;

    @Test
    public void shouldPersistOutboxLogs() {
        DateTime now = DateUtil.now();

        OutboxMessageLog messageLog = new OutboxMessageLog("messageId", now, OutboxEventType.Created);
        allOutboxLogs.add(messageLog);

        markForDeletion(messageLog);
        assertEquals(messageLog, allOutboxLogs.get(messageLog.getId()));
    }
}
