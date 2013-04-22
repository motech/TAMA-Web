package org.motechproject.tama.messages.service;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.ivr.kookoo.KooKooIVRContext;
import org.motechproject.ivr.kookoo.KookooIVRResponseBuilder;
import org.motechproject.tama.common.domain.TAMAMessageType;
import org.motechproject.tama.ivr.context.TAMAIVRContext;
import org.motechproject.tama.messages.domain.Method;
import org.motechproject.tama.messages.domain.PlayedMessage;
import org.motechproject.tama.messages.provider.MessageProviders;
import org.motechproject.util.Cookies;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import static junit.framework.Assert.assertTrue;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

public class MessagesTest {

    private Method method = Method.PULL;

    @Mock
    private HealthTipMessage pushedHealthTipsMessage;
    @Mock
    private MessageProviders messageProviders;
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
        messages = new Messages(messageProviders, pushedHealthTipsMessage);
    }

    @Test
    public void shouldAddMessagesToResponse() {
        KookooIVRResponseBuilder message = new KookooIVRResponseBuilder().withPlayAudios("message");

        when(this.messageProviders.hasAnyMessage(eq(method), any(TAMAIVRContext.class), any(TAMAMessageType.class))).thenReturn(true);
        when(this.messageProviders.getResponse(eq(method), any(TAMAIVRContext.class), any(TAMAMessageType.class))).thenReturn(message);

        KookooIVRResponseBuilder response = messages.nextMessage(method, kookooIVRContext, null);
        assertTrue(response.getPlayAudios().contains("message"));
    }

    @Test
    public void shouldAddHealthTipsToResponse() {
        KookooIVRResponseBuilder healthTipMessage = new KookooIVRResponseBuilder().withPlayAudios("healthTipMessage");

        when(pushedHealthTipsMessage.hasAnyMessage(kookooIVRContext, null)).thenReturn(true);
        when(pushedHealthTipsMessage.getResponse(kookooIVRContext, null)).thenReturn(healthTipMessage);

        KookooIVRResponseBuilder response = messages.nextMessage(method, kookooIVRContext, null);
        assertTrue(response.getPlayAudios().contains("healthTipMessage"));
    }

    @Test
    public void shouldReturnEmptyResponseWhenThereAreNoMessages() {
        KookooIVRResponseBuilder response = messages.nextMessage(method, kookooIVRContext, null);
        assertTrue(response.getPlayAudios().isEmpty());
    }

    @Test
    public void shouldAddMessageAsReadWhenLastPlayedMessageIsOutboxMessage() {
        PlayedMessage playedMessage = new PlayedMessage(kookooIVRContext);
        when(cookies.getValue(TAMAIVRContext.TAMA_MESSAGE_TYPE)).thenReturn("messageType");
        when(playedMessage.id()).thenReturn("messageId");

        messages.markAsRead(method, kookooIVRContext, playedMessage);
        verify(messageProviders).markAsRead(method, "messageType", "messageId");
    }

    @Test
    public void shouldMarkHealthTipAsReadWhenLastPlayedMessageIsHealthTip() {
        PlayedMessage playedMessage = new PlayedMessage(kookooIVRContext);
        String playedHealthTip = "healthTip";

        when(cookies.getValue(TAMAIVRContext.LAST_PLAYED_HEALTH_TIP)).thenReturn(playedHealthTip);
        messages.markAsRead(method, kookooIVRContext, playedMessage);
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
