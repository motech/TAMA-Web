package org.motechproject.tama.web;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.ivr.kookoo.KooKooIVRContext;
import org.motechproject.ivr.kookoo.KookooIVRResponseBuilder;
import org.motechproject.ivr.kookoo.service.KookooCallDetailRecordsService;
import org.motechproject.outbox.api.VoiceOutboxService;
import org.motechproject.outbox.api.model.OutboundVoiceMessage;
import org.motechproject.tama.ivr.TAMAIVRContextFactory;
import org.motechproject.tama.ivr.TamaIVRMessage;
import org.motechproject.tama.ivr.VoiceMessageResponseFactory;
import org.motechproject.tama.outbox.OutboxContextForTest;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

public class OutboxControllerTest {
    public static final String patientId = "patientId";
    public static final String preferredLanguage = "en";

    @Mock
    private VoiceOutboxService outboxService;
    @Mock
    private TamaIVRMessage tamaIvrMessage;
    @Mock
    private VoiceMessageResponseFactory messageResponseFactory;
    @Mock
    private KookooCallDetailRecordsService callDetailRecordsService;

    private OutboxController outboxController;
    private OutboxContextForTest outboxContextForTest;

    @Before
    public void setUp() {
        initMocks(this);
        outboxContextForTest = new OutboxContextForTest().partyId(patientId).preferredLanguage(preferredLanguage).callId("4543");
        TAMAIVRContextFactory contextFactory = mock(TAMAIVRContextFactory.class);

        when(contextFactory.createOutboxContext(any(KooKooIVRContext.class))).thenReturn(outboxContextForTest);
        outboxController = new OutboxController(outboxService, tamaIvrMessage, contextFactory, messageResponseFactory, callDetailRecordsService);
    }

    @Test
    public void shouldPlayTheNextMessage() {
        String lastPlayedMessageId = "123";
        outboxContextForTest.lastPlayedMessageId(lastPlayedMessageId);
        OutboundVoiceMessage voiceMessage = voiceMessage("456");
        when(outboxService.nextMessage(lastPlayedMessageId, patientId)).thenReturn(voiceMessage);

        outboxController.gotDTMF(any(KooKooIVRContext.class));
        assertEquals(voiceMessage.getId(), outboxContextForTest.lastPlayedMessageId());
    }

    @Test
    public void shouldPlayYouHaveNoMessages() {
        String lastPlayedMessageId = null;
        outboxContextForTest.lastPlayedMessageId(lastPlayedMessageId);
        when(outboxService.nextMessage(lastPlayedMessageId, patientId)).thenReturn(null);

        KookooIVRResponseBuilder ivrResponseBuilder = outboxController.gotDTMF(any(KooKooIVRContext.class));
        ivrResponseBuilder.getPlayAudios().contains(TamaIVRMessage.NO_MESSAGES);
    }

    @Test
    public void shouldPlayThoseWereYourMessages() {
        String lastPlayedMessageId = "123";
        outboxContextForTest.lastPlayedMessageId(lastPlayedMessageId);
        when(outboxService.nextMessage(lastPlayedMessageId, patientId)).thenReturn(null);

        KookooIVRResponseBuilder ivrResponseBuilder = outboxController.gotDTMF(any(KooKooIVRContext.class));
        ivrResponseBuilder.getPlayAudios().contains(TamaIVRMessage.THOSE_WERE_YOUR_MESSAGES);
    }

    private OutboundVoiceMessage voiceMessage(String messageId) {
        OutboundVoiceMessage voiceMessage = new OutboundVoiceMessage();
        voiceMessage.setId(messageId);
        return voiceMessage;
    }
}
