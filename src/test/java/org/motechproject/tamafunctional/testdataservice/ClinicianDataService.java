package org.motechproject.tamafunctional.testdataservice;

import org.motechproject.tamafunctional.framework.MyPageFactory;
import org.motechproject.tamafunctional.page.LoginPage;
import org.motechproject.tamafunctional.testdata.TestClinician;
import org.openqa.selenium.WebDriver;

public class ClinicianDataService extends EntityDataService {
    public ClinicianDataService(WebDriver webDriver) {
        super(webDriver);
    }

    public void create(TestClinician clinician) {
        MyPageFactory.initElements(webDriver, LoginPage.class)
                .loginWithCorrectAdminUserNamePassword()
                .goToClinicianRegistrationPage()
                .registerClinician(clinician)
                .logout();
        logInfo("{Created}{Clinician}{UserName=%s}", clinician.userName());
    }

    public void createWithClinc(TestClinician clinician) {
        new ClinicDataService(webDriver).create(clinician.clinic());
        create(clinician);
    }
}
