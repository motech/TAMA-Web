package org.motechproject.tama.ivr.action;

import org.apache.commons.lang.StringUtils;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.tama.ivr.IVR;
import org.motechproject.tama.ivr.IVRRequest;
import org.motechproject.tama.ivr.action.event.BaseActionTest;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

public class RetryActionTest extends BaseActionTest {
    private RetryAction retryAction;
    @Mock
    private UserNotAuthorisedAction userNotAuthorisedAction;
    private Integer maxAttempts;

    @Before
    public void setUp() {
        super.setUp();

        maxAttempts = 5;
        retryAction = new RetryAction(userNotAuthorisedAction, maxAttempts, messages);
    }

    @Test
    public void shouldGoToUserNotAuthorisedActionIfItIsTheLastAttempt() {
        IVRRequest ivrRequest = new IVRRequest("sid", "cid", "event", "1234#");
        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute(IVR.Attributes.NUMBER_OF_ATTEMPTS)).thenReturn(new Integer(4));
        when(userNotAuthorisedAction.handle(ivrRequest, request, response)).thenReturn("OK");

        String handle = retryAction.handle(ivrRequest, request, response);
        verify(userNotAuthorisedAction).handle(ivrRequest, request, response);
        assertEquals("OK", handle);
    }

    @Test
    public void shouldSendRequestForPinAgainIfItIsNotTheLastAttemptAndAlsoIncrementAttemptCount() {
        IVRRequest ivrRequest = new IVRRequest("sid", "cid", "event", "1234#");
        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute(IVR.Attributes.NUMBER_OF_ATTEMPTS)).thenReturn(null);

        String responseXML = retryAction.handle(ivrRequest, request, response);

        verify(session).setAttribute(IVR.Attributes.NUMBER_OF_ATTEMPTS, 1);
        assertEquals("<response sid=\"sid\"><collectdtmf><playaudio>http://music</playaudio></collectdtmf></response>", StringUtils.replace(responseXML, System.getProperty("line.separator"), ""));
    }

    @Test
    public void shouldSendRequestForPinAgainIfItIsNotTheLastAttemptAndNotIncrementAttemptCountWhenPassCodeIsNotSent() {
        IVRRequest ivrRequest = new IVRRequest("sid", "cid", "event", null);
        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute(IVR.Attributes.NUMBER_OF_ATTEMPTS)).thenReturn(null);

        String responseXML = retryAction.handle(ivrRequest, request, response);

        verify(session, never()).setAttribute(IVR.Attributes.NUMBER_OF_ATTEMPTS, 0);
        assertEquals("<response sid=\"sid\"><collectdtmf><playaudio>http://music</playaudio></collectdtmf></response>", StringUtils.replace(responseXML, System.getProperty("line.separator"), ""));
    }
}
