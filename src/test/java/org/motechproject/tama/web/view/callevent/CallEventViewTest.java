package org.motechproject.tama.web.view.callevent;

import org.junit.Before;
import org.junit.Test;
import org.motechproject.ivr.kookoo.eventlogging.CallEventConstants;
import org.motechproject.server.service.ivr.CallEvent;
import org.motechproject.tama.web.view.CallEventView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

public class CallEventViewTest {

    private CallEventView callEventView;

    private Map<String, String> data;

    @Before
    public void setUp() {
        data = new HashMap<String, String>();
    }

    @Test
    public void hasInputWhenEventTypeIsGotDtmf() {
        callEventView = new CallEventView(new CallEvent("gotDtmf", data));
        assertTrue(callEventView.isUserInputAvailable());
    }

    @Test
    public void shouldReturnInputDtmf_IfDtmfDataAvilable() {
        data.put(CallEventConstants.DTMF_DATA, "1");
        callEventView = new CallEventView(new CallEvent("gotDtmf", data));
        assertEquals("1", callEventView.getUserInput());
    }

    @Test
    public void shouldReturnEmpty_WhenEventTypeNotDtmf() {
        callEventView = new CallEventView(new CallEvent("NewCall", data));
        assertEquals("", callEventView.getUserInput());
    }

    @Test
    public void shouldReturnAListOfAllResponsesPlayed() {
        callEventView = new CallEventView(new CallEvent("NewCall", data));
        data.put(CallEventConstants.RESPONSE_XML, "<response sid=\"123\"><collectdtmf><playaudio>signature_music.wav</playaudio></collectdtmf></response>");

        List<String> content = callEventView.getResponses();

        assertEquals("signature_music", content.get(0));
    }
}
