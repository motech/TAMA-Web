package org.motechproject.tamaregression.serial;

import org.joda.time.LocalDate;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.motechproject.tama.dailypillreminder.listener.AdherenceTrendListener;
import org.motechproject.tamadatasetup.service.TAMADateTimeService;
import org.motechproject.tamafunctionalframework.framework.MyPageFactory;
import org.motechproject.tamafunctionalframework.framework.ScheduledTaskManager;
import org.motechproject.tamafunctionalframework.ivr.BaseIVRTest;
import org.motechproject.tamafunctionalframework.ivr.IVRAssert;
import org.motechproject.tamafunctionalframework.page.ListPatientsPage;
import org.motechproject.tamafunctionalframework.page.LoginPage;
import org.motechproject.tamafunctionalframework.page.ShowAlertPage;
import org.motechproject.tamafunctionalframework.page.UpdateAlertPage;
import org.motechproject.tamafunctionalframework.testdata.OutboxCallInfo;
import org.motechproject.tamafunctionalframework.testdata.PillReminderCallInfo;
import org.motechproject.tamafunctionalframework.testdata.TestClinician;
import org.motechproject.tamafunctionalframework.testdata.TestPatient;
import org.motechproject.tamafunctionalframework.testdata.ivrreponse.IVRResponse;
import org.motechproject.tamafunctionalframework.testdata.treatmentadvice.TestDrugDosage;
import org.motechproject.tamafunctionalframework.testdata.treatmentadvice.TestTreatmentAdvice;
import org.motechproject.tamafunctionalframework.testdataservice.PatientDataService;
import org.motechproject.util.DateUtil;

import java.io.IOException;

import static junit.framework.Assert.assertEquals;
import static org.motechproject.tama.ivr.TamaIVRMessage.*;

public class AdherenceFallingTest extends BaseIVRTest {
    private TestPatient patient;
    private TestClinician clinician;
    private ScheduledTaskManager scheduledTaskManager;
    private TAMADateTimeService tamaDateTimeService;

    @Before
    public void setUp() {
        super.setUp();
        scheduledTaskManager = new ScheduledTaskManager(webClient);
        tamaDateTimeService = new TAMADateTimeService(webClient);
        tamaDateTimeService.adjustDateTime(DateUtil.now().minusWeeks(2));
        setupData();
    }

    @After
    public void tearDown() throws IOException {
        tamaDateTimeService.adjustDateTime(DateUtil.now());
        super.tearDown();
    }

    @Test
    public void shouldRaise_AdherenceFallingAlert_WhenAdherenceFalls() throws IOException {
        tamaDateTimeService.adjustDateTime(DateUtil.now().minusWeeks(1));
        patientRecordsAdherenceAWeekBack();
        tamaDateTimeService.adjustDateTime(DateUtil.now());
        patientRecordsAdherenceNow();
        triggerAdherenceFallingJob();
        verifyCreationOfAdherenceFallingAlertForThePatient();
        verifyOutboxMessageCreated();
    }

    private void setupData() {
        clinician = TestClinician.withMandatory();
        patient = TestPatient.withMandatory();
        TestDrugDosage[] drugDosages = TestDrugDosage.create("Combivir", "Efferven");
        LocalDate twoWeeksBack = DateUtil.today().minusWeeks(2);
        drugDosages[0].startDate(twoWeeksBack);
        drugDosages[1].startDate(twoWeeksBack);
        TestTreatmentAdvice treatmentAdvice = TestTreatmentAdvice.withExtrinsic(drugDosages);

        PatientDataService patientDataService = new PatientDataService(webDriver);
        patientDataService.setupRegimenWithDependents(treatmentAdvice, patient, clinician);
        caller = caller(patient);
    }

    private void patientRecordsAdherenceNow() {
        caller.replyToCall(new PillReminderCallInfo(1));

        IVRResponse ivrResponse = caller.enter("1234");
        patientConfirmsNotHavingTakenDose(ivrResponse);
        caller.hangup();
    }

    private void patientConfirmsNotHavingTakenDose(IVRResponse ivrResponse) {
        IVRAssert.asksForCollectDtmfWith(ivrResponse, PILL_REMINDER_RESPONSE_MENU, ITS_TIME_FOR_THE_PILL_OUTGOING_CALL_FOR_CURRENT_DOSAGE, FROM_THE_BOTTLE_OUTGOING_CALL_FOR_CURRENT_DOSAGE);
        ivrResponse = caller.enter("3");
        ivrResponse = caller.enter("2");
        IVRAssert.assertAudioFilesPresent(ivrResponse, PLEASE_CARRY_SMALL_BOX);
    }

    private void patientRecordsAdherenceAWeekBack() {
        caller.replyToCall(new PillReminderCallInfo(1));

        IVRResponse ivrResponse = caller.enter("1234");
        patientConfirmsTakingLastWeeksDose(ivrResponse);
        caller.hangup();
    }

    private void patientConfirmsTakingLastWeeksDose(IVRResponse ivrResponse) {
        IVRAssert.asksForCollectDtmfWith(ivrResponse, PILL_REMINDER_RESPONSE_MENU, ITS_TIME_FOR_THE_PILL_OUTGOING_CALL_FOR_CURRENT_DOSAGE, FROM_THE_BOTTLE_OUTGOING_CALL_FOR_CURRENT_DOSAGE);
        ivrResponse = caller.enter("1");
        IVRAssert.assertAudioFilesPresent(ivrResponse, DOSE_RECORDED);
    }

    private void triggerAdherenceFallingJob() {
        scheduledTaskManager.trigger(AdherenceTrendListener.class, "handleAdherenceTrendEvent", patient.id());
    }

    private void verifyCreationOfAdherenceFallingAlertForThePatient() {
        LoginPage loginPage = MyPageFactory.initElements(webDriver, LoginPage.class);
        ListPatientsPage listPatientsPage = loginPage.loginWithClinicianUserNamePassword(clinician.userName(), clinician.password());
        UpdateAlertPage updateAlertPage = listPatientsPage.goToAlertsPage().filterUnreadAlerts().openUpdateAlertPage(patient.patientId());
        updateAlertPage.changeNotes("testnotes");
        ShowAlertPage showAlertsPage = updateAlertPage.save();
        assertEquals(patient.patientId(), showAlertsPage.patientId());
        assertEquals("FallingAdherence", showAlertsPage.alertType());
        assertEquals("Adherence fell by 50.00%, from 100.00% to 50.00%", showAlertsPage.description());
        assertEquals("Daily", showAlertsPage.callPreference());
        showAlertsPage.logout();
    }

    private void verifyOutboxMessageCreated() {
        caller = caller(patient);
        caller.hangup();
        caller.replyToCall(new OutboxCallInfo());
        IVRResponse ivrResponse = caller.enter("1234");
        IVRAssert.assertAudioFilesPresent(ivrResponse, DEFAULT_OUTBOUND_CLINIC_MESSAGE, FILE_050_03_01_ITS_TIME_FOR_BEST_CALL_TIME);
        ivrResponse = caller.listenMore();
        IVRAssert.assertAudioFilesPresent(ivrResponse, YOUR_ADHERENCE_IS, "Num_050", PERCENT, M02_07_ADHERENCE_COMMENT_LT70_FALLING);
        ivrResponse = caller.listenMore();
        IVRAssert.assertAudioFilesPresent(ivrResponse, THESE_WERE_YOUR_MESSAGES_FOR_NOW);
        caller.hangup();
    }
}
