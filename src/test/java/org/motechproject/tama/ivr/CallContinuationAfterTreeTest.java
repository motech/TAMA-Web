package org.motechproject.tama.ivr;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.outbox.api.VoiceOutboxService;
import org.motechproject.server.service.ivr.*;

import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.same;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.powermock.api.mockito.PowerMockito.when;

public class CallContinuationAfterTreeTest {

    public static final String SIGNATURE_MUSIC_URL = "signature_music.wav";
    public static final String OUTBOX_LOCATION = "http://server/tama/outbox";
    private static final String NO_MESSAGES_FOR_NOW_URL = "http://localhost/tama/en/noMessage.wav";

    @Mock
    private IVRResponseBuilder ivrResponseBuilder;

    @Mock
    private IVRMessage ivrMessage;

    @Mock
    private VoiceOutboxService voiceOutboxService;

    @Mock
    private IVRSession ivrSession;

    @Mock
    private IVRRequest ivrRequest;

    private IVRContext ivrContext;

    private CallContinuationAfterTree callContinuationAfterTree;

    private String patient_id = "123";

    @Before
    public void setup() {
        initMocks(this);

        when(ivrSession.getExternalId()).thenReturn(patient_id);
        ivrContext = new IVRContext(ivrRequest, ivrSession);
        callContinuationAfterTree = new CallContinuationAfterTree(ivrMessage, voiceOutboxService);
    }

    @Test
    public void shouldPlayNoOutboxMessagesForNow_WhenThereAreNoPendingOutboxMessages() {
        when(voiceOutboxService.getNumberPendingMessages(patient_id)).thenReturn(0);
        when(ivrMessage.getWav(same(TamaIVRMessage.NO_MESSAGES_FOR_NOW), anyString())).thenReturn(NO_MESSAGES_FOR_NOW_URL);

        callContinuationAfterTree.continueCall(ivrContext, ivrResponseBuilder);

        verify(ivrResponseBuilder, times(1)).withPlayAudios(TamaIVRMessage.NO_MESSAGES_FOR_NOW);
        verify(ivrResponseBuilder, times(1)).withHangUp();
    }

    @Test
    public void shouldPlaySignatureMusic_AfterPlayingNoOutboxMessagesForNow() {
        when(voiceOutboxService.getNumberPendingMessages(patient_id)).thenReturn(0);
        when(ivrMessage.getSignatureMusic()).thenReturn(TamaIVRMessage.SIGNATURE_MUSIC);

        callContinuationAfterTree.continueCall(ivrContext, ivrResponseBuilder);

        verify(ivrResponseBuilder, times(1)).withPlayAudios(TamaIVRMessage.SIGNATURE_MUSIC);
        verify(ivrResponseBuilder, times(1)).withHangUp();
    }

    @Test
    public void shouldPlayOutboxMessages_WhenThereArePendingOutboxMessages() {
        when(voiceOutboxService.getNumberPendingMessages(patient_id)).thenReturn(1);
        when(ivrMessage.getText(TamaIVRMessage.OUTBOX_LOCATION_URL)).thenReturn(OUTBOX_LOCATION);

        callContinuationAfterTree.continueCall(ivrContext, ivrResponseBuilder);

        verify(ivrResponseBuilder, times(1)).withNextUrl(OUTBOX_LOCATION);
        verify(ivrResponseBuilder, times(1)).withPlayAudios(TamaIVRMessage.CONTINUE_TO_OUTBOX);
    }

}