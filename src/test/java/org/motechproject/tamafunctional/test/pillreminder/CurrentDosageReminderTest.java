package org.motechproject.tamafunctional.test.pillreminder;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
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

    @Before
    public void testSetUp() throws Exception {
        scheduledJobDataService.clearJobs();

        TestClinician clinician = TestClinician.withMandatory();
        TestPatient patient = TestPatient.withMandatory();
        TestTreatmentAdvice treatmentAdvice = TestTreatmentAdvice.withExtrinsic(TestDrugDosage.forEvening().brandName("Efferven"), TestDrugDosage.forEvening().brandName("Combivir"));

        PatientDataService patientDataService = new PatientDataService(webDriver);
        patientDataService.setupARTRegimenWithDependents(treatmentAdvice, patient, clinician);
        caller = caller(patient);
    }

    @Test
    public void currentDosageReminder() throws IOException {
        String dosageId = scheduledJobDataService.currentJobId();
        logInfo("{Regimen}{Id={%s}}", dosageId);

        caller.replyToCall(new PillReminderCallInfo(dosageId, 1));
        IVRResponse ivrResponse = caller.enter("1234");
        asksForCollectDtmfWith(ivrResponse, PILL_REMINDER_RESPONSE_MENU, ITS_TIME_FOR_THE_PILL, PILL_FROM_THE_BOTTLE);
//        ivrResponse = caller.enter("1");
//        audioFilePresent(ivrResponse, DOSE_RECORDED);
////        ivrResponse = caller.enter("3");
////        assertEquals(false, ivrResponse.isEmpty());
////        assertEquals(false, ivrResponse.isHangedUp());
    }

    @Test
    public void dosageTakenFlow() throws IOException {
        caller.call();
        IVRResponse ivrResponse = caller.enter("1234");
        asksForCollectDtmfWith(ivrResponse, MENU_010_05_01_MAINMENU4, YOUR_NEXT_DOSE_IS, AT, YOUR_NEXT_DOSE_IS_PADDING);
        ivrResponse = caller.enter("3");
        audioFilePresent(ivrResponse, NO_MESSAGES);
    }
}
