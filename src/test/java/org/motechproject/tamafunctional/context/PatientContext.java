package org.motechproject.tamafunctional.context;

import org.motechproject.tama.builder.PatientBuilder;
import org.motechproject.tama.domain.Patient;
import org.motechproject.tamafunctional.framework.MyPageFactory;
import org.motechproject.tamafunctional.page.LoginPage;
import org.openqa.selenium.WebDriver;

public class PatientContext extends AbstractContext {

    private String patientId;
    private final ClinicianContext clinicianContext;

    public PatientContext(String patientId, ClinicianContext clinicianContext) {
        super(clinicianContext);
        this.patientId = patientId;
        this.clinicianContext = clinicianContext;
    }

    public PatientContext() {
        this.patientId = new PatientBuilder().withDefaults().build().getPatientId();
        this.clinicianContext = new ClinicianContext();
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
