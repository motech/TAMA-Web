package org.motechproject.tama.functional.context;


import org.motechproject.tama.builder.ClinicBuilder;
import org.motechproject.tama.builder.ClinicianBuilder;
import org.motechproject.tama.domain.Clinic;
import org.motechproject.tama.domain.Clinician;
import org.motechproject.tama.functional.framework.MyPageFactory;
import org.motechproject.tama.functional.page.LoginPage;
import org.openqa.selenium.WebDriver;

public class ClinicianContext extends AbstractContext{

    private final String clinicName;
    private final String userName;
    private final String password;


    public ClinicianContext(){
        Clinic clinic = ClinicBuilder.startRecording().withDefaults().build();
        Clinician clinician = ClinicianBuilder.startRecording().withDefaults().withClinic(clinic).build();
        clinicName = clinic.getName();
        userName = clinician.getUsername();
        password = clinician.getPassword();
    }

    public ClinicianContext(String clinicName, String userName, String password) {
        this.clinicName = clinicName;
        this.userName = userName;
        this.password = password;
    }

    @Override
    protected void create(WebDriver webDriver) {
        Clinic clinic = ClinicBuilder.startRecording().withDefaults().withName(clinicName).build();

        MyPageFactory.initElements(webDriver, LoginPage.class)
                .loginWithCorrectAdminUserNamePassword()
                .goToClinicRegistrationPage()
                .registerClinic(clinic)
                .logout();

        Clinician clinician = ClinicianBuilder.startRecording().withDefaults().withClinic(clinic).
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
