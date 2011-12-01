package org.motechproject.tamafunctional.test;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.tamafunctional.framework.MyPageFactory;
import org.motechproject.tamafunctional.page.LoginPage;
import org.motechproject.tamafunctional.test.ivr.BaseIVRTest;
import org.motechproject.tamafunctional.testdata.PillReminderCallInfo;
import org.motechproject.tamafunctional.testdata.TestClinician;
import org.motechproject.tamafunctional.testdata.TestPatient;
import org.motechproject.tamafunctional.testdata.treatmentadvice.TestDrugDosage;
import org.motechproject.tamafunctional.testdata.treatmentadvice.TestTreatmentAdvice;
import org.motechproject.tamafunctional.testdataservice.PatientDataService;
import org.motechproject.tamafunctional.testdataservice.ScheduledJobDataService;
import org.motechproject.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Date;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath*:**/applicationFunctionalTestContext.xml")
public class CallLogsTest extends BaseIVRTest{

    @Autowired
    private ScheduledJobDataService scheduledJobDataService;

    @Before
    public void testSetUp() {
        scheduledJobDataService.clearJobs();
    }

    @Test
    public void shouldLogCallLogForPillReminderCall(){
        TestClinician clinician = TestClinician.withMandatory();
        TestPatient patient = TestPatient.withMandatory();
        TestTreatmentAdvice treatmentAdvice = TestTreatmentAdvice.withExtrinsic(TestDrugDosage.create("Efferven", "Combivir"));

        PatientDataService patientDataService = new PatientDataService(webDriver);
        patientDataService.setupARTRegimenWithDependents(treatmentAdvice, patient, clinician);

        String currentDosageId = scheduledJobDataService.currentDosageId();
        logInfo("{CurrentDosageId}{Id={%s}}", currentDosageId);
        caller = caller(patient);
        caller.replyToCall(new PillReminderCallInfo(currentDosageId, 1));
        caller.enter("1234");
        caller.hangup();
        LoginPage loginPage = MyPageFactory.initElements(webDriver, LoginPage.class);
        Date today = DateUtil.today().toDate();
        loginPage.loginWithClinicianUserNamePassword(clinician.userName(), clinician.password()).goToFilterCallLogsPage().filterCallLogs(today, today);
    }
}
