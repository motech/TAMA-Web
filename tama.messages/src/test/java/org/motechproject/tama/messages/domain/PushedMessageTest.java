package org.motechproject.tama.messages.domain;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.ivr.kookoo.KooKooIVRContext;
import org.motechproject.tama.ivr.context.TAMAIVRContext;
import org.motechproject.tama.outbox.context.OutboxContext;
import org.motechproject.util.Cookies;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class PushedMessageTest {

    @Mock
    private KooKooIVRContext kookooIVRContext;
    @Mock
    private Cookies cookies;

    private PushedMessage pushedMessage;

    @Before
    public void setup() {
        initMocks(this);
        setupIVRSession(kookooIVRContext);
        pushedMessage = new PushedMessage(kookooIVRContext);
    }

    @Test
    public void pushedMessageExistsWhenAnOutboxMessageWasPushed() {
        when(cookies.getValue(TAMAIVRContext.LAST_PLAYED_HEALTH_TIP)).thenReturn("message");
        assertTrue(pushedMessage.exists());
    }

    @Test
    public void pushedMessageExistsWhenHealthTipMessageWasPushed() {
        when(cookies.getValue(OutboxContext.LAST_PLAYED_VOICE_MESSAGE_ID)).thenReturn("message");
        assertTrue(pushedMessage.exists());
    }

    @Test
    public void messageTypeIsOutboxWhenNoMessageWasPlayed() {
        when(cookies.getValue(TAMAIVRContext.LAST_PLAYED_HEALTH_TIP)).thenReturn(null);
        assertEquals(PushedMessage.Types.OUTBOX, pushedMessage.type());
    }

    @Test
    public void messageTypeIsHealthTipWhenAHealthTipMessageWasPlayed() {
        when(cookies.getValue(TAMAIVRContext.LAST_PLAYED_HEALTH_TIP)).thenReturn("message");
        assertEquals(PushedMessage.Types.HEALTH_TIPS, pushedMessage.type());
    }

    private void setupIVRSession(KooKooIVRContext kookooIVRContext) {
        when(kookooIVRContext.cookies()).thenReturn(cookies);
    }
}
