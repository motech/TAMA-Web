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
import static org.mockito.MockitoAnnotations.initMocks;

public class RetryActionTest extends BaseActionTest {
    private RetryAction retryAction;
    @Mock
    private UserNotAuthorisedAction userNotAuthorisedAction;
    private Integer maxAttempts;

    @Before
    public void setUp() {
        initMocks(this);
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

        verify(session).invalidate();
        verify(userNotAuthorisedAction).handle(ivrRequest, request, response);
        assertEquals("OK", handle);
    }

    @Test
    public void shouldSendRequestForPinAgainIfItIsNotTheLastAttemptAndAlsoIncrementAttemptCount() {
        IVRRequest ivrRequest = new IVRRequest("sid", "cid", "event", "1234#");
        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute(IVR.Attributes.NUMBER_OF_ATTEMPTS)).thenReturn(null);
        when(messages.get(IVR.MessageKey.TAMA_IVR_ASK_FOR_PIN_AFTER_FAILURE)).thenReturn("failed");

        String handle = retryAction.handle(ivrRequest, request, response);

        verify(session).setAttribute(IVR.Attributes.NUMBER_OF_ATTEMPTS, 1);
        assertEquals("<response sid=\"sid\"><collectdtmf><playtext>failed</playtext></collectdtmf></response>", StringUtils.replace(handle, "\n", ""));
    }

    @Test
    public void shouldSendRequestForPinAgainIfItIsNotTheLastAttemptAndNotIncrementAttemptCountWhenPassCodeIsNotSent() {
        IVRRequest ivrRequest = new IVRRequest("sid", "cid", "event", null);
        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute(IVR.Attributes.NUMBER_OF_ATTEMPTS)).thenReturn(null);
        when(messages.get(IVR.MessageKey.TAMA_IVR_REMIND_FOR_PIN)).thenReturn("please enter you pin");

        String handle = retryAction.handle(ivrRequest, request, response);

        verify(session, never()).setAttribute(IVR.Attributes.NUMBER_OF_ATTEMPTS, 0);
        assertEquals("<response sid=\"sid\"><collectdtmf><playtext>please enter you pin</playtext></collectdtmf></response>", StringUtils.replace(handle, "\n", ""));
    }
}
