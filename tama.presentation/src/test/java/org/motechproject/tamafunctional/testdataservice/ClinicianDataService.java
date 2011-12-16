package org.motechproject.tamafunctional.testdataservice;

import org.motechproject.tamafunctional.page.LoginPage;
import org.motechproject.tamafunctional.page.ShowClinicianPage;
import org.motechproject.tamafunctional.testdata.TestClinician;
import org.openqa.selenium.WebDriver;

public class ClinicianDataService extends EntityDataService {
    public ClinicianDataService(WebDriver webDriver) {
        super(webDriver);
    }

    public void createWithClinic(TestClinician clinician) {
        createWithClinicAndDontLogout(clinician).logout();
    }

    public void create(TestClinician clinician) {
        createWithoutLogout(clinician).logout();
    }

    public ShowClinicianPage createWithClinicAndDontLogout(TestClinician clinician) {
        new ClinicDataService(webDriver).create(clinician.clinic());
        return createWithoutLogout(clinician);
    }

    private ShowClinicianPage createWithoutLogout(TestClinician clinician) {
        ShowClinicianPage showClinicianPage = page(LoginPage.class)
                .loginWithCorrectAdminUserNamePassword()
                .goToClinicianRegistrationPage()
                .registerClinician(clinician);
        logInfo("{Created}{Clinician}{UserName=%s}", clinician.userName());
        return showClinicianPage;
    }
}
