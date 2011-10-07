package org.motechproject.tama.web;

import org.custommonkey.xmlunit.XMLUnit;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.server.service.ivr.IVRSession;
import org.motechproject.tama.ivr.TamaIVRMessage;
import org.motechproject.tama.util.TamaSessionUtil;
import org.xml.sax.SAXException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.IOException;

import static org.custommonkey.xmlunit.XMLAssert.assertXMLEqual;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class PostOutboxControllerTest {

    private PostOutboxController postOutboxController;

    private static final String NO_MESSAGES = "noMessages";
    public static final String SIGNATURE_MUSIC = "signatureMusic";
    private static final String THOSE_WERE_YOUR_MESSAGES = "thoseWereYourMessages";
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

        when(tamaIvrMessage.getSignatureMusic()).thenReturn(TamaIVRMessage.SIGNATURE_MUSIC);
        when(tamaIvrMessage.getWav(TamaIVRMessage.SIGNATURE_MUSIC, "en")).thenReturn(SIGNATURE_MUSIC);
        when(tamaIvrMessage.getWav(TamaIVRMessage.NO_MESSAGES, "en")).thenReturn(NO_MESSAGES);
        when(tamaIvrMessage.getWav(TamaIVRMessage.THOSE_WERE_YOUR_MESSAGES, "en")).thenReturn(THOSE_WERE_YOUR_MESSAGES);
        when(tamaIvrMessage.getWav(TamaIVRMessage.HANGUP_OR_MAIN_MENU, "en")).thenReturn(HANGUP_OR_MAIN_MENU);
        XMLUnit.setIgnoreWhitespace(true);
    }

    /* patient selected the menu option to listen to outbox messages */

        /* patient had outbox messages
               * patient has listened to all outbox messages */
        @Test
        public void shouldPlay_HangupOrWaitForMoreOptions_AndThenHangup() throws IOException, SAXException {
            when(session.getAttribute(TamaSessionUtil.TamaSessionAttribute.POST_TREE_CALL_CONTINUE)).thenReturn(null);
            when(session.getAttribute(TamaSessionUtil.TamaSessionAttribute.LAST_PLAYED_VOICE_MESSAGE_ID)).thenReturn("lastMessage");
            String response = postOutboxController.play(request);
            assertXMLEqual("comparing xml response", "<response><playaudio>hangupOrMainMenu</playaudio><playaudio>signatureMusic</playaudio><hangup/></response>", response);
        }

        /*  Patient had no outbox messages */
        @Test
        public void shouldPlay_NoMoreMessages_HangupOrWaitForMoreOptions_AndThenHangup() throws IOException, SAXException {
            when(session.getAttribute(TamaSessionUtil.TamaSessionAttribute.POST_TREE_CALL_CONTINUE)).thenReturn(null);
            when(session.getAttribute(TamaSessionUtil.TamaSessionAttribute.LAST_PLAYED_VOICE_MESSAGE_ID)).thenReturn(null);

            String response = postOutboxController.play(request);
            assertXMLEqual("comparing xml response", "<response><playaudio>noMessages</playaudio><playaudio>hangupOrMainMenu</playaudio><playaudio>signatureMusic</playaudio><hangup/></response>", response);
        }

    /* patient was directed to outbox at the end of a call*/
    @Test
    public void shouldPlay_ThoseWereYourMessages_AndHangup() throws IOException, SAXException {
        when(session.getAttribute(TamaSessionUtil.TamaSessionAttribute.POST_TREE_CALL_CONTINUE)).thenReturn("true");

        String response = postOutboxController.play(request);
        assertXMLEqual("comparing xml response", "<response><playaudio>thoseWereYourMessages</playaudio><playaudio>signatureMusic</playaudio></response>", response);
    }
}
