package org.motechproject.tamafunctionalframework.ivr;

import org.junit.After;
import org.junit.Before;
import org.motechproject.tamafunctionalframework.framework.BaseTest;
import org.motechproject.tamafunctionalframework.framework.MyWebClient;
import org.motechproject.tamafunctionalframework.ivr.Caller;
import org.motechproject.tamafunctionalframework.testdata.TestPatient;
import org.motechproject.tamafunctionalframework.testdata.ivrreponse.IVRResponse;

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
    public void tearDown() throws IOException {
        if (caller != null) {
            try {
                caller.logCookies();
                caller.hangup();
            } finally {
                caller.tearDown();
            }
        }
        webClient.shutDown();
        super.tearDown();
    }

    protected Caller caller(TestPatient patient) {
        return new Caller(unique("sid"), patient.mobileNumber(), webClient);
    }

    protected void assertClinicianPhoneNumberPresent(IVRResponse ivrResponse, String number) {
        assertTrue(ivrResponse.isNumberPresent(number));
    }
}
