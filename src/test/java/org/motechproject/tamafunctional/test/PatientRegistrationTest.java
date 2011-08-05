package org.motechproject.tamafunctional.test;

import junit.framework.Assert;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.tama.TAMAConstants;
import org.motechproject.tama.builder.PatientBuilder;
import org.motechproject.tama.domain.HIVMedicalHistory;
import org.motechproject.tama.domain.Patient;
import org.motechproject.tamafunctional.context.ClinicianContext;
import org.motechproject.tamafunctional.framework.BaseTest;
import org.motechproject.tamafunctional.framework.MyPageFactory;
import org.motechproject.tamafunctional.page.LoginPage;
import org.motechproject.tamafunctional.page.ShowPatientPage;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.IOException;
import java.text.SimpleDateFormat;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath*:**/applicationContext.xml")
public class PatientRegistrationTest extends BaseTest {

    @Before
    public void setUp() {
        super.setUp();
    }

    @Test
    public void testSuccessfulPatientRegistration() {
        ClinicianContext clinicianContext = new ClinicianContext();
        buildContexts(clinicianContext);

        Patient patient = PatientBuilder.startRecording().withDefaults().build();
        HIVMedicalHistory hivMedicalHistory = patient.getMedicalHistory().getHivMedicalHistory();
        ShowPatientPage showPatientPage = MyPageFactory.initElements(webDriver, LoginPage.class).
                loginWithClinicianUserNamePassword(clinicianContext.getUsername(), clinicianContext.getPassword()).
                goToPatientRegistrationPage().
                registerNewPatient(patient);

        Assert.assertEquals(showPatientPage.getPatientId(), patient.getPatientId());
        Assert.assertEquals(showPatientPage.getMobileNumber(), patient.getMobilePhoneNumber());
        Assert.assertEquals(showPatientPage.getDateOfBirth(), new SimpleDateFormat("dd/MM/yyyy").format(patient.getDateOfBirth()));
        Assert.assertEquals(showPatientPage.getHIVTestReason(), hivMedicalHistory.getTestReason().getName());
        Assert.assertEquals(showPatientPage.getModeOfTransmission(), hivMedicalHistory.getModeOfTransmission().getType());
        Assert.assertEquals(showPatientPage.getAllergyText(), "ARV Allergy : arvAllergyDescription");
        Assert.assertEquals(showPatientPage.getRashText(), TAMAConstants.NNRTIRash.DRD.getValue());
        showPatientPage.logout();
    }

    @After
    public void  tearDown() throws IOException {
       super.tearDown();
    }

}
