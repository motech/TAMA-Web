package org.motechproject.tama.messages.controller;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.ivr.kookoo.KooKooIVRContext;
import org.motechproject.ivr.kookoo.KookooIVRResponseBuilder;
import org.motechproject.ivr.kookoo.controller.StandardResponseController;
import org.motechproject.ivr.kookoo.service.KookooCallDetailRecordsService;
import org.motechproject.ivr.message.IVRMessage;
import org.motechproject.tama.messages.PushedHealthTipMessage;
import org.motechproject.tama.messages.PushedOutboxMessage;
import org.motechproject.util.Cookies;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

public class MessagesControllerTest {

    @Mock
    private IVRMessage ivrMessage;
    @Mock
    private KookooCallDetailRecordsService callDetailRecordService;
    @Mock
    private StandardResponseController standardResponseController;
    @Mock
    private PushedOutboxMessage pushedOutboxMessage;
    @Mock
    private PushedHealthTipMessage pushedHealthTipMessage;
    @Mock
    private KooKooIVRContext kookooIVRContext;

    private MessagesController messagesController;

    @Before
    public void setup() {
        initMocks(this);
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpSession httpSession = mock(HttpSession.class);
        Cookies cookies = mock(Cookies.class);

        when(request.getSession()).thenReturn(httpSession);
        when(kookooIVRContext.httpRequest()).thenReturn(request);
        when(kookooIVRContext.cookies()).thenReturn(cookies);
        messagesController = new MessagesController(ivrMessage, callDetailRecordService, standardResponseController, pushedOutboxMessage, pushedHealthTipMessage);
    }

    @Test
    public void shouldPlayOutboxMessages() {
        when(pushedOutboxMessage.addToResponse(any(KookooIVRResponseBuilder.class), eq(kookooIVRContext))).thenReturn(true);
        messagesController.gotDTMF(kookooIVRContext);
        verify(pushedHealthTipMessage, never()).addToResponse(any(KookooIVRResponseBuilder.class), eq(kookooIVRContext));
    }

    @Test
    public void shouldPlayHealthTipMessageWhenThereAreNoOutboxMessages() {
        when(pushedOutboxMessage.addToResponse(any(KookooIVRResponseBuilder.class), eq(kookooIVRContext))).thenReturn(false);
        messagesController.gotDTMF(kookooIVRContext);
        verify(pushedHealthTipMessage).addToResponse(any(KookooIVRResponseBuilder.class), eq(kookooIVRContext));
    }
}
