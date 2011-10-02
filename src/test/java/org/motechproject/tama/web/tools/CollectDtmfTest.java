package org.motechproject.tama.web.tools;

import org.junit.Before;
import org.junit.Test;

public class CollectDtmfTest {

    private CollectDtmf collectDtmf;

    @Before
    public void setUp(){
        collectDtmf = new CollectDtmf();
    }

    @Test
    public void responseShouldBeTheFirstAudioFile_WhenThereIsAnyAudio() {
        Response ivrResponse = KooKooResponseParser.fromXml("<response sid=\"123\"><collectdtmf><playaudio>foo.wav</playaudio><playaudio>bar.wav</playaudio></collectdtmf></response>");
        ivrResponse.responsePlayed().equals("foo.wav");
    }

    @Test
    public void responseShouldBeTheFirstText_WhenThereIsNoAudioAndThereIsAnyText() {
        Response ivrResponse = KooKooResponseParser.fromXml("<response sid=\"123\"><collectdtmf><playtext>foo</playtext><playtext>bar</playtext></collectdtmf></response>");
        ivrResponse.responsePlayed().equals("foo.wav");
    }

    @Test
    public void responseShouldBeEmpty_WhenThereIsNoTextOrAudio() {
        Response ivrResponse = KooKooResponseParser.fromXml("<response sid=\"123\"><collectdtmf><playtext>foo</playtext><playtext>bar</playtext></collectdtmf></response>");
        ivrResponse.responsePlayed().equals("foo.wav");
    }
}
