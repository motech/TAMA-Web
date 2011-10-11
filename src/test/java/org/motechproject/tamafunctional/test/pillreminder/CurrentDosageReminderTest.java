package org.motechproject.tamafunctional.test.pillreminder;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.tama.util.FileUtil;
import org.motechproject.tamafunctional.ivr.Caller;
import org.motechproject.tamafunctional.test.ivr.BaseIVRTest;
import org.motechproject.tamafunctional.testdata.PillReminderCallInfo;
import org.motechproject.tamafunctional.testdata.TestClinician;
import org.motechproject.tamafunctional.testdata.TestPatient;
import org.motechproject.tamafunctional.testdata.ivrreponse.IVRResponse;
import org.motechproject.tamafunctional.testdata.treatmentadvice.TestDrugDosage;
import org.motechproject.tamafunctional.testdata.treatmentadvice.TestTreatmentAdvice;
import org.motechproject.tamafunctional.testdataservice.PatientDataService;
import org.motechproject.tamafunctional.testdataservice.ScheduledJobDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.IOException;

import static org.motechproject.tama.ivr.TamaIVRMessage.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath*:**/applicationFunctionalTestContext.xml")
public class CurrentDosageReminderTest extends BaseIVRTest {
    @Autowired
    private ScheduledJobDataService scheduledJobDataService;

    @Autowired
    private FileUtil util;

    @Before
    public void testSetUp() {
        scheduledJobDataService.clearJobs();
    }

    @Test
    public void dosageTakenFlow() throws IOException {
        TestClinician clinician = TestClinician.withMandatory();
        TestPatient patient = TestPatient.withMandatory();
        TestTreatmentAdvice treatmentAdvice = TestTreatmentAdvice.withExtrinsic(TestDrugDosage.forEvening().brandName("Efferven"), TestDrugDosage.forEvening().brandName("Combivir"));

        PatientDataService patientDataService = new PatientDataService(webDriver);
        patientDataService.setupARTRegimenWithDependents(treatmentAdvice, patient, clinician);

        String dosageId = scheduledJobDataService.currentJobId();
        logInfo("{Regimen}{Id={%s}}", dosageId);
        Caller caller = caller(patient);
        caller.replyToCall(new PillReminderCallInfo(dosageId, 1));
        IVRResponse ivrResponse = caller.enter("1234");
        asksForCollectDtmfWith(ivrResponse, util.sanitizeFilename(YOUR_NEXT_DOSE_IS), util.sanitizeFilename(AT), util.sanitizeFilename(MENU_010_05_01_MAINMENU4));
    }
}
