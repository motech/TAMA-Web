package org.motechproject.tama.outbox.controller;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.motechproject.ivr.kookoo.KooKooIVRContext;
import org.motechproject.ivr.kookoo.KookooIVRResponseBuilder;
import org.motechproject.ivr.kookoo.controller.StandardResponseController;
import org.motechproject.ivr.kookoo.service.KookooCallDetailRecordsService;
import org.motechproject.outbox.api.VoiceOutboxService;
import org.motechproject.outbox.api.model.OutboundVoiceMessage;
import org.motechproject.tama.ivr.TamaIVRMessage;
import org.motechproject.tama.ivr.context.TAMAIVRContext;
import org.motechproject.tama.outbox.OutboxContextForTest;
import org.motechproject.tama.outbox.factory.VoiceMessageResponseFactory;
import org.motechproject.tama.outbox.service.OutboxEventLogger;
import org.motechproject.util.Cookies;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class OutboxControllerTest {
    public static final String patientId = "patientId";
    public static final String preferredLanguage = "en";

    @Mock
    private VoiceOutboxService outboxService;
    @Mock
    private TamaIVRMessage tamaIvrMessage;
    @Mock
    private TAMAIVRContext tamaivrContext;
    @Mock
    private Cookies cookies;
    @Mock
    private VoiceMessageResponseFactory messageResponseFactory;
    @Mock
    private KookooCallDetailRecordsService callDetailRecordsService;
    @Mock
    private StandardResponseController standardResponseController;
    @Mock
    private OutboxEventLogger outboxEventLogger;

    private OutboxController outboxController;
    private OutboxContextForTest outboxContextForTest;


    @Before
    public void setUp() {
        initMocks(this);
        outboxContextForTest = new OutboxContextForTest().partyId(patientId).preferredLanguage(preferredLanguage).callId("4543");
        outboxController = new OutboxController(outboxService, tamaIvrMessage, messageResponseFactory, callDetailRecordsService, standardResponseController, outboxEventLogger);
        outboxController = Mockito.spy(outboxController);
        Mockito.doReturn(outboxContextForTest).when(outboxController).getOutboxContext(Matchers.<KooKooIVRContext>any());
    }

    @Test
    public void shouldPlayTheNextMessage() {
        String lastPlayedMessageId = "123";
        outboxContextForTest.lastPlayedMessageId(lastPlayedMessageId);
        OutboundVoiceMessage voiceMessage = voiceMessage("456");
        when(outboxService.nextMessage(lastPlayedMessageId, patientId)).thenReturn(voiceMessage);

        outboxController.gotDTMF(any(KooKooIVRContext.class));
        assertEquals(voiceMessage.getId(), outboxContextForTest.lastPlayedMessageId());
        assertEquals(false, outboxContextForTest.hasOutboxCompleted());
    }

    @Test
    public void shouldPlayYouHaveNoMessages() {
        String lastPlayedMessageId = null;
        outboxContextForTest.lastPlayedMessageId(lastPlayedMessageId);
        when(outboxService.nextMessage(lastPlayedMessageId, patientId)).thenReturn(null);

        KookooIVRResponseBuilder ivrResponseBuilder = outboxController.gotDTMF(any(KooKooIVRContext.class));
        ivrResponseBuilder.getPlayAudios().contains(TamaIVRMessage.NO_MESSAGES);
        assertEquals(true, outboxContextForTest.hasOutboxCompleted());
    }

    @Test
    public void shouldPlayThoseWereYourMessages() {
        String lastPlayedMessageId = "123";
        outboxContextForTest.lastPlayedMessageId(lastPlayedMessageId);
        when(outboxService.nextMessage(lastPlayedMessageId, patientId)).thenReturn(null);

        KookooIVRResponseBuilder ivrResponseBuilder = outboxController.gotDTMF(any(KooKooIVRContext.class));
        ivrResponseBuilder.getPlayAudios().contains(TamaIVRMessage.THESE_WERE_YOUR_MESSAGES_FOR_NOW);
        assertEquals(true, outboxContextForTest.hasOutboxCompleted());
    }

    @Test
    public void shouldRaisePlayedEventAfterPlayingMessage() {
        final String messageId = "messageId";

        OutboundVoiceMessage outboundMessage = new OutboundVoiceMessage();
        outboundMessage.setId(messageId);

        when(outboxService.nextMessage(anyString(), anyString())).thenReturn(outboundMessage);

        outboxController.gotDTMF(any(KooKooIVRContext.class));
        verify(outboxEventLogger).onPlayed(eq(patientId), any(KookooIVRResponseBuilder.class), eq(messageId));
    }

    private OutboundVoiceMessage voiceMessage(String messageId) {
        OutboundVoiceMessage voiceMessage = new OutboundVoiceMessage();
        voiceMessage.setId(messageId);
        return voiceMessage;
    }
}
