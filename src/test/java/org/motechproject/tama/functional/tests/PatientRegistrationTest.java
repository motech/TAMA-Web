package org.motechproject.tama.functional.tests;

import junit.framework.Assert;
import org.junit.Test;
import org.motechproject.tama.Patient;
import org.motechproject.tama.builders.PatientBuilder;
import org.motechproject.tama.functional.pages.LoginPage;
import org.motechproject.tama.functional.pages.ShowPatientPage;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.PageFactory;

import java.text.SimpleDateFormat;

public class PatientRegistrationTest {
    private WebDriver webDriver = new FirefoxDriver();


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

}
