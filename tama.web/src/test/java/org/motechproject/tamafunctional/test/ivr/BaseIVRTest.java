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
import static org.motechproject.tamafunctional.test.ivr.IVRAssert.*;

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
