package org.motechproject.tama.functional.test;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.tama.functional.context.ClinicianContext;
import org.motechproject.tama.functional.context.PatientContext;
import org.motechproject.tama.functional.framework.BaseTest;
import org.motechproject.tama.functional.framework.MyPageFactory;
import org.motechproject.tama.functional.page.ListPatientsPage;
import org.motechproject.tama.functional.page.LoginPage;
import org.motechproject.tama.functional.page.Page;
import org.motechproject.tama.functional.page.ShowPatientPage;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.IOException;

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
        String clinicianUsername1 = "cl1";
        String clinicianPassword1 = "cl1";
        String clinicianUsername2 = "cl2";
        String clinicianPassword2 = "cl2";
        PatientContext patientContext1 = new PatientContext("P1", new ClinicianContext("Clinic1", clinicianUsername1, clinicianPassword1));
        PatientContext patientContext2 = new PatientContext("P2", new ClinicianContext("Clinic2", clinicianUsername2, clinicianPassword2));

        buildContexts(patientContext1,patientContext2);

        ListPatientsPage listPatientsPage = MyPageFactory.initElements(webDriver, LoginPage.class)
                .loginWithClinicianUserNamePassword(clinicianUsername1, clinicianPassword1)
                .goToListPatientsPage();
        ShowPatientPage showPatientPage = listPatientsPage.searchPatientBy(patientContext1.getPatientId());
        assertEquals(showPatientPage.getPatientId(), patientContext1.getPatientId());

        Page page = showPatientPage.goToListPatientsPage().unsuccessfulSearchPatientBy(patientContext2.getPatientId(), ListPatientsPage.class, ListPatientsPage.LIST_PATIENT_PANE_ID);
        assertEquals(String.format("Patient with id: %s not found", patientContext2.getPatientId()), page.getPatientSearchErrorMessage());
        page.logout();


        listPatientsPage = MyPageFactory.initElements(webDriver, LoginPage.class).
                loginWithClinicianUserNamePassword(clinicianUsername2, clinicianPassword2)
                .goToListPatientsPage();
        showPatientPage = listPatientsPage.searchPatientBy(patientContext2.getPatientId());
        assertEquals(showPatientPage.getPatientId(), patientContext2.getPatientId());

        page = showPatientPage.goToListPatientsPage().unsuccessfulSearchPatientBy(patientContext1.getPatientId(), ListPatientsPage.class, ListPatientsPage.LIST_PATIENT_PANE_ID);
        assertEquals(String.format("Patient with id: %s not found", patientContext1.getPatientId()), page.getPatientSearchErrorMessage());
        page.logout();
    }

    @After
    public void  tearDown() throws IOException {
       super.tearDown();
    }

}
