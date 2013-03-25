package org.motechproject.tama.messages.controller;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.ivr.kookoo.KooKooIVRContext;
import org.motechproject.ivr.kookoo.KookooIVRResponseBuilder;
import org.motechproject.ivr.kookoo.controller.StandardResponseController;
import org.motechproject.ivr.kookoo.service.KookooCallDetailRecordsService;
import org.motechproject.ivr.message.IVRMessage;
import org.motechproject.tama.ivr.domain.CallState;
import org.motechproject.tama.messages.service.Messages;
import org.motechproject.util.Cookies;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.motechproject.ivr.kookoo.eventlogging.CallEventConstants.CALL_STATE;
import static org.motechproject.tama.ivr.domain.CallState.PULL_MESSAGES_TREE;
import static org.motechproject.tama.ivr.domain.CallState.PUSH_MESSAGES_COMPLETE;

public class MessagesControllerTest {

    @Mock
    private KooKooIVRContext kookooIVRContext;
    @Mock
    private HttpSession httpSession;
    @Mock
    private Cookies cookies;
    @Mock
    private IVRMessage ivrMessage;
    @Mock
    private KookooCallDetailRecordsService callDetailRecordsService;
    @Mock
    private StandardResponseController standardResponseController;
    @Mock
    private Messages messages;
    @Mock
    private PullMessagesController pullMessagesController;
    @Mock
    private PushMessagesController pushMessagesController;

    private MessagesController messagesController;

    @Before
    public void setup() {
        initMocks(this);
        setupCookies();
        setupSession();
        messagesController = new MessagesController(ivrMessage, callDetailRecordsService, standardResponseController, pullMessagesController, pushMessagesController);
    }

    private void setupSession() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getSession()).thenReturn(httpSession);
        when(kookooIVRContext.httpRequest()).thenReturn(request);
    }

    private void setupCookies() {
        when(kookooIVRContext.cookies()).thenReturn(cookies);
    }

    @Test
    public void shouldPlayTheNextPulledMessage() {
        when(httpSession.getAttribute(CALL_STATE)).thenReturn(CallState.PULL_MESSAGES.name());
        when(pullMessagesController.markAsReadAndContinue(kookooIVRContext)).thenReturn(true);
        when(pullMessagesController.gotDTMF(kookooIVRContext)).thenReturn(new KookooIVRResponseBuilder());

        messagesController.gotDTMF(kookooIVRContext);
        verify(pullMessagesController).gotDTMF(kookooIVRContext);
    }

    @Test
    public void shouldNotPlayPulledMessageWhenMarkAsReadAndContinueIsFalse() {
        when(httpSession.getAttribute(CALL_STATE)).thenReturn(CallState.PULL_MESSAGES.name());
        when(pullMessagesController.markAsReadAndContinue(kookooIVRContext)).thenReturn(false);

        KookooIVRResponseBuilder response = messagesController.gotDTMF(kookooIVRContext);
        verify(pullMessagesController, never()).gotDTMF(kookooIVRContext);
        assertTrue(response.isEmpty());
    }

    @Test
    public void shouldPlayTheNextPushedMessage() {
        when(pushMessagesController.markAsReadAndContinue(kookooIVRContext)).thenReturn(true);
        when(pushMessagesController.gotDTMF(kookooIVRContext)).thenReturn(new KookooIVRResponseBuilder());

        messagesController.gotDTMF(kookooIVRContext);
        verify(pushMessagesController).gotDTMF(kookooIVRContext);
    }

    @Test
    public void shouldNotPlayPushMessageIfMarkAsReadAndContinueIsFalse() {
        when(pushMessagesController.markAsReadAndContinue(kookooIVRContext)).thenReturn(false);

        KookooIVRResponseBuilder response = messagesController.gotDTMF(kookooIVRContext);
        verify(pushMessagesController, never()).gotDTMF(kookooIVRContext);
        assertTrue(response.isEmpty());
    }

    @Test
    public void shouldSetPullMessagesAsCompleteIfMessageIsAlreadyPlayed() {
        when(httpSession.getAttribute(CALL_STATE)).thenReturn(CallState.PULL_MESSAGES.name());
        when(pullMessagesController.markAsReadAndContinue(kookooIVRContext)).thenReturn(false);

        messagesController.gotDTMF(kookooIVRContext);
        verify(httpSession).setAttribute(CALL_STATE, PULL_MESSAGES_TREE.toString());
    }

    @Test
    public void shouldSetPushMessageAsCompleteIfMessageIsAlreadyPlayed() {
        when(pushMessagesController.markAsReadAndContinue(kookooIVRContext)).thenReturn(false);

        messagesController.gotDTMF(kookooIVRContext);
        verify(httpSession).setAttribute(CALL_STATE, PUSH_MESSAGES_COMPLETE.toString());
    }
}
