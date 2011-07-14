package org.motechproject.tama.functional.test;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.tama.builder.ClinicBuilder;
import org.motechproject.tama.builder.ClinicianBuilder;
import org.motechproject.tama.builder.PatientBuilder;
import org.motechproject.tama.domain.Clinic;
import org.motechproject.tama.domain.Clinician;
import org.motechproject.tama.domain.Patient;
import org.motechproject.tama.functional.framework.BaseTest;
import org.motechproject.tama.functional.framework.MyPageFactory;
import org.motechproject.tama.functional.page.ListPatientsPage;
import org.motechproject.tama.functional.page.LoginPage;
import org.motechproject.tama.functional.page.Page;
import org.motechproject.tama.functional.page.ShowPatientPage;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "/testApplicationContext.xml")
public class ClinicianSecurityTest extends BaseTest {

    @Override
    @Before
    public void setUp() {
        super.setUp();
    }

    @Test
    public void shouldVerifyIfCorrectPatientsAreSeenByLoggedInClinician() {
        Clinician clinician1 = createClinicAndClinicianWith("Clinic1", "Clinician1", "cl1", "cl1");
        Patient patient1 = createPatientWith(clinician1, "P1");

        Clinician clinician2 = createClinicAndClinicianWith("Clinic2", "Clinician2", "cl2", "cl2");
        Patient patient2 = createPatientWith(clinician2, "P2");


        ListPatientsPage listPatientsPage = MyPageFactory.initElements(webDriver, LoginPage.class).
                loginWithClinicianUserNamePassword(clinician1.getUsername(), clinician1.getPassword())
                .goToListPatientsPage();
        ShowPatientPage showPatientPage = listPatientsPage.searchPatientBy(patient1.getPatientId());
        assertEquals(showPatientPage.getPatientId(), patient1.getPatientId());

        Page page = showPatientPage.goToListPatientsPage().unsuccessfulSearchPatientBy(patient2.getPatientId(), ListPatientsPage.class, ListPatientsPage.LIST_PATIENT_PANE_ID);
        assertEquals(String.format("Patient with id: %s not found", patient2.getPatientId()), page.getPatientSearchErrorMessage());
        page.logout();



        listPatientsPage = MyPageFactory.initElements(webDriver, LoginPage.class).
                loginWithClinicianUserNamePassword(clinician2.getUsername(), clinician2.getPassword())
                .goToListPatientsPage();
        showPatientPage = listPatientsPage.searchPatientBy(patient2.getPatientId());
        assertEquals(showPatientPage.getPatientId(), patient2.getPatientId());

        page = showPatientPage.goToListPatientsPage().unsuccessfulSearchPatientBy(patient1.getPatientId(), ListPatientsPage.class, ListPatientsPage.LIST_PATIENT_PANE_ID);
        assertEquals(String.format("Patient with id: %s not found", patient1.getPatientId()), page.getPatientSearchErrorMessage());
        page.logout();
    }

    private Clinician createClinicAndClinicianWith(String clinicName, String clinicianName, String clinicianUsername, String clinicianPassword) {
        Clinic clinic = ClinicBuilder.startRecording().withDefaults().withName(clinicName).build();
        MyPageFactory.initElements(webDriver, LoginPage.class)
                .loginWithCorrectAdminUserNamePassword()
                .goToClinicRegistrationPage()
                .registerClinic(clinic)
                .logout();

        Clinician clinician = ClinicianBuilder.startRecording().withDefaults().withClinic(clinic).
                withName(clinicianName).withUserName(clinicianUsername).withPassword(clinicianPassword).build();

        MyPageFactory.initElements(webDriver, LoginPage.class)
                .loginWithCorrectAdminUserNamePassword()
                .goToClinicianRegistrationPage()
                .registerClinician(clinician)
                .logout();
        return clinician;
    }

    private Patient createPatientWith(Clinician clinician, String patientId) {
        Patient patient = PatientBuilder.startRecording().withDefaults().withPatientId(patientId).build();
        MyPageFactory.initElements(webDriver, LoginPage.class)
                .loginWithClinicianUserNamePassword(clinician.getUsername(), clinician.getPassword())
                .goToPatientRegistrationPage()
                .registerNewPatient(patient)
                .logout();
        return patient;
    }
}
