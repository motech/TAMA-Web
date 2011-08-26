package org.motechproject.tamafunctional.test.ivr;


import org.junit.After;
import org.junit.Before;
import org.motechproject.tamafunctional.framework.BaseTest;
import org.motechproject.tamafunctional.framework.MyWebClient;
import org.motechproject.tamafunctional.ivr.Caller;
import org.motechproject.tamafunctional.testdata.TestPatient;
import org.motechproject.tamafunctional.testdata.ivrreponse.IVRResponse;

import java.io.IOException;

import static junit.framework.Assert.assertTrue;

public abstract class BaseIVRTest extends BaseTest {
    protected MyWebClient webClient;

    @Before
    public void setUp() {
        webClient = new MyWebClient();
        super.setUp();
    }

    @After
    public void tearDown() throws IOException {
        webClient.shutDown();
        super.tearDown();
    }

    protected Caller caller(TestPatient patient) {
        return new Caller("123", patient.mobileNumber(), webClient);
    }

    protected void asksForCollectDtmfWith(IVRResponse ivrResponse, String... name) {
        assertTrue(ivrResponse.collectDtmf());
        assertTrue(ivrResponse.audioPlayed(), ivrResponse.audioPlayed(name));
    }
}
