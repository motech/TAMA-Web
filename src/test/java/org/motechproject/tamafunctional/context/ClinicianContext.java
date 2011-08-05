package org.motechproject.tamafunctional.context;


import org.motechproject.tama.builder.ClinicianBuilder;
import org.motechproject.tama.domain.Clinician;
import org.motechproject.tamafunctional.framework.MyPageFactory;
import org.motechproject.tamafunctional.page.LoginPage;
import org.openqa.selenium.WebDriver;

public class ClinicianContext extends AbstractContext{

    private final String userName;
    private final String password;
    private final ClinicContext clinicContext;

    public ClinicianContext(){
        this.clinicContext = new ClinicContext();
        Clinician clinician = ClinicianBuilder.startRecording().withDefaults().withClinic(clinicContext.getClinic()).build();
        userName = clinician.getUsername();
        password = clinician.getPassword();
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
        Clinician clinician = ClinicianBuilder.startRecording().withDefaults().withClinic(clinicContext.getClinic()).
                withName(userName).withUserName(userName).withPassword(password).build();

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
