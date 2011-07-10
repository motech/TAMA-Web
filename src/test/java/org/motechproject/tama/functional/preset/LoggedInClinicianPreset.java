package org.motechproject.tama.functional.preset;

import org.motechproject.tama.builder.ClinicBuilder;
import org.motechproject.tama.builder.ClinicianBuilder;
import org.motechproject.tama.domain.Clinic;
import org.motechproject.tama.domain.Clinician;
import org.motechproject.tama.functional.page.LoginPage;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.PageFactory;

public class LoggedInClinicianPreset {

    private WebDriver webDriver;

    public LoggedInClinicianPreset(WebDriver webDriver) {
        this.webDriver = webDriver;
    }

    public Clinician create() {
        Clinic clinic = ClinicBuilder.startRecording().withDefaults().build();
        Clinician clinician = ClinicianBuilder.startRecording().withDefaults().withClinic(clinic).build();
        PageFactory.initElements(webDriver, LoginPage.class)
                .loginWithCorrectAdminUserNamePassword()
                .goToClinicRegistrationPage()
                .registerClinic(clinic)
                .goToHomePage()
                .goToClinicianRegistrationPage()
                .registerClinician(clinician);
        return clinician;
    }
}
