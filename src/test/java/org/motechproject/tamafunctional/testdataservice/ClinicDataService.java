package org.motechproject.tamafunctional.testdataservice;

import org.motechproject.tamafunctional.framework.MyPageFactory;
import org.motechproject.tamafunctional.page.LoginPage;
import org.motechproject.tamafunctional.testdata.TestClinic;
import org.openqa.selenium.WebDriver;

public class ClinicDataService {
    private WebDriver webDriver;

    public ClinicDataService(WebDriver webDriver) {
        this.webDriver = webDriver;
    }

    public void create(TestClinic testClinic) {
        MyPageFactory.initElements(webDriver, LoginPage.class)
                .loginWithCorrectAdminUserNamePassword()
                .goToClinicRegistrationPage()
                .registerClinic(testClinic)
                .logout();
    }
}
