package org.motechproject.tamafunctional.test.ivr;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.motechproject.tamafunctional.context.ClinicianContext;
import org.motechproject.tamafunctional.framework.MyPageFactory;
import org.motechproject.tamafunctional.framework.MyWebClient;
import org.motechproject.tamafunctional.ivr.Caller;
import org.motechproject.tamafunctional.page.LoginPage;
import org.motechproject.tamafunctional.page.ShowPatientPage;
import org.motechproject.tamafunctional.testdata.TestClinician;
import org.motechproject.tamafunctional.testdata.TestPatient;
import org.motechproject.tamafunctional.testdata.ivrreponse.AudioNames;
import org.motechproject.tamafunctional.testdata.ivrreponse.IVRResponse;
import org.motechproject.tamafunctional.testdataservice.ClinicanDataService;
import org.motechproject.tamafunctional.testdataservice.PatientDataService;

import java.io.IOException;

import static junit.framework.Assert.*;

public class PatientAuthenticationTest extends BaseIVRTest {
    private MyWebClient webClient;

    @Before
    public void setUp() {
        webClient = new MyWebClient();
        super.setUp();
    }

    @Test
    public void shouldTestConversationForNonExistentPatient() throws IOException {
        Caller caller = new Caller("123", "1234567890", webClient);
        IVRResponse ivrResponse = caller.call();
        assertEquals("123", ivrResponse.sid());
        assertNotNull(ivrResponse.isHangedUp());
    }

    @Test
    public void shouldTestConversationForActivatedPatientAndWrongPasscode() throws IOException {
        TestClinician clinician = TestClinician.withMandatory();
        new ClinicanDataService(webDriver).createWithClinc(clinician);

        TestPatient patient = TestPatient.withMandatory().mobileNumber("9876543210").passcode("5678");
        new PatientDataService(webDriver).registerAndActivate(patient, clinician);

        Caller caller = new Caller("123", patient.mobileNumber(), webClient);
        IVRResponse ivrResponse = caller.call();
        assertTrue(ivrResponse.collectDtmf());
        assertTrue(ivrResponse.audioPlayed(), ivrResponse.audioPlayed(AudioNames.WELCOME));

        ivrResponse = caller.enter("1234#");
        assertTrue(ivrResponse.collectDtmf());
        assertTrue(ivrResponse.audioPlayed(), ivrResponse.audioPlayed(AudioNames.WELCOME));
    }

    @After
    public void tearDown() throws IOException {
        webClient.shutDown();
        super.tearDown();
    }
}

