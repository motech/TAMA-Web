package org.motechproject.tamafunctional.test;

import org.junit.Test;
import org.motechproject.tamafunctional.framework.BaseTest;
import org.motechproject.tamafunctional.framework.MyPageFactory;
import org.motechproject.tamafunctional.page.ListPatientsPage;
import org.motechproject.tamafunctional.page.LoginPage;
import org.motechproject.tamafunctional.page.Page;
import org.motechproject.tamafunctional.page.ShowPatientPage;
import org.motechproject.tamafunctional.testdata.TestClinic;
import org.motechproject.tamafunctional.testdata.TestClinician;
import org.motechproject.tamafunctional.testdata.TestPatient;
import org.motechproject.tamafunctional.testdataservice.ClinicanDataService;
import org.motechproject.tamafunctional.testdataservice.PatientDataService;

import static org.junit.Assert.assertEquals;

public class ClinicianSecurityTest extends BaseTest {
    @Test
    public void shouldVerifyIfCorrectPatientsAreSeenByLoggedInClinician() {
        TestClinician clinician1 = TestClinician.withMandatory().clinic(TestClinic.withMandatory().name(unique("securityTest")));
        new ClinicanDataService(webDriver).createWithClinc(clinician1);
        TestClinician clinician2 = TestClinician.withMandatory().clinic(TestClinic.withMandatory().name(unique("securityTest")));
        new ClinicanDataService(webDriver).createWithClinc(clinician2);

        TestPatient patientUnderClinican1 = TestPatient.withMandatory();
        new PatientDataService(webDriver).register(patientUnderClinican1, clinician1);
        TestPatient patientUnderClinican2 = TestPatient.withMandatory();
        new PatientDataService(webDriver).register(patientUnderClinican2, clinician2);

        assertThatClinicianSeesOnlyHisPatients(clinician1, patientUnderClinican1, patientUnderClinican2);
        assertThatClinicianSeesOnlyHisPatients(clinician2, patientUnderClinican2, patientUnderClinican1);
    }


    private void assertThatClinicianSeesOnlyHisPatients(TestClinician clinician, TestPatient patient, TestPatient patientNotUnderClinician) {
        ListPatientsPage listPatientsPage = MyPageFactory.initElements(webDriver, LoginPage.class)
                .loginWithClinicianUserNamePassword(clinician.userName(), clinician.password())
                .goToListPatientsPage();
        ShowPatientPage showPatientPage = listPatientsPage.searchPatientBy(patient.patientId());
        assertEquals(showPatientPage.getPatientId(), patient.patientId());

        Page page = showPatientPage.goToListPatientsPage().unsuccessfulSearchPatientBy(patientNotUnderClinician.patientId(), ListPatientsPage.class, ListPatientsPage.LIST_PATIENT_PANE_ID);
        assertEquals(String.format("Patient with id: %s not found", patientNotUnderClinician.patientId()), page.getPatientSearchErrorMessage());
        page.logout();
    }
}