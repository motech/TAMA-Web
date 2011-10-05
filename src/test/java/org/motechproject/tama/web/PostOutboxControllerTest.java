package org.motechproject.tama.web;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.server.service.ivr.IVRRequest;
import org.motechproject.server.service.ivr.IVRSession;
import org.motechproject.tama.ivr.TamaIVRMessage;
import org.motechproject.tama.util.TamaSessionUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import static junit.framework.Assert.assertTrue;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class PostOutboxControllerTest {

    private PostOutboxController postOutboxController;

    private static final String NO_MESSAGES_FOR_NOW = "noMessagesForNow";
    private static final String HANGUP_OR_MAIN_MENU = "hangupOrMainMenu";

    @Mock
    private HttpSession session;
    @Mock
    private TamaIVRMessage tamaIvrMessage;
    @Mock
    private HttpServletRequest request;

    @Before
    public void setUp() {
        initMocks(this);
        postOutboxController = new PostOutboxController(tamaIvrMessage);
        when(request.getSession()).thenReturn(session);
        when(session.getAttribute(IVRSession.IVRCallAttribute.PREFERRED_LANGUAGE_CODE)).thenReturn("en");
    }

    @Test
    public void shouldPlayNoMoreMessagesAndHangup_AfterPatientListensToAllOutboxMessagesDuringPillReminderCall() {
        when(session.getAttribute(TamaSessionUtil.TamaSessionAttribute.CALL_DIRECTION)).thenReturn(IVRRequest.CallDirection.Outbound);
        when(tamaIvrMessage.getWav(TamaIVRMessage.NO_MESSAGES_FOR_NOW, "en")).thenReturn(NO_MESSAGES_FOR_NOW);

        String response = postOutboxController.play(request);
        assertTrue(response.contains(NO_MESSAGES_FOR_NOW));
    }

    @Test
    public void shouldPlayMainMenuOrHangup_AfterPatientListensToAllOutboxMessagesDuringIncomingCall() {
        when(session.getAttribute(TamaSessionUtil.TamaSessionAttribute.CALL_DIRECTION)).thenReturn(IVRRequest.CallDirection.Inbound);
        when(tamaIvrMessage.getWav(TamaIVRMessage.HANGUP_OR_MAIN_MENU, "en")).thenReturn(HANGUP_OR_MAIN_MENU);

        String response = postOutboxController.play(request);
        assertTrue(response.contains(HANGUP_OR_MAIN_MENU));
        assertTrue(response.contains("hangup"));
    }

}
