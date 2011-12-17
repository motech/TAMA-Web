package org.motechproject.tamafunctional.test;

import junit.framework.Assert;
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
    @Test
    public void testSuccessfulPatientActivation() {
        TestClinician clinician = TestClinician.withMandatory().clinic(TestClinic.withMandatory());
        new ClinicianDataService(webDriver).createWithClinic(clinician);

        TestPatient patient = TestPatient.withMandatory();
        ShowPatientPage showPatientPage = MyPageFactory.initElements(webDriver, LoginPage.class).
                loginWithClinicianUserNamePassword(clinician.userName(), clinician.password()).
                goToPatientRegistrationPage().
                registerNewPatient(patient);

        Assert.assertEquals("Inactive", showPatientPage.getStatus().trim());

        ShowPatientPage pageAfterActivation = showPatientPage.activatePatient();
        Assert.assertEquals("Active", pageAfterActivation.getStatus().trim());

        pageAfterActivation.logout();
    }

    @Test
    public void testPatientDeactivation() {
        TestClinician clinician = TestClinician.withMandatory().clinic(TestClinic.withMandatory());
        new ClinicianDataService(webDriver).createWithClinic(clinician);

        TestPatient patient = TestPatient.withMandatory();
        ShowPatientPage showPatientPage = MyPageFactory.initElements(webDriver, LoginPage.class).
                loginWithClinicianUserNamePassword(clinician.userName(), clinician.password()).
                goToPatientRegistrationPage().
                registerNewPatient(patient);

        showPatientPage.activatePatient();

        ShowPatientPage pageAfterDeactivation = showPatientPage.deactivatePatient("Study complete");
        Assert.assertEquals("Study complete", pageAfterDeactivation.getStatus().trim());

        pageAfterDeactivation.logout();
    }
}
