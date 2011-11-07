package org.motechproject.tamafunctional.frameworkunittest;

import org.junit.Test;
import org.motechproject.tamafunctional.framework.KooKooResponseParser;
import org.motechproject.tamafunctional.testdata.ivrreponse.IVRResponse;

import static junit.framework.Assert.assertNotNull;

public class KooKooResponseParserTest {
    @Test
    public void parse() {
        IVRResponse ivrResponse = KooKooResponseParser.fromXml("<response sid=\"123\"><hangup/></response>");
        assertNotNull(ivrResponse);
        assertNotNull(ivrResponse.isHangedUp());
        assertNotNull(ivrResponse.sid());
    }

    @Test
    public void parseCollectDtmfWithMultipleAudio() {
        IVRResponse ivrResponse = KooKooResponseParser.fromXml("<response sid=\"123\"><collectdtmf><playaudio>foo.wav</playaudio><playaudio>bar.wav</playaudio></collectdtmf></response>");
        assertNotNull(ivrResponse);
    }
}
