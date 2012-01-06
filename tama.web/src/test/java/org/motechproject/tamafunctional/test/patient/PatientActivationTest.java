package org.motechproject.tamafunctional.test.patient;

import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;
import org.motechproject.tamafunctional.framework.BaseTest;
import org.motechproject.tamafunctional.framework.MyPageFactory;
import org.motechproject.tamafunctional.page.LoginPage;
import org.motechproject.tamafunctional.page.ShowPatientPage;
import org.motechproject.tamafunctional.testdata.TestClinic;
import org.motechproject.tamafunctional.testdata.TestClinician;
import org.motechproject.tamafunctional.testdata.TestPatient;
import org.motechproject.tamafunctional.testdataservice.ClinicianDataService;

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
        ShowPatientPage pageAfterActivation = showPatientPage.activatePatient();
        Assert.assertEquals("Active", pageAfterActivation.getStatus().trim());
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
