package org.motechproject.tamafunctional.test;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.tamafunctional.context.ClinicContext;
import org.motechproject.tamafunctional.context.ClinicianContext;
import org.motechproject.tamafunctional.context.PatientContext;
import org.motechproject.tamafunctional.framework.BaseTest;
import org.motechproject.tamafunctional.framework.MyPageFactory;
import org.motechproject.tamafunctional.page.ListPatientsPage;
import org.motechproject.tamafunctional.page.LoginPage;
import org.motechproject.tamafunctional.page.Page;
import org.motechproject.tamafunctional.page.ShowPatientPage;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.IOException;

import static org.junit.Assert.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath*:**/applicationContext.xml")
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
        PatientContext patientContext1 = new PatientContext("P1", new ClinicianContext(clinicianUsername1, clinicianPassword1, new ClinicContext("Clinic1")));
        PatientContext patientContext2 = new PatientContext("P2", new ClinicianContext(clinicianUsername2, clinicianPassword2, new ClinicContext("Clinic2")));

        buildContexts(patientContext1, patientContext2);

        assertThatClinicianSeesOnlyHisPatients(clinicianUsername1, clinicianPassword1, patientContext1.getPatientId(), patientContext2.getPatientId());
        assertThatClinicianSeesOnlyHisPatients(clinicianUsername2, clinicianPassword2, patientContext2.getPatientId(), patientContext1.getPatientId());
    }


    private void assertThatClinicianSeesOnlyHisPatients(String clinicianUsername, String clinicianPassword, String patientUnderClinician, String patientNotUnderClinician) {
        ListPatientsPage listPatientsPage = MyPageFactory.initElements(webDriver, LoginPage.class)
                .loginWithClinicianUserNamePassword(clinicianUsername, clinicianPassword)
                .goToListPatientsPage();
        ShowPatientPage showPatientPage = listPatientsPage.searchPatientBy(patientUnderClinician);
        assertEquals(showPatientPage.getPatientId(), patientUnderClinician);

        Page page = showPatientPage.goToListPatientsPage().unsuccessfulSearchPatientBy(patientNotUnderClinician, ListPatientsPage.class, ListPatientsPage.LIST_PATIENT_PANE_ID);
        assertEquals(String.format("Patient with id: %s not found", patientNotUnderClinician), page.getPatientSearchErrorMessage());
        page.logout();
    }

    @After
    public void tearDown() throws IOException {
        super.tearDown();
    }

}
