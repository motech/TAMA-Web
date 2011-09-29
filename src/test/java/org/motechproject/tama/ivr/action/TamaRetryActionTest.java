package org.motechproject.tama.ivr.action;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.server.service.ivr.IVREvent;
import org.motechproject.server.service.ivr.IVRRequest;
import org.motechproject.server.service.ivr.IVRSession.IVRCallAttribute;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

public class TamaRetryActionTest extends BaseActionTest {
    private TamaRetryAction retryAction;
    @Mock
    private TamaUserNotAuthorisedAction userNotAuthorisedAction;
    private Integer maxAttempts;
    @Mock
    IVRRequest ivrRequest;

    @Before
    public void setUp() {
        super.setUp();

        maxAttempts = 5;
        retryAction = new TamaRetryAction(userNotAuthorisedAction, maxAttempts, messages);
    }

    @Test
    public void shouldGoToUserNotAuthorisedActionIfItIsTheLastAttempt() {
        when(ivrRequest.getSid()).thenReturn("sid");
        when(ivrRequest.getCid()).thenReturn("cid");
        when(ivrRequest.getEvent()).thenReturn(IVREvent.GOT_DTMF.name());
        when(ivrRequest.getData()).thenReturn("1234#");
        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute(IVRCallAttribute.PREFERRED_LANGUAGE_CODE)).thenReturn("en");
        when(session.getAttribute(IVRCallAttribute.NUMBER_OF_ATTEMPTS)).thenReturn(4);
        when(userNotAuthorisedAction.createResponse(ivrRequest, request, response)).thenReturn("OK");

        String handle = retryAction.createResponse(ivrRequest, request, response);
        verify(userNotAuthorisedAction).createResponse(ivrRequest, request, response);
        assertEquals("OK", handle);
    }

    @Test
    public void shouldSendRequestForPinAgainIfItIsNotTheLastAttemptAndAlsoIncrementAttemptCount() {
        when(ivrRequest.getSid()).thenReturn("sid");
        when(ivrRequest.getCid()).thenReturn("cid");
        when(ivrRequest.getEvent()).thenReturn(IVREvent.GOT_DTMF.name());
        when(ivrRequest.getData()).thenReturn("1234#");
        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute(IVRCallAttribute.PREFERRED_LANGUAGE_CODE)).thenReturn("en");
        when(session.getAttribute(IVRCallAttribute.NUMBER_OF_ATTEMPTS)).thenReturn(null);
        when(messages.getWav(SIGNATURE_MUSIC, "en")).thenReturn(SIGNATURE_MUSIC);
        String responseXML = retryAction.createResponse(ivrRequest, request, response);

        verify(session).setAttribute(IVRCallAttribute.NUMBER_OF_ATTEMPTS, 1);
        assertEquals("<response sid=\"sid\"><collectdtmf><playaudio>http://music</playaudio></collectdtmf></response>", sanitize(responseXML));
    }

    @Test
    public void shouldSendRequestForPinAgainIfItIsNotTheLastAttemptAndNotIncrementAttemptCountWhenPassCodeIsNotSent() {
        when(ivrRequest.getSid()).thenReturn("sid");
        when(ivrRequest.getCid()).thenReturn("cid");
        when(ivrRequest.getEvent()).thenReturn(IVREvent.GOT_DTMF.name());
        when(ivrRequest.getData()).thenReturn(null);
        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute(IVRCallAttribute.PREFERRED_LANGUAGE_CODE)).thenReturn("en");
        when(session.getAttribute(IVRCallAttribute.NUMBER_OF_ATTEMPTS)).thenReturn(null);
        when(messages.getWav(SIGNATURE_MUSIC, "en")).thenReturn(SIGNATURE_MUSIC);

        String responseXML = retryAction.createResponse(ivrRequest, request, response);

        verify(session, never()).setAttribute(IVRCallAttribute.NUMBER_OF_ATTEMPTS, 0);
        assertEquals("<response sid=\"sid\"><collectdtmf><playaudio>http://music</playaudio></collectdtmf></response>", sanitize(responseXML));
    }
}
