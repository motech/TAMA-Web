package org.motechproject.tama.functional.test;

import junit.framework.Assert;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.tama.builder.PatientBuilder;
import org.motechproject.tama.builder.TreatmentAdviceViewBuilder;
import org.motechproject.tama.domain.Clinician;
import org.motechproject.tama.domain.Patient;
import org.motechproject.tama.functional.framework.BaseTest;
import org.motechproject.tama.functional.framework.MyPageFactory;
import org.motechproject.tama.functional.page.LoginPage;
import org.motechproject.tama.functional.page.ShowPatientPage;
import org.motechproject.tama.functional.page.ViewARTRegimenPage;
import org.motechproject.tama.functional.preset.ClinicianPreset;
import org.motechproject.tama.web.model.TreatmentAdviceView;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.IOException;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "/testApplicationContext.xml")
public class PatientActivationTest extends BaseTest {

    @Before
    public void setUp(){
        super.setUp();
    }

    @Test
    public void testSuccessfulPatientActivation() {
        Clinician clinician = new ClinicianPreset(webDriver).create();

        Patient patient = PatientBuilder.startRecording().withDefaults().build();
        ShowPatientPage showPatientPage = MyPageFactory.initElements(webDriver, LoginPage.class).
                loginWithClinicianUserNamePassword(clinician.getUsername(), clinician.getPassword()).
                goToPatientRegistrationPage().
                registerNewPatient(patient);
        Assert.assertEquals(showPatientPage.getStatus().trim(), Patient.Status.Inactive.toString());

        ShowPatientPage pageAfterActivation = showPatientPage.activatePatient();
        Assert.assertEquals(pageAfterActivation.getStatus().trim(), Patient.Status.Active.toString());

        pageAfterActivation.logout();
    }

    @After
    public void  tearDown() throws IOException {
       super.tearDown();
    }

}
