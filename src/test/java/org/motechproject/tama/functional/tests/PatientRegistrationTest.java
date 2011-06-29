package org.motechproject.tama.functional.tests;

import java.text.SimpleDateFormat;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.tama.builders.PatientBuilder;
import org.motechproject.tama.domain.Patient;
import org.motechproject.tama.functional.pages.LoginPage;
import org.motechproject.tama.functional.pages.ShowPatientPage;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.PageFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "/testApplicationContext.xml")
public class PatientRegistrationTest {
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

    @Autowired
    protected WebDriver webDriver;

    public WebDriver getWebDriver() {
        return webDriver;
    }

    public void setWebDriver(WebDriver webDriver) {
        this.webDriver = webDriver;
    }

    @After
    public void tearDown(){
        this.webDriver.quit();
    }

}
