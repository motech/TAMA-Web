package org.motechproject.tama.functional.test;

import java.text.SimpleDateFormat;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.tama.builder.PatientBuilder;
import org.motechproject.tama.domain.Patient;
import org.motechproject.tama.functional.page.LoginPage;
import org.motechproject.tama.functional.page.ShowPatientPage;
import org.motechproject.tama.functional.setup.WebDriverFactory;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.PageFactory;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "/testApplicationContext.xml")
public class PatientRegistrationTest {
    private WebDriver webDriver;

    @Before
    public void setUp() {
        webDriver = WebDriverFactory.getInstance();
    }

    @Test
    public void testSuccessfulPatientRegistration() {
        Patient patient = PatientBuilder.startRecording().withDefaults().build();
        ShowPatientPage showPatientPage = PageFactory.initElements(webDriver, LoginPage.class).
                loginWithCorrectUserNamePassword().
                goToPatientRegistrationPage().
                registerNewPatient(patient);
        Assert.assertEquals(showPatientPage.getPatientId(), patient.getPatientId());
        Assert.assertEquals(showPatientPage.getMobileNumber(), patient.getMobilePhoneNumber());
        Assert.assertEquals(showPatientPage.getDateOfBirth(), new SimpleDateFormat("dd/MM/yyyy").format(patient.getDateOfBirth()));
    }

    @After
    public void tearDown() {
        webDriver.quit();
    }
}
