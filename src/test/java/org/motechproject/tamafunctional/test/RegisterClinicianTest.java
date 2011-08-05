package org.motechproject.tamafunctional.test;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.tama.builder.ClinicianBuilder;
import org.motechproject.tama.domain.Clinician;
import org.motechproject.tamafunctional.context.ClinicContext;
import org.motechproject.tamafunctional.framework.BaseTest;
import org.motechproject.tamafunctional.framework.MyPageFactory;
import org.motechproject.tamafunctional.page.LoginPage;
import org.motechproject.tamafunctional.page.ShowClinicianPage;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.IOException;

import static junit.framework.Assert.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath*:**/applicationContext.xml")
public class RegisterClinicianTest extends BaseTest {

    @Before
    public void setUp() {
        super.setUp();
    }

    @Test
    public void testClinicianRegistration() {
        ClinicContext clinicContext = new ClinicContext();
        buildContexts(clinicContext);
        Clinician clinician = ClinicianBuilder.startRecording().withDefaults().withClinic(clinicContext.getClinic()).build();
        ShowClinicianPage showClinicianPage = MyPageFactory.initElements(webDriver, LoginPage.class)
                .loginWithCorrectAdminUserNamePassword()
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
