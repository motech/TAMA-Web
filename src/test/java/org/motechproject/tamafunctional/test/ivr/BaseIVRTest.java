package org.motechproject.tamafunctional.test.ivr;


import org.junit.After;
import org.junit.Before;
import org.motechproject.tama.util.FileUtil;
import org.motechproject.tamafunctional.framework.BaseTest;
import org.motechproject.tamafunctional.framework.MyWebClient;
import org.motechproject.tamafunctional.ivr.Caller;
import org.motechproject.tamafunctional.testdata.TestPatient;
import org.motechproject.tamafunctional.testdata.ivrreponse.IVRResponse;

import java.io.IOException;

import static junit.framework.Assert.assertTrue;

public abstract class BaseIVRTest extends BaseTest {
    protected MyWebClient webClient;
    protected Caller caller;

    @Before
    public void setUp() {
        webClient = new MyWebClient();
        super.setUp();
    }

    @After
    @Override
    public void tearDown() throws IOException {
        if (caller != null) {
            try {
                caller.hangup();
            } finally {
                caller.logCookies();
                caller.tearDown();
            }
        }
        webClient.shutDown();
        super.tearDown();
    }

    protected Caller caller(TestPatient patient) {
        return new Caller("123", patient.mobileNumber(), webClient);
    }

    protected void asksForCollectDtmfWith(IVRResponse ivrResponse, String ... names) {
        FileUtil fileUtil = new FileUtil();
        assertTrue(ivrResponse.collectDtmf());
        for (String name : names) {
            name = fileUtil.sanitizeFilename(name);
            assertTrue(String.format("%s not found. %s", name, ivrResponse.audiosPlayed()), ivrResponse.audioPlayed(name));
        }
    }
}
