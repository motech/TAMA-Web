package org.motechproject.tama.web;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.outbox.api.VoiceOutboxService;
import org.motechproject.outbox.api.model.OutboundVoiceMessage;
import org.motechproject.outbox.api.model.OutboundVoiceMessageStatus;
import org.motechproject.server.service.ivr.IVRMessage;
import org.motechproject.server.service.ivr.IVRSession;
import org.motechproject.tama.ivr.TamaIVRMessage;
import org.motechproject.tama.util.TamaSessionUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.HashMap;

import static junit.framework.Assert.assertNull;
import static junit.framework.Assert.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class OutboxControllerTest {

    public static final String PATIENT_ID = "patientId";
    public static final String WAV1 = "something";
    public static final String WAV2 = "something_else";
    public static final String WAV1_URL = "http://localhost/tama/something.wav";
    public static final String WAV2_URL = "http://localhost/tama/something_else.wav";
    public static final String OUTBOX_URL = "http://localhost/tama/outbox";
    public static final String VOICE_MESSAGE_ID = "VMID";
    public static final String SIGNATURE_MUSIC_URL = "http://localhost/wav/signature_music.wav";
    private static final String LAST_PLAYED_VM_ID = "lastVMID";

    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpSession session;
    @Mock
    private VoiceOutboxService outboxService;
    @Mock
    private IVRMessage ivrMessage;
    private OutboxController outboxController;

    @Before
    public void setUp() {
        initMocks(this);
        when(request.getSession()).thenReturn(session);
        when(session.getAttribute(IVRSession.IVRCallAttribute.EXTERNAL_ID)).thenReturn(PATIENT_ID);
        when(session.getAttribute(IVRSession.IVRCallAttribute.PREFERRED_LANGUAGE_CODE)).thenReturn("en");

        when(ivrMessage.getText(TamaIVRMessage.OUTBOX_LOCATION_URL)).thenReturn(OUTBOX_URL);
        when(ivrMessage.getWav(WAV1, "en")).thenReturn(WAV1_URL);
        when(ivrMessage.getWav("something_else", "en")).thenReturn(WAV2_URL);

        outboxController = new OutboxController(outboxService, ivrMessage);
    }

    private OutboundVoiceMessage voiceMessage() {
        OutboundVoiceMessage voiceMessage = new OutboundVoiceMessage();
        voiceMessage.setId(VOICE_MESSAGE_ID);
        final ArrayList<String> audioFiles = new ArrayList<String>() {{
            add(WAV1);
            add(WAV2);
        }};
        HashMap<String, Object> parameters = new HashMap<String, Object>() {{
            put(OutboxController.AUDIO_FILES_KEY, audioFiles);
        }};
        voiceMessage.setParameters(parameters);
        return voiceMessage;
    }

    @Test
    public void shouldRespondWithNextPendingMessage() {
        OutboundVoiceMessage voiceMessage = voiceMessage();
        when(outboxService.getNextPendingMessage(PATIENT_ID)).thenReturn(voiceMessage);

        String response = outboxController.play(request);

        assertTrue(response.contains(WAV1_URL));
        assertTrue(response.contains(WAV2_URL));
        assertTrue(response.contains(OUTBOX_URL));
    }

    @Test
    public void shouldPersistTheCurrentMessagesIDInSession() {
        OutboundVoiceMessage voiceMessage = voiceMessage();
        when(outboxService.getNextPendingMessage(PATIENT_ID)).thenReturn(voiceMessage);

        outboxController.play(request);
        verify(session).setAttribute(TamaSessionUtil.TamaSessionAttribute.LAST_PLAYED_VOICE_MESSAGE_ID, VOICE_MESSAGE_ID);
    }

    @Test
    public void shouldMarkThePreviousMessageAsRead() {
        OutboundVoiceMessage voiceMessage = voiceMessage();
        when(outboxService.getNextPendingMessage(PATIENT_ID)).thenReturn(voiceMessage);

        when(session.getAttribute(TamaSessionUtil.TamaSessionAttribute.LAST_PLAYED_VOICE_MESSAGE_ID)).thenReturn(LAST_PLAYED_VM_ID);
        outboxController.play(request);
        verify(outboxService).setMessageStatus(LAST_PLAYED_VM_ID, OutboundVoiceMessageStatus.PLAYED);
    }

    @Test
    public void shouldPlaySignatureMusicAndHangupIfNoMorePendingOutboxMessages() {
        when(ivrMessage.getSignatureMusic()).thenReturn(TamaIVRMessage.SIGNATURE_MUSIC_URL);
        when(ivrMessage.getWav(TamaIVRMessage.SIGNATURE_MUSIC_URL, "en")).thenReturn(SIGNATURE_MUSIC_URL);
        when(outboxService.getNextPendingMessage(PATIENT_ID)).thenReturn(null);
        String response = outboxController.play(request);

        assertTrue(response.contains(SIGNATURE_MUSIC_URL));
        assertTrue(response.contains("hangup"));
    }

    @Test
    public void shouldInvalidateSessionOnHangupEvent() {
        when(request.getParameter(OutboxController.EVENT_REQUEST_PARAM)).thenReturn("hangup");
        String response = outboxController.play(request);
        verify(session).invalidate();
        assertNull(response);
    }
}
