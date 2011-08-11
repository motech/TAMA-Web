package org.motechproject.tamafunctional.context;

import org.motechproject.tamafunctional.framework.MyPageFactory;
import org.motechproject.tamafunctional.page.LoginPage;
import org.motechproject.tamafunctional.testdata.TestClinic;
import org.openqa.selenium.WebDriver;

public class ClinicContext extends AbstractContext {
    private TestClinic clinic;

    public ClinicContext() {
        clinic = TestClinic.withMandatory();
    }

    public ClinicContext(String name) {
        clinic = TestClinic.withMandatory().andName(name);
    }

    @Override
    protected void create(WebDriver webDriver) {
        MyPageFactory.initElements(webDriver, LoginPage.class)
                .loginWithCorrectAdminUserNamePassword()
                .goToClinicRegistrationPage()
                .registerClinic(clinic)
                .logout();
    }

    public String getName() {
        return clinic.name();
    }

    public TestClinic getClinic() {
        return clinic;
    }
}
