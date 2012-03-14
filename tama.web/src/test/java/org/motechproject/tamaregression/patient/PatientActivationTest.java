package org.motechproject.tamaregression.patient;

import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;
import org.motechproject.tamafunctionalframework.framework.BaseTest;
import org.motechproject.tamafunctionalframework.framework.MyPageFactory;
import org.motechproject.tamafunctionalframework.page.CreateClinicVisitPage;
import org.motechproject.tamafunctionalframework.page.LoginPage;
import org.motechproject.tamafunctionalframework.page.ShowPatientPage;
import org.motechproject.tamafunctionalframework.testdata.TestClinician;
import org.motechproject.tamafunctionalframework.testdata.TestPatient;
import org.motechproject.tamafunctionalframework.testdata.treatmentadvice.TestDrugDosage;
import org.motechproject.tamafunctionalframework.testdata.treatmentadvice.TestTreatmentAdvice;
import org.motechproject.tamafunctionalframework.testdataservice.ClinicianDataService;

public class PatientActivationTest extends BaseTest {

    private TestClinician clinician;

    @Before
    public void setUp() {
        super.setUp();
        clinician = TestClinician.withMandatory();
        new ClinicianDataService(webDriver).createWithClinic(clinician);
    }

    @Test
    public void testSuccessfulPatientActivation() {
        TestPatient patient = TestPatient.withMandatory();
        ShowPatientPage showPatientPage = MyPageFactory.initElements(webDriver, LoginPage.class).
                loginWithClinicianUserNamePassword(clinician.userName(), clinician.password()).
                goToPatientRegistrationPage().
                registerNewPatientOnDailyPillReminder(patient);

        Assert.assertEquals("Inactive", showPatientPage.getStatus().trim());

        CreateClinicVisitPage createClinicVisitPage = showPatientPage.activatePatient();
        TestTreatmentAdvice treatmentAdvice = TestTreatmentAdvice.withExtrinsic(TestDrugDosage.create("Combivir", "Efferven"));
        showPatientPage = createClinicVisitPage.createNewRegimen(treatmentAdvice).gotoShowPatientPage();

        Assert.assertEquals("Active", showPatientPage.getStatus().trim());
        showPatientPage.logout();
    }

    @Test
    public void testPatientDeactivation() {
        TestPatient patient = TestPatient.withMandatory();
        ShowPatientPage showPatientPage = MyPageFactory.initElements(webDriver, LoginPage.class).
                loginWithClinicianUserNamePassword(clinician.userName(), clinician.password()).
                goToPatientRegistrationPage().
                registerNewPatientOnDailyPillReminder(patient);

        CreateClinicVisitPage createClinicVisitPage = showPatientPage.activatePatient();
        TestTreatmentAdvice treatmentAdvice = TestTreatmentAdvice.withExtrinsic(TestDrugDosage.create("Combivir", "Efferven"));
        showPatientPage = createClinicVisitPage.createNewRegimen(treatmentAdvice).gotoShowPatientPage();

        ShowPatientPage pageAfterDeactivation = showPatientPage.deactivatePatient("Study complete");
        Assert.assertEquals("Study complete", pageAfterDeactivation.getStatus().trim());
        pageAfterDeactivation.logout();
    }
}
