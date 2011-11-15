package org.motechproject.tamafunctional.test;

import junit.framework.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.motechproject.tama.domain.Status;
import org.motechproject.tamafunctional.context.ClinicianContext;
import org.motechproject.tamafunctional.framework.BaseTest;
import org.motechproject.tamafunctional.framework.MyPageFactory;
import org.motechproject.tamafunctional.page.LoginPage;
import org.motechproject.tamafunctional.page.ShowPatientPage;
import org.motechproject.tamafunctional.testdata.TestPatient;

public class PatientActivationTest extends BaseTest {
    @Test
    public void testSuccessfulPatientActivation() {
        ClinicianContext clinicianContext = new ClinicianContext();
        buildContexts(clinicianContext);

        TestPatient patient = TestPatient.withMandatory();
        ShowPatientPage showPatientPage = MyPageFactory.initElements(webDriver, LoginPage.class).
                loginWithClinicianUserNamePassword(clinicianContext.getUsername(), clinicianContext.getPassword()).
                goToPatientRegistrationPage().
                registerNewPatient(patient);
        Assert.assertEquals(showPatientPage.getStatus().trim(), Status.Inactive.toString());

        ShowPatientPage pageAfterActivation = showPatientPage.activatePatient();
        Assert.assertEquals(pageAfterActivation.getStatus().trim(), Status.Active.toString());

        pageAfterActivation.logout();
    }

    @Test @Ignore
    public void testPatientDeactivation() {
        ClinicianContext clinicianContext = new ClinicianContext();
        buildContexts(clinicianContext);

        TestPatient patient = TestPatient.withMandatory();
        ShowPatientPage showPatientPage = MyPageFactory.initElements(webDriver, LoginPage.class).
                loginWithClinicianUserNamePassword(clinicianContext.getUsername(), clinicianContext.getPassword()).
                goToPatientRegistrationPage().
                registerNewPatient(patient);

        showPatientPage.activatePatient();

        ShowPatientPage pageAfterDeactivation = showPatientPage.deactivatePatient("Study complete");

        Assert.assertEquals("Study complete", pageAfterDeactivation.getStatus().trim());    // TODO:fails randomly; probably because its navigating to same page

        pageAfterDeactivation.logout();
    }
}
