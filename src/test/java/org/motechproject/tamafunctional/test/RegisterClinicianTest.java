package org.motechproject.tamafunctional.test;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.motechproject.tamafunctional.context.ClinicContext;
import org.motechproject.tamafunctional.framework.BaseTest;
import org.motechproject.tamafunctional.framework.MyPageFactory;
import org.motechproject.tamafunctional.page.LoginPage;
import org.motechproject.tamafunctional.page.ShowClinicianPage;
import org.motechproject.tamafunctional.testdata.TestClinician;

import java.io.IOException;

import static junit.framework.Assert.assertEquals;

public class RegisterClinicianTest extends BaseTest {

    @Before
    public void setUp() {
        super.setUp();
    }

    @Test
    public void testClinicianRegistration() {
        ClinicContext clinicContext = new ClinicContext();
        buildContexts(clinicContext);
        TestClinician clinician = TestClinician.withMandatory().clinic(clinicContext.getClinic());
        ShowClinicianPage showClinicianPage = MyPageFactory.initElements(webDriver, LoginPage.class)
                .loginWithCorrectAdminUserNamePassword()
                .goToClinicianRegistrationPage()
                .registerClinician(clinician);

        assertEquals(clinician.name(), showClinicianPage.getName());
        assertEquals(clinician.contactNumber(), showClinicianPage.getContactNumber());
        assertEquals(clinician.alternateContactNumber(), showClinicianPage.getAlternateContactNumber());
        assertEquals(clinician.userName(), showClinicianPage.getUsername());
        showClinicianPage.logout();
    }

    @After
    public void tearDown() throws IOException {
        super.tearDown();
    }

}
