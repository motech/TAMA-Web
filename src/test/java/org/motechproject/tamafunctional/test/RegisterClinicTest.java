package org.motechproject.tamafunctional.test;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.tama.builder.ClinicBuilder;
import org.motechproject.tama.domain.Clinic;
import org.motechproject.tamafunctional.framework.BaseTest;
import org.motechproject.tamafunctional.framework.MyPageFactory;
import org.motechproject.tamafunctional.page.LoginPage;
import org.motechproject.tamafunctional.page.ShowClinicPage;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.IOException;

import static junit.framework.Assert.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath*:**/applicationContext.xml")
public class RegisterClinicTest extends BaseTest {

    @Before
    public void setUp() {
        super.setUp();
    }

    @Test
    public void testClinicRegistration() {
        Clinic clinic = ClinicBuilder.startRecording().withDefaults().build();
        ShowClinicPage showClinicPage = MyPageFactory.initElements(webDriver, LoginPage.class)
                .loginWithCorrectAdminUserNamePassword()
                .goToClinicRegistrationPage()
                .registerClinic(clinic);

        assertEquals(clinic.getName(), showClinicPage.getName());
        assertEquals(clinic.getPhone(), showClinicPage.getPhone());
        assertEquals(clinic.getAddress(), showClinicPage.getAddress());
        showClinicPage.logout();
    }

    @After
    public void  tearDown() throws IOException {
       super.tearDown();
    }

}
