package org.motechproject.tama.outbox.service;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.motechproject.ivr.kookoo.KookooIVRResponseBuilder;
import org.motechproject.outbox.api.model.OutboundVoiceMessage;
import org.motechproject.outbox.api.model.VoiceMessageType;
import org.motechproject.tama.common.TAMAConstants;
import org.motechproject.tama.outbox.domain.OutboxEventType;
import org.motechproject.tama.outbox.domain.OutboxMessageLog;
import org.motechproject.tama.outbox.integration.repository.AllOutboxLogs;
import org.motechproject.testing.utils.BaseUnitTest;
import org.motechproject.util.DateUtil;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class OutboxEventLoggerTest extends BaseUnitTest {

    @Mock
    AllOutboxLogs allOutboxEvents;
    OutboxEventLogger outboxEventLogger;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        outboxEventLogger = new OutboxEventLogger(allOutboxEvents);
        mockCurrentDate(new DateTime(2011, 1, 1, 1, 1));
    }

    @Test
    public void shouldCreateOutboxEventLogOnCreateEvent() {
        OutboundVoiceMessage message = new OutboundVoiceMessage();
        VoiceMessageType voiceMessageType = new VoiceMessageType();
        voiceMessageType.setVoiceMessageTypeName(TAMAConstants.VOICE_MESSAGE_COMMAND_AUDIO);
        message.setVoiceMessageType(voiceMessageType);
        message.setId("messageId");

        outboxEventLogger.onCreate(message);

        ArgumentCaptor<OutboxMessageLog> captor = ArgumentCaptor.forClass(OutboxMessageLog.class);
        verify(allOutboxEvents).add(captor.capture());

        OutboxMessageLog expectedMessage = new OutboxMessageLog("patientDocId", "messageId", DateUtil.now(), TAMAConstants.VOICE_MESSAGE_COMMAND_AUDIO);
        assertEquals(expectedMessage, captor.getValue());
        assertEquals(expectedMessage.getTypeName(), captor.getValue().getTypeName());
    }

    @Test
    public void shouldCreateOutboxEventLogOnPlayedEvent() {
        KookooIVRResponseBuilder responseBuilder = new KookooIVRResponseBuilder().withPlayAudios("audio1", "audio2");
        OutboxMessageLog outboxMessageLog = mock(OutboxMessageLog.class);

        when(allOutboxEvents.find("patientDocId", "messageId")).thenReturn(outboxMessageLog);
        outboxEventLogger.onPlayed("patientDocId", responseBuilder, "messageId");

        verify(outboxMessageLog).playedOn(eq(DateUtil.now()), anyList());
        verify(allOutboxEvents).update(outboxMessageLog);
    }
}
