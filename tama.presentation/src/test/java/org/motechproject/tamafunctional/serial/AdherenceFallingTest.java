package org.motechproject.tamafunctional.serial;

import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.tamacallflow.listener.AdherenceTrendListener;
import org.motechproject.tamadatasetup.service.TAMADateTimeService;
import org.motechproject.tamafunctional.framework.MyPageFactory;
import org.motechproject.tamafunctional.framework.ScheduledTaskManager;
import org.motechproject.tamafunctional.page.ListPatientsPage;
import org.motechproject.tamafunctional.page.LoginPage;
import org.motechproject.tamafunctional.page.ShowAlertPage;
import org.motechproject.tamafunctional.test.ivr.BaseIVRTest;
import org.motechproject.tamafunctional.testdata.PillReminderCallInfo;
import org.motechproject.tamafunctional.testdata.TestClinician;
import org.motechproject.tamafunctional.testdata.TestPatient;
import org.motechproject.tamafunctional.testdata.ivrreponse.IVRResponse;
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
import static org.motechproject.tamacallflow.ivr.TamaIVRMessage.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath*:**/applicationFunctionalTestContext.xml")
public class AdherenceFallingTest extends BaseIVRTest {
    @Autowired
    private ScheduledJobDataService scheduledJobDataService;
    private TestPatient patient;
    private TestClinician clinician;
    private ScheduledTaskManager scheduledTaskManager;
    private TAMADateTimeService tamaDateTimeService;

    @Before
    public void setUp() {
        super.setUp();
        scheduledTaskManager = new ScheduledTaskManager(webClient);
        tamaDateTimeService = new TAMADateTimeService(webClient);
        tamaDateTimeService.adjustDateTime(DateUtil.now());
        setupData();
    }

    @Test
    public void shouldRaise_AdherenceFallingAlert_WhenAdherenceFalls() throws IOException {
        tamaDateTimeService.adjustDateTime(DateUtil.now().minusWeeks(1));
        patientRecordsAdherenceAWeekBack();
        tamaDateTimeService.adjustDateTime(DateUtil.now());
        triggerAdherenceFallingJob();
        verifyCreationOfAdherenceFallingAlertForThePatient();
    }

    private void setupData() {
        clinician = TestClinician.withMandatory();
        patient = TestPatient.withMandatory();
        TestDrugDosage[] drugDosages = TestDrugDosage.create("Efferven", "Combivir");
        LocalDate twoWeeksBack = DateUtil.today().minusWeeks(2);
        drugDosages[0].startDate(twoWeeksBack);
        drugDosages[1].startDate(twoWeeksBack);
        TestTreatmentAdvice treatmentAdvice = TestTreatmentAdvice.withExtrinsic(drugDosages);

        PatientDataService patientDataService = new PatientDataService(webDriver);
        patientDataService.setupARTRegimenWithDependents(treatmentAdvice, patient, clinician);
        caller = caller(patient);
    }

    private void patientRecordsAdherenceAWeekBack() {
        String currentDosageId = scheduledJobDataService.currentDosageId(patient.id());
        caller.replyToCall(new PillReminderCallInfo(currentDosageId, 1));

        IVRResponse ivrResponse = caller.enter("1234");
        patientConfirmsTakingLastWeeksDose(ivrResponse);
        caller.hangup();
    }

    private void patientConfirmsTakingLastWeeksDose(IVRResponse ivrResponse) {
        asksForCollectDtmfWith(ivrResponse, PILL_REMINDER_RESPONSE_MENU, ITS_TIME_FOR_THE_PILL, PILL_FROM_THE_BOTTLE);
        ivrResponse = caller.enter("1");
        assertAudioFilesPresent(ivrResponse, DOSE_RECORDED);
    }

    private void triggerAdherenceFallingJob() {
        scheduledTaskManager.trigger(AdherenceTrendListener.class, "handleWeeklyAdherence", patient.id());
    }

    private void verifyCreationOfAdherenceFallingAlertForThePatient() {
        LoginPage loginPage = MyPageFactory.initElements(webDriver, LoginPage.class);
        ListPatientsPage listPatientsPage = loginPage.loginWithClinicianUserNamePassword(clinician.userName(), clinician.password());
        ShowAlertPage showAlertsPage = listPatientsPage.goToUnreadAlertsPage().openShowAlertPage(patient.patientId());
        assertEquals(patient.patientId(), showAlertsPage.patientId());
        assertEquals("FallingAdherence", showAlertsPage.alertType());
        assertEquals("Adherence fell by 50.00%, from 14.29% to 7.14%", showAlertsPage.description());
        assertEquals("Daily", showAlertsPage.callPreference());
        showAlertsPage.logout();
    }
}
