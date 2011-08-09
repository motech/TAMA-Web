package org.motechproject.tamafunctional.context;

import org.motechproject.tamafunctional.framework.MyPageFactory;
import org.motechproject.tamafunctional.page.LoginPage;
import org.motechproject.tamafunctional.testdata.TestClinician;
import org.openqa.selenium.WebDriver;

public class ClinicianContext extends AbstractContext {

    private final String userName;
    private final String password;
    private final ClinicContext clinicContext;

    public ClinicianContext() {
        this.clinicContext = new ClinicContext();
        TestClinician clinician = TestClinician.withMandatory().clinic(clinicContext.getClinic());
        userName = clinician.userName();
        password = clinician.password();
    }

    public ClinicianContext(String userName, String password, ClinicContext clinicContext) {
        super(clinicContext);
        this.clinicContext = clinicContext;
        this.userName = userName;
        this.password = password;
    }

    @Override
    protected void create(WebDriver webDriver) {
        clinicContext.create(webDriver);
        TestClinician clinician = TestClinician.withMandatory().clinic(clinicContext.getClinic()).name(userName).userName(userName).password(password);

        MyPageFactory.initElements(webDriver, LoginPage.class)
                .loginWithCorrectAdminUserNamePassword()
                .goToClinicianRegistrationPage()
                .registerClinician(clinician)
                .logout();
    }

    public String getUsername() {
        return userName;
    }

    public String getPassword() {
        return password;
    }
}
