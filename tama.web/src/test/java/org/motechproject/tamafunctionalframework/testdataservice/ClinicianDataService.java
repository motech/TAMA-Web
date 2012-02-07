package org.motechproject.tamafunctionalframework.testdataservice;

import org.motechproject.tamafunctionalframework.page.LoginPage;
import org.motechproject.tamafunctionalframework.page.ShowClinicianPage;
import org.motechproject.tamafunctionalframework.testdata.TestClinician;
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
