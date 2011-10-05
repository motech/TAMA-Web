package org.motechproject.tamafunctional.test.ivr;

import org.junit.Test;
import org.motechproject.tama.ivr.TamaIVRMessage;
import org.motechproject.tamafunctional.ivr.Caller;
import org.motechproject.tamafunctional.testdata.TestClinician;
import org.motechproject.tamafunctional.testdata.TestPatient;
import org.motechproject.tamafunctional.testdata.ivrreponse.IVRResponse;
import org.motechproject.tamafunctional.testdataservice.ClinicanDataService;
import org.motechproject.tamafunctional.testdataservice.PatientDataService;

import java.io.IOException;

import static junit.framework.Assert.*;

public class PatientAuthenticationTest extends BaseIVRTest {
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

        TestPatient patient = TestPatient.withMandatory();
        patient.patientPreferences().passcode("5678");
        new PatientDataService(webDriver).registerAndActivate(patient, clinician);

        Caller caller = caller(patient);
        IVRResponse ivrResponse = caller.call();
        asksForCollectDtmfWith(ivrResponse, TamaIVRMessage.SIGNATURE_MUSIC);

        ivrResponse = caller.enter("1234#");
        asksForCollectDtmfWith(ivrResponse, TamaIVRMessage.SIGNATURE_MUSIC);
    }
}

