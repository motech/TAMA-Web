package org.motechproject.tamafunctional.test.ivr;

import org.junit.After;
import org.junit.Before;
import org.motechproject.tama.common.util.FileUtil;
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

    protected void asksForCollectDtmfWith(IVRResponse ivrResponse, String... names) {
        assertTrue(ivrResponse.collectDtmf());
        assertAudioFilesPresent(ivrResponse, names);
    }

    protected void assertAudioFilesPresent(IVRResponse ivrResponse, String... names) {
        for (String name : names) {
            name = FileUtil.sanitizeFilename(name);
            assertTrue(String.format("%s not found. %s", name, ivrResponse.audiosPlayed()), ivrResponse.wasAudioPlayed(name));
        }
    }

    protected void assertClinicianPhoneNumberPresent(IVRResponse ivrResponse, String number) {
        assertTrue(ivrResponse.isNumberPresent(number));
    }
}
