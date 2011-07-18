package org.motechproject.tama.functional.context;

import org.motechproject.tama.builder.PatientBuilder;
import org.motechproject.tama.domain.Patient;
import org.motechproject.tama.functional.framework.MyPageFactory;
import org.motechproject.tama.functional.page.LoginPage;
import org.openqa.selenium.WebDriver;

public class PatientContext extends AbstractContext {

    private String patientId;
    private final ClinicianContext clinicianContext;

    public PatientContext(String patientId, ClinicianContext clinicianContext) {
        super(clinicianContext);
        this.patientId = patientId;
        this.clinicianContext = clinicianContext;
    }

    @Override
    protected void create(WebDriver webDriver) {
        Patient patient = PatientBuilder.startRecording().withDefaults().withPatientId(patientId).build();
        MyPageFactory.initElements(webDriver, LoginPage.class)
                .loginWithClinicianUserNamePassword(clinicianContext.getUsername(), clinicianContext.getPassword())
                .goToPatientRegistrationPage()
                .registerNewPatient(patient)
                .logout();
    }

    public String getPatientId() {
        return patientId;
    }
}
