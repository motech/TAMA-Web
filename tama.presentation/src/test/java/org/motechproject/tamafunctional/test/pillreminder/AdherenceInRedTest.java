package org.motechproject.tamafunctional.test.pillreminder;

import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.tamafunctional.framework.MyPageFactory;
import org.motechproject.tamafunctional.page.ListPatientsPage;
import org.motechproject.tamafunctional.page.LoginPage;
import org.motechproject.tamafunctional.page.ShowAlertPage;
import org.motechproject.tamafunctional.test.ivr.BaseIVRTest;
import org.motechproject.tamafunctional.testdata.TestClinician;
import org.motechproject.tamafunctional.testdata.TestPatient;
import org.motechproject.tamafunctional.testdata.treatmentadvice.TestDrugDosage;
import org.motechproject.tamafunctional.testdata.treatmentadvice.TestTreatmentAdvice;
import org.motechproject.tamafunctional.testdataservice.PatientDataService;
import org.motechproject.tamafunctional.testdataservice.ScheduledJobDataService;
import org.motechproject.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.IOException;

import static junit.framework.Assert.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath*:**/applicationFunctionalTestContext.xml")
public class AdherenceInRedTest extends BaseIVRTest {
    @Autowired
    private ScheduledJobDataService scheduledJobDataService;
    private TestClinician clinician;
    private TestPatient patient;

    @Before
    public void setUp() {
        super.setUp();
        clinician = TestClinician.withMandatory();
        patient = TestPatient.withMandatory();
        TestDrugDosage[] drugDosages = TestDrugDosage.create("Efferven", "Combivir");
        LocalDate yesterday = DateUtil.today().minusDays(1);
        drugDosages[0].startDate(yesterday);
        drugDosages[1].startDate(yesterday);
        TestTreatmentAdvice treatmentAdvice = TestTreatmentAdvice.withExtrinsic(drugDosages);

        PatientDataService patientDataService = new PatientDataService(webDriver);
        patientDataService.setupARTRegimenWithDependents(treatmentAdvice, patient, clinician);
    }

    @Test
    public void shouldRaise_RedAlert_WhenAdherenceFalls_Below70Percent() throws IOException {
        triggrerRedAlertJob();
        verifyCreationOfRedAlertForThePatient();
    }

    private void triggrerRedAlertJob() {
        scheduledJobDataService.triggerRedAlertAdherenceJob(patient.id());
    }

    private void verifyCreationOfRedAlertForThePatient() {
        LoginPage loginPage = MyPageFactory.initElements(webDriver, LoginPage.class);
        ListPatientsPage listPatientsPage = loginPage.loginWithClinicianUserNamePassword(clinician.userName(), clinician.password());
        ShowAlertPage showAlertsPage = listPatientsPage.goToUnreadAlertsPage().openShowAlertPage(patient.patientId());
        assertEquals(patient.patientId(), showAlertsPage.patientId());
        assertEquals("AdherenceInRed", showAlertsPage.alertType());
        assertEquals("Adherence percentage is 0.00%", showAlertsPage.description());
        assertEquals("Daily", showAlertsPage.callPreference());
        showAlertsPage.logout();
    }
}
