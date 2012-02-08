package org.motechproject.tamasmoke;

import org.junit.Before;
import org.junit.Test;
import org.motechproject.tamafunctionalframework.ivr.BaseIVRTest;
import org.motechproject.tamafunctionalframework.ivr.IVRAssert;
import org.motechproject.tamafunctionalframework.testdata.OutboxCallInfo;
import org.motechproject.tamafunctionalframework.testdata.TestClinician;
import org.motechproject.tamafunctionalframework.testdata.TestPatient;
import org.motechproject.tamafunctionalframework.testdata.ivrreponse.IVRResponse;
import org.motechproject.tamafunctionalframework.testdataservice.ClinicianDataService;
import org.motechproject.tamafunctionalframework.testdataservice.PatientDataService;

import java.io.IOException;

import static org.motechproject.tama.ivr.TamaIVRMessage.*;

public class OutboxCallTest extends BaseIVRTest {

    @Before
    public void setUp() {
        super.setUp();
        TestClinician clinician = TestClinician.withMandatory();
        TestPatient patient = TestPatient.withMandatory();

        new ClinicianDataService(webDriver).createWithClinic(clinician);
        PatientDataService patientDataService = new PatientDataService(webDriver);
        patientDataService.register(patient, clinician);
        caller = caller(patient);
    }

    @Test
    public void shouldMakeAnOutboxCall_AndListenNoMessages() throws IOException {
        caller.replyToCall(new OutboxCallInfo());
        IVRResponse ivrResponse = caller.enter("1234");
        IVRAssert.assertAudioFilesPresent(ivrResponse, DEFAULT_OUTBOUND_CLINIC_MESSAGE, FILE_050_03_01_ITS_TIME_FOR_BEST_CALL_TIME);
        ivrResponse = caller.listenMore();
        IVRAssert.assertAudioFilesPresent(ivrResponse, NO_MESSAGES);
    }
}
