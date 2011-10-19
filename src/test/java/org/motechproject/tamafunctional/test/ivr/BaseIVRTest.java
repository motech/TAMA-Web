package org.motechproject.tamafunctional.test.ivr;


import org.junit.After;
import org.junit.Before;
import org.motechproject.tama.util.FileUtil;
import org.motechproject.tamafunctional.framework.BaseTest;
import org.motechproject.tamafunctional.framework.MyWebClient;
import org.motechproject.tamafunctional.ivr.Caller;
import org.motechproject.tamafunctional.ivr.Phone;
import org.motechproject.tamafunctional.testdata.TestPatient;
import org.motechproject.tamafunctional.testdata.ivrreponse.IVRResponse;

import java.io.IOException;

import static junit.framework.Assert.assertTrue;

public abstract class BaseIVRTest extends BaseTest {
    protected MyWebClient webClient;
    protected Caller caller;
    protected Phone phone;

    @Before
    public void setUp() {
        webClient = new MyWebClient();
        phone = new Phone(5555);
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
        assertTrue(ivrResponse.collectDtmf());
        audioFilePresent(ivrResponse, names);
    }

    protected void audioFilePresent(IVRResponse ivrResponse, String... names) {
        for (String name : names) {
            name = FileUtil.sanitizeFilename(name);
            assertTrue(String.format("%s not found. %s", name, ivrResponse.audiosPlayed()), ivrResponse.audioPlayed(name));
        }
    }
}
