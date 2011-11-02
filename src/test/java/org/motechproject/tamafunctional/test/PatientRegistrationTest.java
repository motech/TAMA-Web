package org.motechproject.tamafunctional.test;

import junit.framework.Assert;
import org.junit.Test;
import org.motechproject.tama.TAMAConstants;
import org.motechproject.tamafunctional.context.ClinicianContext;
import org.motechproject.tamafunctional.framework.BaseTest;
import org.motechproject.tamafunctional.framework.MyPageFactory;
import org.motechproject.tamafunctional.page.LoginPage;
import org.motechproject.tamafunctional.page.ShowPatientPage;
import org.motechproject.tamafunctional.testdata.TestPatient;

import java.text.SimpleDateFormat;

public class PatientRegistrationTest extends BaseTest {
    @Test
    public void testSuccessfulPatientRegistration() {
        ClinicianContext clinicianContext = new ClinicianContext();
        buildContexts(clinicianContext);

        TestPatient patient = TestPatient.withMandatory();

        ShowPatientPage showPatientPage = MyPageFactory.initElements(webDriver, LoginPage.class).
                loginWithClinicianUserNamePassword(clinicianContext.getUsername(), clinicianContext.getPassword()).
                goToPatientRegistrationPage().
                registerNewPatient(patient);

        Assert.assertEquals(patient.patientId(), showPatientPage.getPatientId());
        Assert.assertEquals(patient.mobileNumber(), showPatientPage.getMobileNumber());
        Assert.assertEquals(new SimpleDateFormat("dd/MM/yyyy").format(patient.dateOfBirth().toDate()), showPatientPage.getDateOfBirth());

        Assert.assertEquals(patient.hivMedicalHistory().testReason(), showPatientPage.getHIVTestReason());
        Assert.assertEquals(patient.hivMedicalHistory().modeOfTransmission(), showPatientPage.getModeOfTransmission());
        Assert.assertEquals("ARV Allergy : arvAllergyDescription", showPatientPage.getAllergyText());
        Assert.assertEquals(TAMAConstants.NNRTIRash.DRD.getValue(), showPatientPage.getRashText());

        Assert.assertEquals(patient.patientPreferences().passcode(), showPatientPage.getPasscode());
        Assert.assertEquals(patient.patientPreferences().callPreference(), showPatientPage.getCallPreference());
        showPatientPage.logout();
    }
}
