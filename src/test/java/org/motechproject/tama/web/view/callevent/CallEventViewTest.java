package org.motechproject.tama.web.view.callevent;

import org.junit.Before;
import org.junit.Test;
import org.motechproject.ivr.kookoo.eventlogging.CallEventConstants;
import org.motechproject.server.service.ivr.CallEvent;
import org.motechproject.tama.web.view.CallEventView;

import java.util.HashMap;
import java.util.Map;

import static junit.framework.Assert.assertEquals;

public class CallEventViewTest {

    private CallEventView callEventView;

    private Map<String, String> data;

    @Before
    public void setUp() {
        data = new HashMap<String, String>();
    }

    @Test
    public void shouldDisplayAudioPlayedForNewCallEventAction() {
        callEventView = new CallEventView(new CallEvent("NewCall", data));
        data.put(CallEventConstants.RESPONSE_XML, "<response sid=\"123\"><collectdtmf><playaudio>signature_music.wav</playaudio></collectdtmf></response>");

        String content = callEventView.getContent();

        assertEquals("signature_music.wav was played.", content);
    }

    @Test
    public void shouldDisplayKeyPressedAndAudioPlayedForGotDTMFAction() {
        callEventView = new CallEventView(new CallEvent("gotdtmf", data));
        data.put(CallEventConstants.DTMF_DATA, "1");
        data.put(CallEventConstants.RESPONSE_XML, "<response sid=\"123\"><collectdtmf><playaudio>drpujari's_clinic.wav</playaudio></collectdtmf></response>");

        String content = callEventView.getContent();

        assertEquals("1 was pressed and drpujari's_clinic.wav was played.", content);
    }
}
