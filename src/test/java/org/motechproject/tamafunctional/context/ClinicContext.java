package org.motechproject.tamafunctional.context;

import org.motechproject.tama.builder.ClinicBuilder;
import org.motechproject.tama.domain.Clinic;
import org.motechproject.tamafunctional.framework.MyPageFactory;
import org.motechproject.tamafunctional.page.LoginPage;
import org.openqa.selenium.WebDriver;

public class ClinicContext extends AbstractContext{


    private Clinic clinic;

    public ClinicContext(){
        clinic = ClinicBuilder.startRecording().withDefaults().build();
    }

    public ClinicContext(String name){
        clinic = ClinicBuilder.startRecording().withDefaults().withName(name).build();
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
        return clinic.getName();
    }

    public Clinic getClinic() {
        return clinic;
    }
}
