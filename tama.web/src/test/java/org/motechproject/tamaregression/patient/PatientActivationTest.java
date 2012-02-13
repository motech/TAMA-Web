package org.motechproject.tamaregression.patient;

import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;
import org.motechproject.tamafunctionalframework.framework.BaseTest;
import org.motechproject.tamafunctionalframework.framework.MyPageFactory;
import org.motechproject.tamafunctionalframework.framework.TamaUrl;
import org.motechproject.tamafunctionalframework.page.CreateClinicVisitPage;
import org.motechproject.tamafunctionalframework.page.LoginPage;
import org.motechproject.tamafunctionalframework.page.ShowPatientPage;
import org.motechproject.tamafunctionalframework.testdata.TestClinic;
import org.motechproject.tamafunctionalframework.testdata.TestClinician;
import org.motechproject.tamafunctionalframework.testdata.TestPatient;
import org.motechproject.tamafunctionalframework.testdataservice.ClinicianDataService;

public class PatientActivationTest extends BaseTest {

    private TestClinician clinician;

    @Before
    public void setUp() {
        super.setUp();
        clinician = TestClinician.withMandatory().clinic(TestClinic.withMandatory());
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
        CreateClinicVisitPage pageAfterActivation = showPatientPage.activatePatient();
        Assert.assertEquals("Active", ShowPatientPage.get(webDriver, patient).getStatus().trim());
        pageAfterActivation.logout();
    }

    @Test
    public void testPatientDeactivation() {
        TestPatient patient = TestPatient.withMandatory();
        ShowPatientPage showPatientPage = MyPageFactory.initElements(webDriver, LoginPage.class).
                loginWithClinicianUserNamePassword(clinician.userName(), clinician.password()).
                goToPatientRegistrationPage().
                registerNewPatientOnDailyPillReminder(patient);

        showPatientPage.activatePatient();
        ShowPatientPage pageAfterDeactivation = showPatientPage.deactivatePatient("Study complete");
        Assert.assertEquals("Study complete", pageAfterDeactivation.getStatus().trim());
        pageAfterDeactivation.logout();
    }
}
