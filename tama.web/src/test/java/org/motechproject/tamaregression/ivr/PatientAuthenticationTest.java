package org.motechproject.tamaregression.ivr;

import org.junit.Test;
import org.motechproject.tama.ivr.TamaIVRMessage;
import org.motechproject.tamafunctionalframework.ivr.BaseIVRTest;
import org.motechproject.tamafunctionalframework.ivr.Caller;
import org.motechproject.tamafunctionalframework.ivr.IVRAssert;
import org.motechproject.tamafunctionalframework.testdata.TestClinician;
import org.motechproject.tamafunctionalframework.testdata.TestPatient;
import org.motechproject.tamafunctionalframework.testdata.ivrreponse.IVRResponse;
import org.motechproject.tamafunctionalframework.testdataservice.ClinicianDataService;
import org.motechproject.tamafunctionalframework.testdataservice.PatientDataService;

import java.io.IOException;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;

public class PatientAuthenticationTest extends BaseIVRTest {
    @Test
    public void shouldTestConversationForNonExistentPatient() throws IOException {
        String sid = unique("sid");
        String phoneNumber = unique("phonenumber");
        caller = new Caller(sid, phoneNumber, webClient);
        IVRResponse ivrResponse = caller.call();
        assertEquals(sid, ivrResponse.sid());
        assertNotNull(ivrResponse.isHangedUp());
    }

    @Test
    public void shouldTestConversationForActivatedPatientAndWrongPasscode() throws IOException {
        TestClinician clinician = TestClinician.withMandatory();
        new ClinicianDataService(webDriver).createWithClinic(clinician);

        TestPatient patient = TestPatient.withMandatory();
        patient.patientPreferences().passcode("5678");
        new PatientDataService(webDriver).registerAndActivate(patient, clinician);

        caller = caller(patient);
        IVRResponse ivrResponse = caller.call();
        IVRAssert.asksForCollectDtmfWith(ivrResponse, TamaIVRMessage.SIGNATURE_MUSIC);

        ivrResponse = caller.enter("1234#");
        IVRAssert.asksForCollectDtmfWith(ivrResponse, TamaIVRMessage.SIGNATURE_MUSIC);
    }
}

