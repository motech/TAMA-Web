package org.motechproject.tama.outbox.service;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.motechproject.outbox.api.model.OutboundVoiceMessage;
import org.motechproject.tama.outbox.domain.OutboxEventType;
import org.motechproject.tama.outbox.domain.OutboxMessageLog;
import org.motechproject.tama.outbox.repository.AllOutboxEvents;
import org.motechproject.testing.utils.BaseUnitTest;
import org.motechproject.util.DateUtil;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;

public class OutboxEventLoggerTest extends BaseUnitTest {

    @Mock
    AllOutboxEvents allOutboxEvents;
    OutboxEventLogger outboxEventLogger;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        outboxEventLogger = new OutboxEventLogger(allOutboxEvents);
    }

    @Test
    public void shouldCreateOutboxEventLogOnCreateEvent() {
        mockCurrentDate(DateUtil.now());
        OutboundVoiceMessage message = new OutboundVoiceMessage();
        message.setId("messageId");

        outboxEventLogger.onCreate(message);

        ArgumentCaptor<OutboxMessageLog> captor = ArgumentCaptor.forClass(OutboxMessageLog.class);
        verify(allOutboxEvents).add(captor.capture());

        OutboxMessageLog expectedMessage = new OutboxMessageLog("messageId", DateUtil.now(), OutboxEventType.Created);
        assertEquals(expectedMessage, captor.getValue());
    }
}
