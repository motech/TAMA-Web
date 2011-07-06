package org.motechproject.tama.functional.test;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.tama.builder.ClinicBuilder;
import org.motechproject.tama.domain.Clinic;
import org.motechproject.tama.functional.page.LoginPage;
import org.motechproject.tama.functional.page.ShowClinicPage;
import org.motechproject.tama.functional.setup.WebDriverFactory;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.PageFactory;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static junit.framework.Assert.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "/testApplicationContext.xml")

public class RegisterClinicTest {
    private WebDriver webDriver;

    @Before
    public void setUp() {
        webDriver = WebDriverFactory.getInstance();
    }

    @After
    public void tearDown() {
        webDriver.quit();
    }

    @Test
    public void testClinicRegistration() {
        Clinic clinic = ClinicBuilder.startRecording().withDefaults().build();
        ShowClinicPage showClinicPage = PageFactory.initElements(webDriver, LoginPage.class)
                .loginWithCorrectUserNamePassword()
                .goToClinicRegistrationPage()
                .registerClinic(clinic);

        assertEquals(clinic.getName(), showClinicPage.getName());
        assertEquals(clinic.getPhone(), showClinicPage.getPhone());
        assertEquals(clinic.getAddress(), showClinicPage.getAddress());
        assertEquals(clinic.getCity().getName(), showClinicPage.getCity());
    }


}
