package org.motechproject.tama.web.tools;

import org.junit.Test;

import java.util.List;

import static junit.framework.Assert.assertEquals;

public class ResponseTest {

    @Test
    public void shouldPlayAudiosInCollectDtmf() {
        Response ivrResponse = KooKooResponseParser.fromXml("<response sid=\"123\"><collectdtmf><playaudio>foo.wav</playaudio><playaudio>bar.wav</playaudio></collectdtmf></response>");
        List<String> responses = ivrResponse.responsePlayed();
        assertEquals("foo", responses.get(0));
        assertEquals("bar", responses.get(1));
    }

    @Test
    public void shouldPlayAudiosInResponse_WhenDtmfIsNotCollected() {
        Response ivrResponse = KooKooResponseParser.fromXml("<response sid=\"123\"><playaudio>foo.wav</playaudio><playaudio>bar.wav</playaudio><hangup/></response>");
        List<String> responses = ivrResponse.responsePlayed();
        assertEquals("foo", responses.get(0));
        assertEquals("bar", responses.get(1));
    }
}
