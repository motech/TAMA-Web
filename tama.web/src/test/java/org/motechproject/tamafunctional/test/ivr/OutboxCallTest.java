package org.motechproject.tamafunctional.test.ivr;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.tama.outbox.listener.OutboxCallListener;
import org.motechproject.tamafunctional.framework.ScheduledTaskManager;
import org.motechproject.tamafunctional.testdata.OutboxCallInfo;
import org.motechproject.tamafunctional.testdata.PillReminderCallInfo;
import org.motechproject.tamafunctional.testdata.TestClinician;
import org.motechproject.tamafunctional.testdata.TestPatient;
import org.motechproject.tamafunctional.testdata.ivrreponse.IVRResponse;
import org.motechproject.tamafunctional.testdata.treatmentadvice.TestDrugDosage;
import org.motechproject.tamafunctional.testdata.treatmentadvice.TestTreatmentAdvice;
import org.motechproject.tamafunctional.testdataservice.ClinicianDataService;
import org.motechproject.tamafunctional.testdataservice.PatientDataService;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.IOException;

import static junit.framework.Assert.assertEquals;
import static org.motechproject.tama.ivr.TamaIVRMessage.*;

public class OutboxCallTest extends BaseIVRTest {
    private TestPatient patient;
    private TestClinician clinician;

    @Before
    public void setUp() {
        super.setUp();
        clinician = TestClinician.withMandatory();
        patient = TestPatient.withMandatory();

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
