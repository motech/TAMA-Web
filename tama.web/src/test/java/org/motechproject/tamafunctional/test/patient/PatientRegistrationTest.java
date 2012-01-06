package org.motechproject.tamafunctional.test.patient;

import org.junit.Before;
import org.junit.Test;
import org.motechproject.tama.common.TAMAConstants;
import org.motechproject.tamafunctional.framework.BaseTest;
import org.motechproject.tamafunctional.framework.MyPageFactory;
import org.motechproject.tamafunctional.page.LoginPage;
import org.motechproject.tamafunctional.page.ShowPatientPage;
import org.motechproject.tamafunctional.testdata.TestClinic;
import org.motechproject.tamafunctional.testdata.TestClinician;
import org.motechproject.tamafunctional.testdata.TestPatient;
import org.motechproject.tamafunctional.testdata.TestPatientPreferences;
import org.motechproject.tamafunctional.testdataservice.ClinicianDataService;

import java.text.SimpleDateFormat;

import static org.junit.Assert.assertEquals;
import static org.motechproject.tamafunctional.test.patient.PatientAssertionUtils.*;

public class PatientRegistrationTest extends BaseTest {

    private TestClinician clinician;

    @Before
    public void setUp(){
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
