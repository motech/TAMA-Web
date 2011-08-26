package org.motechproject.tamafunctional.testdataservice;

import org.motechproject.tamafunctional.framework.MyPageFactory;
import org.motechproject.tamafunctional.page.LoginPage;
import org.motechproject.tamafunctional.testdata.TestClinic;
import org.openqa.selenium.WebDriver;

public class ClinicDataService extends EntityDataService {
    public ClinicDataService(WebDriver webDriver) {
        super(webDriver);
    }

    public void create(TestClinic clinic) {
        MyPageFactory.initElements(webDriver, LoginPage.class)
                .loginWithCorrectAdminUserNamePassword()
                .goToClinicRegistrationPage()
                .registerClinic(clinic)
                .logout();
        logInfo("{Created}{Clinic}{Name=%s}", clinic.name());
    }
}
