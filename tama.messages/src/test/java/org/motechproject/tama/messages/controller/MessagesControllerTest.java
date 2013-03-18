package org.motechproject.tama.messages.controller;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.ivr.kookoo.KooKooIVRContext;
import org.motechproject.ivr.kookoo.controller.StandardResponseController;
import org.motechproject.ivr.kookoo.service.KookooCallDetailRecordsService;
import org.motechproject.ivr.message.IVRMessage;
import org.motechproject.tama.ivr.context.TAMAIVRContext;
import org.motechproject.tama.messages.MessagesToBePushed;
import org.motechproject.tama.messages.domain.PushedMessage;
import org.motechproject.tama.outbox.context.OutboxContext;
import org.motechproject.util.Cookies;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.motechproject.ivr.kookoo.eventlogging.CallEventConstants.CALL_STATE;
import static org.motechproject.tama.ivr.domain.CallState.PUSH_MESSAGES_COMPLETE;

public class MessagesControllerTest {

    @Mock
    private KooKooIVRContext kookooIVRContext;
    @Mock
    private IVRMessage ivrMessage;
    @Mock
    private KookooCallDetailRecordsService callDetailRecordsService;
    @Mock
    private StandardResponseController standardResponseController;
    @Mock
    private MessagesToBePushed messagesToBePushed;
    @Mock
    private HttpSession httpSession;
    @Mock
    private Cookies cookies;

    private String patientId = "patientId";
    private MessagesController messagesController;

    @Before
    public void setup() {
        initMocks(this);
        setupCookies();
        setupSession();
        messagesController = new MessagesController(ivrMessage, callDetailRecordsService, standardResponseController, messagesToBePushed);
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
    public void shouldAddMessageToResponse() {
        when(cookies.getValue(TAMAIVRContext.LAST_PLAYED_HEALTH_TIP)).thenReturn("");
        when(cookies.getValue(OutboxContext.LAST_PLAYED_VOICE_MESSAGE_ID)).thenReturn("");

        messagesController.gotDTMF(kookooIVRContext);

        verify(messagesToBePushed).nextMessage(kookooIVRContext);
    }

    @Test
    public void shouldMarkMessageAsReadIfMessageIsAlreadyPlayed() {
        when(cookies.getValue(TAMAIVRContext.LAST_PLAYED_HEALTH_TIP)).thenReturn("healthTip");

        messagesController.gotDTMF(kookooIVRContext);

        verify(messagesToBePushed).markAsRead(kookooIVRContext, new PushedMessage(kookooIVRContext));
    }

    @Test
    public void shouldNotMarkMessageAsReadIfMessageIsNotAlreadyPlayed() {
        when(cookies.getValue(TAMAIVRContext.LAST_PLAYED_HEALTH_TIP)).thenReturn("");
        when(cookies.getValue(OutboxContext.LAST_PLAYED_VOICE_MESSAGE_ID)).thenReturn("");

        messagesController.gotDTMF(kookooIVRContext);

        verify(messagesToBePushed, never()).markAsRead(kookooIVRContext, new PushedMessage(kookooIVRContext));
    }

    @Test
    public void shouldSetCallStateToCompleteIfMessageIsAlreadyPlayed() {
        when(cookies.getValue(TAMAIVRContext.LAST_PLAYED_HEALTH_TIP)).thenReturn("healthTip");

        messagesController.gotDTMF(kookooIVRContext);

        verify(httpSession).setAttribute(CALL_STATE, PUSH_MESSAGES_COMPLETE.toString());
    }
}
