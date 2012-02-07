package org.motechproject.tamafunctionalframework.testdataservice;

import org.motechproject.tamafunctionalframework.page.LoginPage;
import org.motechproject.tamafunctionalframework.testdata.TestClinic;
import org.openqa.selenium.WebDriver;

public class ClinicDataService extends EntityDataService {
    public ClinicDataService(WebDriver webDriver) {
        super(webDriver);
    }

    public void create(TestClinic clinic) {
        page(LoginPage.class)
                .loginWithCorrectAdminUserNamePassword()
                .goToClinicRegistrationPage()
                .registerClinic(clinic)
                .logout();
        logInfo("{Created}{Clinic}{Name=%s}", clinic.name());
    }
}
