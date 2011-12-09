package org.motechproject.tamafunctional.test;

import junit.framework.Assert;
import org.junit.Test;
import org.motechproject.tamadomain.domain.Status;
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
        new ClinicianDataService(webDriver).createWithClinc(clinician);

        TestPatient patient = TestPatient.withMandatory();
        ShowPatientPage showPatientPage = MyPageFactory.initElements(webDriver, LoginPage.class).
                loginWithClinicianUserNamePassword(clinician.userName(), clinician.password()).
                goToPatientRegistrationPage().
                registerNewPatient(patient);

        Assert.assertEquals(showPatientPage.getStatus().trim(), Status.Inactive.toString());

        ShowPatientPage pageAfterActivation = showPatientPage.activatePatient();
        Assert.assertEquals(pageAfterActivation.getStatus().trim(), Status.Active.toString());

        pageAfterActivation.logout();
    }

    @Test
    public void testPatientDeactivation() {
        TestClinician clinician = TestClinician.withMandatory().clinic(TestClinic.withMandatory());
        new ClinicianDataService(webDriver).createWithClinc(clinician);

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
