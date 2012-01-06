package org.motechproject.tamafunctional.test.patient;

import org.junit.Before;
import org.junit.Test;
import org.motechproject.tamafunctional.framework.BaseTest;
import org.motechproject.tamafunctional.framework.MyPageFactory;
import org.motechproject.tamafunctional.page.LoginPage;
import org.motechproject.tamafunctional.page.ShowPatientPage;
import org.motechproject.tamafunctional.page.UpdatePatientPage;
import org.motechproject.tamafunctional.testdata.TestClinic;
import org.motechproject.tamafunctional.testdata.TestClinician;
import org.motechproject.tamafunctional.testdata.TestPatient;
import org.motechproject.tamafunctional.testdataservice.ClinicianDataService;

import static org.junit.Assert.assertEquals;
import static org.motechproject.tamafunctional.testdata.TestPatientPreferences.*;

public class PatientUpdateTest extends BaseTest {

    private TestClinician clinician;

    @Before
    public void setUp(){
        super.setUp();
        clinician = TestClinician.withMandatory().clinic(TestClinic.withMandatory());
        new ClinicianDataService(webDriver).createWithClinic(clinician);
    }

    @Test
    public void shouldWarnWhenEnrollingAPatientOnFourDayRecall() throws InterruptedException {
        TestPatient patient = TestPatient.withMandatory().callPreference(CallPreference.DAILY_CALL);

        ShowPatientPage showPatientPage = registerPatient(patient);
        showPatientPage = showPatientPage.activatePatient();
        showPatientPage = changePatientToWeeklyCallAndExpectWarning(showPatientPage);
        showPatientPage.logout();
    }

    private ShowPatientPage registerPatient(TestPatient patient) {
        return MyPageFactory.initElements(webDriver, LoginPage.class).
                    loginWithClinicianUserNamePassword(clinician.userName(), clinician.password()).
                    goToPatientRegistrationPage().
                    registerNewPatientOnDailyPillReminder(patient);
    }

    private ShowPatientPage changePatientToWeeklyCallAndExpectWarning(ShowPatientPage showPatientPage) {
        boolean expectWarningDialog = true;
        UpdatePatientPage updatePatientPage = showPatientPage.clickOnEditTAMAPreferences();
        showPatientPage = updatePatientPage.changePatientToWeeklyCallPlanWithBestCallDayAndTime("Thursday", "06:00", expectWarningDialog);
        return showPatientPage;
    }

}
