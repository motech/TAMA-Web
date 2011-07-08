package org.motechproject.tama.functional.test;


import junit.framework.Assert;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.tama.builder.PatientBuilder;
import org.motechproject.tama.domain.Patient;
import org.motechproject.tama.functional.framework.BaseTest;
import org.motechproject.tama.functional.page.ListPatientsPage;
import org.motechproject.tama.functional.page.LoginPage;
import org.motechproject.tama.functional.page.Page;
import org.motechproject.tama.functional.page.ShowPatientPage;
import org.motechproject.tama.functional.setup.WebDriverFactory;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.PageFactory;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.text.SimpleDateFormat;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "/testApplicationContext.xml")
public class SearchPatientByIdTest extends BaseTest {
    @Test
    public void testSuccessfulPatientSearch() {
        Patient patient = PatientBuilder.startRecording().withDefaults().build();
        ShowPatientPage showPatientPage = PageFactory.initElements(webDriver, LoginPage.class).
                loginWithCorrectUserNamePassword().
                goToPatientRegistrationPage().
                registerNewPatient(patient).
                goToListPatientsPage().
                searchPatientBy(patient.getPatientId());

        Assert.assertEquals(showPatientPage.getPatientId(), patient.getPatientId());
        Assert.assertEquals(showPatientPage.getMobileNumber(), patient.getMobilePhoneNumber());
        Assert.assertEquals(showPatientPage.getDateOfBirth(), new SimpleDateFormat("dd/MM/yyyy").format(patient.getDateOfBirth()));
    }

    @Test
    public void testUnsuccessfulPatientSearch() {
        String non_existing_id = "non_existing_id";
        Page listPatientPage = PageFactory.initElements(webDriver, LoginPage.class).
                loginWithCorrectUserNamePassword().
                goToListPatientsPage().
                unsuccesfulSearchPatientBy(non_existing_id, ListPatientsPage.class);

        Assert.assertEquals(listPatientPage.getPatientSearchErrorMessage(), String.format("Patient with id: %s not found", non_existing_id));
    }
}
