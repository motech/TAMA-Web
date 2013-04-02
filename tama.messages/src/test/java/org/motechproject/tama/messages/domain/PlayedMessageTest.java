package org.motechproject.tama.messages.domain;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.ivr.kookoo.KooKooIVRContext;
import org.motechproject.ivr.kookoo.eventlogging.CallEventConstants;
import org.motechproject.tama.ivr.context.TAMAIVRContext;
import org.motechproject.tama.ivr.domain.CallState;
import org.motechproject.tama.outbox.context.OutboxContext;
import org.motechproject.util.Cookies;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

public class PlayedMessageTest {

    @Mock
    private KooKooIVRContext kookooIVRContext;
    @Mock
    private Cookies cookies;
    @Mock
    private HttpSession httpSession;


    private PlayedMessage playedMessage;

    @Before
    public void setup() {
        initMocks(this);
        setupIVRSession(kookooIVRContext);
        playedMessage = new PlayedMessage(kookooIVRContext);
    }

    @Test
    public void pushedMessageExistsWhenAnOutboxMessageWasPushed() {
        when(cookies.getValue(TAMAIVRContext.LAST_PLAYED_HEALTH_TIP)).thenReturn("message");
        assertTrue(playedMessage.exists());
    }

    @Test
    public void pushedMessageExistsWhenHealthTipMessageWasPushed() {
        when(cookies.getValue(OutboxContext.LAST_PLAYED_VOICE_MESSAGE_ID)).thenReturn("message");
        assertTrue(playedMessage.exists());
    }

    @Test
    public void messageTypeIsOutboxWhenNoMessageWasPlayed() {
        when(cookies.getValue(TAMAIVRContext.LAST_PLAYED_HEALTH_TIP)).thenReturn(null);
        assertEquals(PlayedMessage.Types.MESSAGES, playedMessage.type());
    }

    @Test
    public void messageTypeIsHealthTipWhenAHealthTipMessageWasPlayed() {
        when(cookies.getValue(TAMAIVRContext.LAST_PLAYED_HEALTH_TIP)).thenReturn("message");
        assertEquals(PlayedMessage.Types.HEALTH_TIPS, playedMessage.type());
    }

    @Test
    public void shouldSetCallStateToAuthenticatedOnReset() {
        playedMessage.reset();
        verify(httpSession).setAttribute(CallEventConstants.CALL_STATE, CallState.AUTHENTICATED.name());
    }

    @Test
    public void shouldResetMessageCategoryOnReset() {
        playedMessage.reset();
        verify(cookies).add(TAMAIVRContext.MESSAGE_CATEGORY_NAME, "");
    }

    private void setupIVRSession(KooKooIVRContext kookooIVRContext) {
        HttpServletRequest request = mock(HttpServletRequest.class);

        when(request.getSession()).thenReturn(httpSession);
        when(kookooIVRContext.httpRequest()).thenReturn(request);
        when(kookooIVRContext.cookies()).thenReturn(cookies);
    }
}
