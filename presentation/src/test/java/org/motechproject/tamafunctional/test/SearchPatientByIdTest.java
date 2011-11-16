package org.motechproject.tamafunctional.test;


import org.junit.Before;
import org.junit.Test;
import org.motechproject.tamafunctional.context.ClinicianContext;
import org.motechproject.tamafunctional.framework.BaseTest;
import org.motechproject.tamafunctional.framework.MyPageFactory;
import org.motechproject.tamafunctional.page.ListPatientsPage;
import org.motechproject.tamafunctional.page.LoginPage;
import org.motechproject.tamafunctional.page.Page;
import org.motechproject.tamafunctional.page.ShowPatientPage;
import org.motechproject.tamafunctional.testdata.TestPatient;

import java.text.SimpleDateFormat;

import static org.junit.Assert.assertEquals;

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
        TestPatient patient = TestPatient.withMandatory().patientId("xyz1234");
        ShowPatientPage showPatientPage = MyPageFactory.initElements(webDriver, LoginPage.class).
                loginWithClinicianUserNamePassword(clinicianContext.getUsername(), clinicianContext.getPassword()).
                goToPatientRegistrationPage().
                registerNewPatient(patient).
                goToListPatientsPage().
                searchPatientBy(patient.patientId());

        assertEquals(showPatientPage.getPatientId(), patient.patientId());
        assertEquals(showPatientPage.getMobileNumber(), patient.mobileNumber());
        assertEquals(showPatientPage.getDateOfBirth(), new SimpleDateFormat("dd/MM/yyyy").format(patient.dateOfBirth().toDate()));
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
}
