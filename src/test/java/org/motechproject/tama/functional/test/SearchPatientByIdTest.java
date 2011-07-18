package org.motechproject.tama.functional.test;


import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.tama.builder.PatientBuilder;
import org.motechproject.tama.domain.Patient;
import org.motechproject.tama.functional.context.ClinicianContext;
import org.motechproject.tama.functional.framework.BaseTest;
import org.motechproject.tama.functional.framework.MyPageFactory;
import org.motechproject.tama.functional.page.ListPatientsPage;
import org.motechproject.tama.functional.page.LoginPage;
import org.motechproject.tama.functional.page.Page;
import org.motechproject.tama.functional.page.ShowPatientPage;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.IOException;
import java.text.SimpleDateFormat;

import static org.junit.Assert.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "/testApplicationContext.xml")
public class SearchPatientByIdTest extends BaseTest {

    private ClinicianContext clinicianContext;

    @Before
    public void setUp() {
        super.setUp();
        clinicianContext = new ClinicianContext();
        buildContexts(clinicianContext);
    }

    @Test
    public void testSuccessfulPatientSearch() {
        Patient patient = PatientBuilder.startRecording().withDefaults().withPatientId("xyz1234").build();
        ShowPatientPage showPatientPage = MyPageFactory.initElements(webDriver, LoginPage.class).
                loginWithClinicianUserNamePassword(clinicianContext.getUsername(), clinicianContext.getPassword()).
                goToPatientRegistrationPage().
                registerNewPatient(patient).
                goToListPatientsPage().
                searchPatientBy(patient.getPatientId());

        assertEquals(showPatientPage.getPatientId(), patient.getPatientId());
        assertEquals(showPatientPage.getMobileNumber(), patient.getMobilePhoneNumber());
        assertEquals(showPatientPage.getDateOfBirth(), new SimpleDateFormat("dd/MM/yyyy").format(patient.getDateOfBirth()));
        showPatientPage.logout();
    }

    @Test
    public void testUnsuccessfulPatientSearch() {

        String non_existing_id = "non_existing_id";
        Page listPatientPage = MyPageFactory.initElements(webDriver, LoginPage.class).
                loginWithClinicianUserNamePassword(clinicianContext.getUsername(), clinicianContext.getPassword()).
                goToListPatientsPage().
                unsuccessfulSearchPatientBy(non_existing_id, ListPatientsPage.class, ListPatientsPage.LIST_PATIENT_PANE_ID);

        assertEquals(listPatientPage.getPatientSearchErrorMessage(), String.format("Patient with id: %s not found", non_existing_id));
        listPatientPage.logout();
    }

    @After
    public void  tearDown() throws IOException {
       super.tearDown();
    }

}
