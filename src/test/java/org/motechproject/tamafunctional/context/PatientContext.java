package org.motechproject.tamafunctional.context;

import org.motechproject.tama.builder.PatientBuilder;
import org.motechproject.tama.domain.Patient;
import org.motechproject.tamafunctional.framework.MyPageFactory;
import org.motechproject.tamafunctional.page.LoginPage;
import org.motechproject.tamafunctional.testdata.TestPatient;
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
        TestPatient patient = TestPatient.withMandatory(clinicianContext.clinic()).patientId(patientId);
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
