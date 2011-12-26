package org.motechproject.tamafunctional.test;

import org.junit.Test;
import org.motechproject.tamafunctional.framework.BaseTest;
import org.motechproject.tamafunctional.framework.MyPageFactory;
import org.motechproject.tamafunctional.page.LoginPage;
import org.motechproject.tamafunctional.page.ShowClinicianPage;
import org.motechproject.tamafunctional.testdata.TestClinic;
import org.motechproject.tamafunctional.testdata.TestClinician;
import org.motechproject.tamafunctional.testdataservice.ClinicDataService;

import static junit.framework.Assert.assertEquals;

public class RegisterClinicianTest extends BaseTest {
    @Test
    public void testClinicianRegistration() {
        TestClinic clinic = TestClinic.withMandatory();
        new ClinicDataService(webDriver).create(clinic);
        TestClinician clinician = TestClinician.withMandatory().clinic(clinic);
        ShowClinicianPage showClinicianPage = MyPageFactory.initElements(webDriver, LoginPage.class)
                .loginWithCorrectAdminUserNamePassword()
                .goToClinicianRegistrationPage()
                .registerClinician(clinician);

        logInfo("{Created}{Clinician}{UserName=%s}", clinician.userName());
        assertEquals(clinician.name(), showClinicianPage.getName());
        assertEquals(clinician.contactNumber(), showClinicianPage.getContactNumber());
        assertEquals(clinician.alternateContactNumber(), showClinicianPage.getAlternateContactNumber());
        assertEquals(clinician.userName(), showClinicianPage.getUsername());
        showClinicianPage.logout();
    }
}
