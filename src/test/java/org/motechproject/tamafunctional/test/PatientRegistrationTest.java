package org.motechproject.tamafunctional.test;

import junit.framework.Assert;
import org.junit.After;
import org.junit.Test;
import org.motechproject.tamafunctional.context.ClinicianContext;
import org.motechproject.tamafunctional.framework.BaseTest;
import org.motechproject.tamafunctional.framework.MyPageFactory;
import org.motechproject.tamafunctional.page.LoginPage;
import org.motechproject.tamafunctional.page.ShowPatientPage;
import org.motechproject.tamafunctional.testdata.TestPatient;

import java.io.IOException;
import java.text.SimpleDateFormat;

public class PatientRegistrationTest extends BaseTest {
    @Test
    public void testSuccessfulPatientRegistration() {
        ClinicianContext clinicianContext = new ClinicianContext();
        buildContexts(clinicianContext);

        TestPatient patient = TestPatient.withMandatory(clinicianContext.clinic());

//    #252
//        HIVMedicalHistory hivMedicalHistory = patient.getMedicalHistory().getHivMedicalHistory();
        ShowPatientPage showPatientPage = MyPageFactory.initElements(webDriver, LoginPage.class).
                loginWithClinicianUserNamePassword(clinicianContext.getUsername(), clinicianContext.getPassword()).
                goToPatientRegistrationPage().
                registerNewPatient(patient);

        Assert.assertEquals(showPatientPage.getPatientId(), patient.patientId());
        Assert.assertEquals(showPatientPage.getMobileNumber(), patient.mobileNumber());
        Assert.assertEquals(showPatientPage.getDateOfBirth(), new SimpleDateFormat("dd/MM/yyyy").format(patient.dateOfBirth().toDate()));

//    #252
//        Assert.assertEquals(showPatientPage.getHIVTestReason(), hivMedicalHistory.getTestReason().getName());
//        Assert.assertEquals(showPatientPage.getModeOfTransmission(), hivMedicalHistory.getModeOfTransmission().getType());
//        Assert.assertEquals(showPatientPage.getAllergyText(), "ARV Allergy : arvAllergyDescription");
//        Assert.assertEquals(showPatientPage.getRashText(), TAMAConstants.NNRTIRash.DRD.getValue());
        showPatientPage.logout();
    }

    @After
    public void  tearDown() throws IOException {
       super.tearDown();
    }

}
