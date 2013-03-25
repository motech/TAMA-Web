package org.motechproject.tama.messages.controller;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.ivr.kookoo.KooKooIVRContext;
import org.motechproject.ivr.kookoo.KookooIVRResponseBuilder;
import org.motechproject.tama.ivr.context.TAMAIVRContext;
import org.motechproject.tama.messages.domain.PlayedMessage;
import org.motechproject.tama.messages.service.Messages;
import org.motechproject.tama.outbox.context.OutboxContext;
import org.motechproject.util.Cookies;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

public class PushMessagesControllerTest {

    @Mock
    private KooKooIVRContext kookooIVRContext;
    @Mock
    private HttpSession httpSession;
    @Mock
    private Cookies cookies;
    @Mock
    private Messages messages;

    private String patientId = "patientId";

    private PushMessagesController messagesController;

    @Before
    public void setup() {
        initMocks(this);
        setupSession();
        setupCookies();
        messagesController = new PushMessagesController(messages);
    }

    private void setupSession() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getSession()).thenReturn(httpSession);
        when(kookooIVRContext.httpRequest()).thenReturn(request);
        when(httpSession.getAttribute(TAMAIVRContext.PATIENT_ID)).thenReturn(patientId);
    }

    private void setupCookies() {
        when(kookooIVRContext.cookies()).thenReturn(cookies);
    }

    @Test
    public void shouldMarkMessageAsReadIfMessageIsAlreadyPlayed() {
        when(cookies.getValue(TAMAIVRContext.LAST_PLAYED_HEALTH_TIP)).thenReturn("healthTip");

        boolean shouldContinue = messagesController.markAsReadAndContinue(kookooIVRContext);

        assertFalse(shouldContinue);
        verify(messages).markAsRead(kookooIVRContext, new PlayedMessage(kookooIVRContext));
    }

    @Test
    public void shouldNotMarkMessageAsReadIfMessageIsNotAlreadyPlayed() {
        when(cookies.getValue(TAMAIVRContext.LAST_PLAYED_HEALTH_TIP)).thenReturn("");
        when(cookies.getValue(OutboxContext.LAST_PLAYED_VOICE_MESSAGE_ID)).thenReturn("");
        when(messages.nextMessage(kookooIVRContext)).thenReturn(new KookooIVRResponseBuilder());

        boolean shouldContinue = messagesController.markAsReadAndContinue(kookooIVRContext);

        assertTrue(shouldContinue);
        verify(messages, never()).markAsRead(kookooIVRContext, new PlayedMessage(kookooIVRContext));
    }

    @Test
    public void shouldAddMessageToResponse() {
        when(cookies.getValue(TAMAIVRContext.LAST_PLAYED_HEALTH_TIP)).thenReturn("");
        when(cookies.getValue(OutboxContext.LAST_PLAYED_VOICE_MESSAGE_ID)).thenReturn("");
        when(messages.nextMessage(kookooIVRContext)).thenReturn(new KookooIVRResponseBuilder());

        messagesController.gotDTMF(kookooIVRContext);

        verify(messages).nextMessage(kookooIVRContext);
    }
}
