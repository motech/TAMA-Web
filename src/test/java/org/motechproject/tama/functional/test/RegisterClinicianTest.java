package org.motechproject.tama.functional.test;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.tama.builder.ClinicBuilder;
import org.motechproject.tama.builder.ClinicianBuilder;
import org.motechproject.tama.domain.Clinic;
import org.motechproject.tama.domain.Clinician;
import org.motechproject.tama.functional.framework.BaseTest;
import org.motechproject.tama.functional.framework.MyPageFactory;
import org.motechproject.tama.functional.page.LoginPage;
import org.motechproject.tama.functional.page.ShowClinicianPage;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.IOException;

import static junit.framework.Assert.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "/testApplicationContext.xml")
public class RegisterClinicianTest extends BaseTest {

    @Before
    public void setUp() {
        super.setUp();
    }

    @Test
    public void testClinicianRegistration() {
        Clinic clinic = ClinicBuilder.startRecording().withDefaults().build();
        Clinician clinician = ClinicianBuilder.startRecording().withDefaults().withClinic(clinic).build();
        ShowClinicianPage showClinicianPage = MyPageFactory.initElements(webDriver, LoginPage.class)
                .loginWithCorrectAdminUserNamePassword()
                .goToClinicRegistrationPage()
                .registerClinic(clinic)
                .goToHomePage()
                .goToClinicianRegistrationPage()
                .registerClinician(clinician);

        assertEquals(clinician.getName(), showClinicianPage.getName());
        assertEquals(clinician.getContactNumber(), showClinicianPage.getContactNumber());
        assertEquals(clinician.getAlternateContactNumber(), showClinicianPage.getAlternateContactNumber());
        assertEquals(clinician.getUsername(), showClinicianPage.getUsername());
        showClinicianPage.logout();
    }

    @After
    public void  tearDown() throws IOException {
       super.tearDown();
    }

}
