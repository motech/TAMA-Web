package org.motechproject.tamaregression;

import org.junit.Test;
import org.motechproject.tamafunctionalframework.framework.BaseTest;
import org.motechproject.tamafunctionalframework.framework.MyPageFactory;
import org.motechproject.tamafunctionalframework.page.*;
import org.motechproject.tamafunctionalframework.testdata.TestClinic;
import org.motechproject.tamafunctionalframework.testdata.TestClinician;
import org.motechproject.tamafunctionalframework.testdata.TestPatient;
import org.motechproject.tamafunctionalframework.testdataservice.ClinicianDataService;
import org.motechproject.tamafunctionalframework.testdataservice.PatientDataService;

import static org.junit.Assert.assertEquals;

public class ClinicianSecurityTest extends BaseTest {
    @Test
    public void shouldVerifyIfCorrectPatientsAreSeenByLoggedInClinician() {
        TestClinician clinician1 = TestClinician.withMandatory().clinic(TestClinic.withMandatory().name(unique("securityTest")));
        new ClinicianDataService(webDriver).createWithClinic(clinician1);
        TestClinician clinician2 = TestClinician.withMandatory().clinic(TestClinic.withMandatory().name(unique("securityTest")));
        new ClinicianDataService(webDriver).createWithClinic(clinician2);

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
        ShowPatientSummaryPage showPatientSummaryPage = listPatientsPage.searchPatientBy(patient.patientId());
        assertEquals(showPatientSummaryPage.getPatientId(), patient.patientId());

        Page page = showPatientSummaryPage.goToListPatientsPage().unsuccessfulSearchPatientBy(patientNotUnderClinician.patientId(), ListPatientsPage.class, ListPatientsPage.LIST_PATIENT_PANE_ID);
        assertEquals(String.format("Patient with id: %s not found", patientNotUnderClinician.patientId()), page.getPatientSearchErrorMessage());
        page.logout();
    }
}