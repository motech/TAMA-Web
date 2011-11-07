package org.motechproject.tamafunctional.test;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.motechproject.tamafunctional.framework.BaseTest;
import org.motechproject.tamafunctional.framework.MyPageFactory;
import org.motechproject.tamafunctional.page.*;
import org.motechproject.tamafunctional.testdata.TestClinic;
import org.motechproject.tamafunctional.testdata.TestClinician;
import org.motechproject.tamafunctional.testdata.TestLabResult;
import org.motechproject.tamafunctional.testdata.TestPatient;
import org.motechproject.tamafunctional.testdataservice.ClinicianDataService;
import org.motechproject.tamafunctional.testdataservice.PatientDataService;
import org.openqa.selenium.By;

import java.io.IOException;
import java.util.Arrays;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class LabResultRecordTest extends BaseTest {

    private TestClinician clinician;

    private TestLabResult labResult;

    private TestPatient patient;

    @Before
    public void setUp() {
        super.setUp();
        createClinician();
        createPatient();
        labResult = TestLabResult.withMandatory();
    }

    @Test
    public void successfulLabResultCreation() {
        ShowLabResultsPage labResultPage = setUpALabResult_AfterActivatingAPatient();

        assertEquals("12/11/1998", labResultPage.getTestDates().get(0));
        assertEquals("12/11/1998", labResultPage.getTestDates().get(1));
        assertEquals(labResult.results().get(0), labResultPage.getResults().get(0));
        assertEquals(labResult.results().get(1), labResultPage.getResults().get(1));

        labResultPage.logout();
    }

    @Test
    public void labResultsLinkListsAllLabResults_AfterEnteringLabResultsOnceForThePatient() {
        ShowLabResultsPage labResultPage = setUpALabResult_AfterActivatingAPatient();

        labResultPage.gotoHomePage().gotoShowPatientPage(patient).goToLabResultsPage();

        assertNotNull(webDriver.findElement(By.id("labresults_edit_link")));

        labResultPage.logout();
    }

    @Test
    public void editLabResult() {
        ShowLabResultsPage showLabResultsPage = setUpALabResult_AfterActivatingAPatient();

        CreateLabResultsPage createLabResultsPage = showLabResultsPage.gotoEditPage();

        createLabResultsPage.results(Arrays.asList("1","2"));

        createLabResultsPage.submit();

        assertEquals("1", showLabResultsPage.getResults().get(0));
        assertEquals("2", showLabResultsPage.getResults().get(1));

        showLabResultsPage.logout();

    }

    private ShowLabResultsPage setUpALabResult_AfterActivatingAPatient() {
        LoginPage loginPage = MyPageFactory.initElements(webDriver, LoginPage.class);
        return createNewLabResult(loginPage);
    }

    private ShowLabResultsPage createNewLabResult(LoginPage loginPage) {
        return activatePatient(loginPage).goToLabResultsPage().registerNewLabResult(labResult);
    }

    private ShowPatientPage activatePatient(LoginPage loginPage) {
        return login(loginPage).gotoShowPatientPage(patient).activatePatient();
    }

    private ListPatientsPage login(LoginPage loginPage) {
        return loginPage.loginWithClinicianUserNamePassword(clinician.userName(), clinician.password());
    }

    private void createClinician() {
        TestClinic clinic = TestClinic.withMandatory();

        clinician = TestClinician.withMandatory();
        clinician.clinic(clinic);

        ClinicianDataService clinicianDataService = new ClinicianDataService(webDriver);
        clinicianDataService.createWithClinc(clinician);
    }


    private void createPatient() {
        patient = TestPatient.withMandatory();
        PatientDataService patientDataService = new PatientDataService(webDriver);
        patientDataService.register(patient, clinician);
    }
}
