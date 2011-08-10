package org.motechproject.tamafunctional.test;

import junit.framework.Assert;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.tama.builder.PatientBuilder;
import org.motechproject.tama.builder.TreatmentAdviceViewBuilder;
import org.motechproject.tama.domain.Patient;
import org.motechproject.tamafunctional.context.ClinicianContext;
import org.motechproject.tamafunctional.framework.BaseTest;
import org.motechproject.tamafunctional.framework.MyPageFactory;
import org.motechproject.tamafunctional.page.LoginPage;
import org.motechproject.tamafunctional.page.ViewARTRegimenPage;
import org.motechproject.tama.web.model.TreatmentAdviceView;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.IOException;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath*:**/applicationContext.xml")
public class PatientARTRegimenTest  extends BaseTest {

    @Before
    public void setUp(){
        super.setUp();
    }

    @Test
    public void testPatientARTRegimen() {
        ClinicianContext clinicianContext = new ClinicianContext();
        buildContexts(clinicianContext);
        Patient patient = PatientBuilder.startRecording().withDefaults().build();
        TreatmentAdviceView treatmentAdvice = TreatmentAdviceViewBuilder.startRecording().withDefaults().withPatientId(patient.getPatientId()).build();

        ViewARTRegimenPage viewARTRegimenPage = MyPageFactory.initElements(webDriver, LoginPage.class).
                loginWithClinicianUserNamePassword(clinicianContext.getUsername(), clinicianContext.getPassword()).
                goToPatientRegistrationPage().
                registerNewPatient(patient)
                .activatePatient()
                .goToCreateARTRegimenPage()
                .registerNewARTRegimen(treatmentAdvice)
                .goToViewARTRegimenPage();
        Assert.assertEquals(viewARTRegimenPage.getPatientId(), treatmentAdvice.getPatientId());
        Assert.assertEquals(viewARTRegimenPage.getRegimenName(), treatmentAdvice.getRegimenName());
        Assert.assertEquals(viewARTRegimenPage.getDrugCompositionGroupName(), treatmentAdvice.getDrugCompositionName());

        viewARTRegimenPage.logout();
    }

    @After
    public void  tearDown() throws IOException {
       super.tearDown();
    }


}
