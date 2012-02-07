package org.motechproject.tamaregression.ivr;

import org.junit.Test;
import org.motechproject.tamafunctionalframework.framework.MyPageFactory;
import org.motechproject.tamafunctionalframework.ivr.BaseIVRTest;
import org.motechproject.tamafunctionalframework.page.LoginPage;
import org.motechproject.tamafunctionalframework.testdata.PillReminderCallInfo;
import org.motechproject.tamafunctionalframework.testdata.TestClinician;
import org.motechproject.tamafunctionalframework.testdata.TestPatient;
import org.motechproject.tamafunctionalframework.testdata.treatmentadvice.TestDrugDosage;
import org.motechproject.tamafunctionalframework.testdata.treatmentadvice.TestTreatmentAdvice;
import org.motechproject.tamafunctionalframework.testdataservice.PatientDataService;
import org.motechproject.util.DateUtil;

import java.util.Date;

public class CallLogsTest extends BaseIVRTest {

    @Test
    public void shouldLogCallLogForPillReminderCall() {
        TestClinician clinician = TestClinician.withMandatory();
        TestPatient patient = TestPatient.withMandatory();
        TestTreatmentAdvice treatmentAdvice = TestTreatmentAdvice.withExtrinsic(TestDrugDosage.create("Efferven", "Combivir"));

        PatientDataService patientDataService = new PatientDataService(webDriver);
        patientDataService.setupRegimenWithDependents(treatmentAdvice, patient, clinician);

        caller = caller(patient);
        caller.replyToCall(new PillReminderCallInfo(1));
        caller.enter("1234");
        caller.hangup();
        LoginPage loginPage = MyPageFactory.initElements(webDriver, LoginPage.class);
        Date today = DateUtil.today().toDate();
        loginPage.loginWithClinicianUserNamePassword(clinician.userName(), clinician.password()).goToFilterCallLogsPage().filterCallLogs(today, today);
    }
}
