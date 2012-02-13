package org.motechproject.tamaregression.patient;

import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;
import org.motechproject.tamafunctionalframework.framework.BaseTest;
import org.motechproject.tamafunctionalframework.framework.MyPageFactory;
import org.motechproject.tamafunctionalframework.page.LoginPage;
import org.motechproject.tamafunctionalframework.page.ShowPatientPage;
import org.motechproject.tamafunctionalframework.page.UpdatePatientPage;
import org.motechproject.tamafunctionalframework.testdata.TestClinic;
import org.motechproject.tamafunctionalframework.testdata.TestClinician;
import org.motechproject.tamafunctionalframework.testdata.TestPatient;
import org.motechproject.tamafunctionalframework.testdataservice.ClinicianDataService;

import static org.junit.Assert.assertEquals;
import static org.motechproject.tamafunctionalframework.testdata.TestPatientPreferences.*;

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
        showPatientPage.activatePatient();
        showPatientPage = ShowPatientPage.get(webDriver, patient);
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
