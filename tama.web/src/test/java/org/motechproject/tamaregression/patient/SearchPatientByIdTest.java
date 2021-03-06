package org.motechproject.tamaregression.patient;


import org.junit.Before;
import org.junit.Test;
import org.motechproject.tamafunctionalframework.framework.BaseTest;
import org.motechproject.tamafunctionalframework.framework.MyPageFactory;
import org.motechproject.tamafunctionalframework.page.ListPatientsPage;
import org.motechproject.tamafunctionalframework.page.LoginPage;
import org.motechproject.tamafunctionalframework.page.Page;
import org.motechproject.tamafunctionalframework.page.ShowPatientSummaryPage;
import org.motechproject.tamafunctionalframework.testdata.TestClinician;
import org.motechproject.tamafunctionalframework.testdata.TestPatient;
import org.motechproject.tamafunctionalframework.testdataservice.ClinicianDataService;

import static org.junit.Assert.assertEquals;
import static org.motechproject.tamaregression.patient.PatientAssertionUtils.assertBasicInformation;

public class SearchPatientByIdTest extends BaseTest {

    private TestClinician clinician;

    @Before
    public void setUp() {
        super.setUp();
        clinician = TestClinician.withMandatory();
        new ClinicianDataService(webDriver).createWithClinic(clinician);
    }

    @Test
    public void testSuccessfulPatientSearch() {
        TestPatient patient = TestPatient.withMandatory().patientId("xyz1234");
        ShowPatientSummaryPage showPatientPage = MyPageFactory.initElements(webDriver, LoginPage.class).
                loginWithClinicianUserNamePassword(clinician.userName(), clinician.password()).
                goToPatientRegistrationPage().
                registerNewPatientOnDailyPillReminder(patient).
                goToListPatientsPage().
                searchPatientBy(patient.patientId());

        assertBasicInformation(patient, showPatientPage);
        showPatientPage.logout();
    }

    @Test
    public void testUnsuccessfulPatientSearch() {
        String non_existing_id = "non_existing_id";
        Page listPatientPage = MyPageFactory.initElements(webDriver, LoginPage.class).
                loginWithClinicianUserNamePassword(clinician.userName(), clinician.password()).
                goToListPatientsPage().
                unsuccessfulSearchPatientBy(non_existing_id, ListPatientsPage.class, ListPatientsPage.LIST_PATIENT_PANE_ID);

        assertEquals(listPatientPage.getPatientSearchErrorMessage(), String.format("Patient with id: %s not found", non_existing_id));
        listPatientPage.logout();
    }
}
