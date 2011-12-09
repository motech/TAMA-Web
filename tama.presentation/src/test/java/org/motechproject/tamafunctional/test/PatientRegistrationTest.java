package org.motechproject.tamafunctional.test;

import junit.framework.Assert;
import org.junit.Test;
import org.motechproject.tamacommon.TAMAConstants;
import org.motechproject.tamafunctional.framework.BaseTest;
import org.motechproject.tamafunctional.framework.MyPageFactory;
import org.motechproject.tamafunctional.page.LoginPage;
import org.motechproject.tamafunctional.page.ShowPatientPage;
import org.motechproject.tamafunctional.testdata.TestClinic;
import org.motechproject.tamafunctional.testdata.TestClinician;
import org.motechproject.tamafunctional.testdata.TestPatient;
import org.motechproject.tamafunctional.testdataservice.ClinicianDataService;

import java.text.SimpleDateFormat;

public class PatientRegistrationTest extends BaseTest {
    @Test
    public void testSuccessfulPatientRegistration() {
        TestClinician clinician = TestClinician.withMandatory().clinic(TestClinic.withMandatory());
        new ClinicianDataService(webDriver).createWithClinc(clinician);

        TestPatient patient = TestPatient.withMandatory();

        ShowPatientPage showPatientPage = MyPageFactory.initElements(webDriver, LoginPage.class).
                loginWithClinicianUserNamePassword(clinician.userName(), clinician.password()).
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
