package org.motechproject.tama.web.tools;

import org.junit.Before;
import org.junit.Test;

import java.util.Collections;
import java.util.List;

import static junit.framework.Assert.assertEquals;

public class CollectDtmfTest {

    private CollectDtmf collectDtmf;

    @Before
    public void setUp(){
        collectDtmf = new CollectDtmf();
    }

    @Test
    public void responseShouldBeAudiosPlayed_WhenThereIsAnyAudio() {
        Response ivrResponse = KooKooResponseParser.fromXml("<response sid=\"123\"><collectdtmf><playaudio>foo.wav</playaudio><playaudio>bar.wav</playaudio></collectdtmf></response>");
        List<String> responses = ivrResponse.responsePlayed();
        assertEquals("foo", responses.get(0));
        assertEquals("bar", responses.get(1));
    }

    @Test
    public void responseShouldBeTheFirstText_WhenThereIsNoAudioAndThereIsAnyText() {
        Response ivrResponse = KooKooResponseParser.fromXml("<response sid=\"123\"><collectdtmf><playtext>foo</playtext><playtext>bar</playtext></collectdtmf></response>");
        List<String> responses = ivrResponse.responsePlayed();
        assertEquals("foo", responses.get(0));
        assertEquals("bar", responses.get(1));
    }

    @Test
    public void responseShouldBeEmpty_WhenThereIsNoTextOrAudio() {
        Response ivrResponse = KooKooResponseParser.fromXml("<response sid=\"123\"><collectdtmf></collectdtmf></response>");
        assertEquals(Collections.emptyList(), ivrResponse.responsePlayed());
    }
}
