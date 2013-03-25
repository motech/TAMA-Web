package org.motechproject.tama.messages.service;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.ivr.kookoo.KooKooIVRContext;
import org.motechproject.ivr.kookoo.KookooIVRResponseBuilder;
import org.motechproject.tama.ivr.context.TAMAIVRContext;
import org.motechproject.tama.messages.domain.PlayedMessage;
import org.motechproject.util.Cookies;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import static junit.framework.Assert.assertTrue;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

public class MessagesTest {

    @Mock
    private HealthTipMessage pushedHealthTipsMessage;
    @Mock
    private OutboxMessage outboxMessage;
    @Mock
    private KooKooIVRContext kookooIVRContext;
    @Mock
    private HttpSession httpSession;
    @Mock
    private Cookies cookies;

    private Messages messages;

    @Before
    public void setup() {
        initMocks(this);
        setupCookies();
        setupSession();
        messages = new Messages(outboxMessage, pushedHealthTipsMessage);
    }

    @Test
    public void shouldAddOutboxMessageToResponse() {
        KookooIVRResponseBuilder outboxMessage = new KookooIVRResponseBuilder().withPlayAudios("outboxMessage");

        when(this.outboxMessage.hasAnyMessage(kookooIVRContext)).thenReturn(true);
        when(this.outboxMessage.getResponse(kookooIVRContext)).thenReturn(outboxMessage);

        KookooIVRResponseBuilder response = messages.nextMessage(kookooIVRContext);
        assertTrue(response.getPlayAudios().contains("outboxMessage"));
    }

    @Test
    public void shouldAddHealthTipsToResponse() {
        KookooIVRResponseBuilder healthTipMessage = new KookooIVRResponseBuilder().withPlayAudios("healthTipMessage");

        when(pushedHealthTipsMessage.hasAnyMessage(kookooIVRContext, null)).thenReturn(true);
        when(pushedHealthTipsMessage.getResponse(kookooIVRContext, null)).thenReturn(healthTipMessage);

        KookooIVRResponseBuilder response = messages.nextMessage(kookooIVRContext);
        assertTrue(response.getPlayAudios().contains("healthTipMessage"));
    }

    @Test
    public void shouldReturnEmptyResponseWhenThereAreNoMessages() {
        KookooIVRResponseBuilder response = messages.nextMessage(kookooIVRContext);
        assertTrue(response.getPlayAudios().isEmpty());
    }

    @Test
    public void shouldMarkOutboxMessageAsReadWhenLastPlayedMessageIsOutboxMessage() {
        PlayedMessage playedMessage = new PlayedMessage(kookooIVRContext);

        messages.markAsRead(kookooIVRContext, playedMessage);
        verify(outboxMessage).markAsRead(kookooIVRContext);
    }

    @Test
    public void shouldMarkHealthTipAsReadWhenLastPlayedMessageIsHealthTip() {
        PlayedMessage playedMessage = new PlayedMessage(kookooIVRContext);
        String playedHealthTip = "healthTip";

        when(cookies.getValue(TAMAIVRContext.LAST_PLAYED_HEALTH_TIP)).thenReturn(playedHealthTip);
        messages.markAsRead(kookooIVRContext, playedMessage);
        verify(pushedHealthTipsMessage).markAsRead(anyString(), eq(playedHealthTip));
    }


    private void setupCookies() {
        when(kookooIVRContext.cookies()).thenReturn(cookies);
    }

    private void setupSession() {
        HttpServletRequest httpRequest = mock(HttpServletRequest.class);
        when(httpRequest.getSession()).thenReturn(httpSession);
        when(kookooIVRContext.httpRequest()).thenReturn(httpRequest);
    }
}
