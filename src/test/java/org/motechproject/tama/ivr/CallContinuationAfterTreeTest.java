package org.motechproject.tama.ivr;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.outbox.api.VoiceOutboxService;
import org.motechproject.server.service.ivr.*;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.powermock.api.mockito.PowerMockito.when;

public class CallContinuationAfterTreeTest {
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

    @Before
    public void setup() {
        initMocks(this);

        ivrContext = new IVRContext(ivrRequest, ivrSession);
        callContinuationAfterTree = new CallContinuationAfterTree(ivrMessage, voiceOutboxService);
    }

    @Test
    public void shouldHangupTheCallIfThereAreNoPendingOutboxMessages() {
        String SIGNATURE_MUSIC_URL = "signature_music.wav";

        final String PATIENT_ID = "123";
        when(ivrSession.getExternalId()).thenReturn(PATIENT_ID);
        when(voiceOutboxService.getNumberPendingMessages(PATIENT_ID)).thenReturn(0);
        when(ivrMessage.getSignatureMusic()).thenReturn(SIGNATURE_MUSIC_URL);

        callContinuationAfterTree.continueCall(ivrContext, ivrResponseBuilder);

        verify(ivrResponseBuilder, times(1)).withPlayAudios(SIGNATURE_MUSIC_URL);
        verify(ivrResponseBuilder, times(1)).withHangUp();
    }

    @Test
    public void shouldContinueToOutboxIfThereArePendingMessages() {
        final String PATIENT_ID = "123";
        when(ivrSession.getExternalId()).thenReturn(PATIENT_ID);
        when(voiceOutboxService.getNumberPendingMessages(PATIENT_ID)).thenReturn(1);
        final String OUTBOX_LOCATION = "http://server/tama/outbox";
        when(ivrMessage.getText(TamaIVRMessage.OUTBOX_LOCATION_URL)).thenReturn(OUTBOX_LOCATION);

        callContinuationAfterTree.continueCall(ivrContext, ivrResponseBuilder);

        verify(ivrResponseBuilder, times(1)).withNextUrl(OUTBOX_LOCATION);
        verify(ivrResponseBuilder, times(1)).withPlayAudios(TamaIVRMessage.CONTINUE_TO_OUTBOX);
    }

}