package org.motechproject.tamaregression;

import org.junit.Before;
import org.junit.Test;
import org.motechproject.tama.common.TAMAConstants;
import org.motechproject.tamafunctionalframework.framework.BaseTest;
import org.motechproject.tamafunctionalframework.framework.MyPageFactory;
import org.motechproject.tamafunctionalframework.page.LoginPage;
import org.motechproject.tamafunctionalframework.page.ShowClinicVisitListPage;
import org.motechproject.tamafunctionalframework.testdata.TestClinician;
import org.motechproject.tamafunctionalframework.testdata.TestLabResult;
import org.motechproject.tamafunctionalframework.testdata.TestPatient;
import org.motechproject.tamafunctionalframework.testdata.TestVitalStatistics;
import org.motechproject.tamafunctionalframework.testdata.treatmentadvice.TestDrugDosage;
import org.motechproject.tamafunctionalframework.testdata.treatmentadvice.TestTreatmentAdvice;
import org.motechproject.tamafunctionalframework.testdataservice.ClinicianDataService;
import org.motechproject.tamafunctionalframework.testdataservice.PatientDataService;
import org.motechproject.util.DateUtil;

import static junit.framework.Assert.assertEquals;

public class ClinicVisitRegressionTest extends BaseTest {
    private TestClinician clinician;

    @Before
    public void setUp() {
        super.setUp();
        clinician = TestClinician.withMandatory();
        new ClinicianDataService(webDriver).createWithClinic(clinician);
    }

    @Test
    public void testAdjustDueDateAsToday(){
        ShowClinicVisitListPage showClinicVisitListPage = gotoShowClinicVisitsPage();

        showClinicVisitListPage.adjustDueDateAsToday();

        String today = DateUtil.today().toString(TAMAConstants.DATE_FORMAT);
        assertEquals(today, showClinicVisitListPage.getAdjustedDueDate());
    }

    @Test
    public void testMarkAsMissed(){
        ShowClinicVisitListPage showClinicVisitListPage = gotoShowClinicVisitsPage();

        showClinicVisitListPage.markAsMissed();

        assertEquals("Missed", showClinicVisitListPage.getVisitDate());
    }


    private ShowClinicVisitListPage gotoShowClinicVisitsPage() {
        TestPatient patient = TestPatient.withMandatory();
        PatientDataService patientDataService = new PatientDataService(webDriver);
        TestVitalStatistics vitalStatistics = TestVitalStatistics.withMandatory();
        TestLabResult labResult = TestLabResult.withMandatory();
        TestTreatmentAdvice treatmentAdvice = TestTreatmentAdvice.withExtrinsic(TestDrugDosage.create("Efferven", "Combivir"));


        patientDataService.registerAndActivate(treatmentAdvice, labResult, vitalStatistics, patient, clinician);

        return MyPageFactory.initElements(webDriver, LoginPage.class).
                loginWithClinicianUserNamePassword(clinician.userName(), clinician.password()).
                gotoShowPatientPage(patient).goToClinicVisitListPage();
    }
}
