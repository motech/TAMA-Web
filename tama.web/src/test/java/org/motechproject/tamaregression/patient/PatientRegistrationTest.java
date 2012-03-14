package org.motechproject.tamaregression.patient;

import org.junit.Before;
import org.junit.Test;
import org.motechproject.tamafunctionalframework.framework.BaseTest;
import org.motechproject.tamafunctionalframework.framework.MyPageFactory;
import org.motechproject.tamafunctionalframework.page.LoginPage;
import org.motechproject.tamafunctionalframework.page.ShowPatientPage;
import org.motechproject.tamafunctionalframework.testdata.TestClinic;
import org.motechproject.tamafunctionalframework.testdata.TestClinician;
import org.motechproject.tamafunctionalframework.testdata.TestPatient;
import org.motechproject.tamafunctionalframework.testdata.TestPatientPreferences;
import org.motechproject.tamafunctionalframework.testdataservice.ClinicianDataService;

import static org.junit.Assert.assertEquals;
import static org.motechproject.tamaregression.patient.PatientAssertionUtils.*;

public class PatientRegistrationTest extends BaseTest {

    private TestClinician clinician;

    @Before
    public void setUp() {
        super.setUp();
        clinician = TestClinician.withMandatory().clinic(TestClinic.withMandatory());
        new ClinicianDataService(webDriver).createWithClinic(clinician);
    }

    @Test
    public void onDailyReminder() {
        TestPatient patient = TestPatient.withMandatory();

        ShowPatientPage showPatientPage = MyPageFactory.initElements(webDriver, LoginPage.class).
                loginWithClinicianUserNamePassword(clinician.userName(), clinician.password()).
                goToPatientRegistrationPage().
                registerNewPatientOnDailyPillReminder(patient);

        assertPatientRegistered(patient, showPatientPage);
        showPatientPage.logout();
    }

    @Test
    public void onFourDayRecall() {
        TestPatientPreferences patientPreferences = TestPatientPreferences.withMandatory();
        patientPreferences.callPreference(TestPatientPreferences.CallPreference.WEEKLY_CALL);
        TestPatient patient = TestPatient.withMandatory().patientPreferences(patientPreferences);

        ShowPatientPage showPatientPage = MyPageFactory.initElements(webDriver, LoginPage.class).
                loginWithClinicianUserNamePassword(clinician.userName(), clinician.password()).
                goToPatientRegistrationPage().
                registerNewPatientOnWeekly(patient);

        assertPatientRegistered(patient, showPatientPage);
        showPatientPage.logout();
    }
}
