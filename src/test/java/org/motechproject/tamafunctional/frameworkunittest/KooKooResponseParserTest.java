package org.motechproject.tamafunctional.frameworkunittest;

import org.junit.Test;
import org.motechproject.tamafunctional.framework.KooKooResponseParser;
import org.motechproject.tamafunctional.testdata.ivrreponse.IVRResponse;

import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;

public class KooKooResponseParserTest {
    @Test
    public void parse() {
        IVRResponse ivrResponse = KooKooResponseParser.fromXml("<response sid=\"123\"><hangup/></response>");
        assertNotNull(ivrResponse);
        assertNotNull(ivrResponse.isHangedUp());
        assertNotNull(ivrResponse.sid());
    }
}
