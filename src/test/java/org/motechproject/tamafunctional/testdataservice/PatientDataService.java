package org.motechproject.tamafunctional.testdataservice;

import org.motechproject.tamafunctional.framework.MyPageFactory;
import org.motechproject.tamafunctional.page.LoginPage;
import org.motechproject.tamafunctional.page.ShowPatientPage;
import org.motechproject.tamafunctional.testdata.TestClinician;
import org.motechproject.tamafunctional.testdata.TestPatient;
import org.openqa.selenium.WebDriver;

public class PatientDataService {
    private WebDriver webDriver;

    public PatientDataService(WebDriver webDriver) {
        this.webDriver = webDriver;
    }

    public void registerAndActivate(TestPatient patient, TestClinician clinician) {
        ShowPatientPage showPatientPage = MyPageFactory.initElements(webDriver, LoginPage.class).
                loginWithClinicianUserNamePassword(clinician.userName(), clinician.password()).
                goToPatientRegistrationPage().
                registerNewPatient(patient);
        showPatientPage.activatePatient().logout();
    }
}
