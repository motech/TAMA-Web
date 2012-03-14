package org.motechproject.tamasmoke;

import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;
import org.motechproject.tama.dailypillreminder.listener.AdherenceQualityListener;
import org.motechproject.tama.fourdayrecall.listener.FourDayRecallListener;
import org.motechproject.tamafunctionalframework.framework.BaseTest;
import org.motechproject.tamafunctionalframework.framework.MyPageFactory;
import org.motechproject.tamafunctionalframework.framework.MyWebClient;
import org.motechproject.tamafunctionalframework.framework.ScheduledTaskManager;
import org.motechproject.tamafunctionalframework.ivr.Caller;
import org.motechproject.tamafunctionalframework.page.AlertsPage;
import org.motechproject.tamafunctionalframework.page.ListPatientsPage;
import org.motechproject.tamafunctionalframework.page.LoginPage;
import org.motechproject.tamafunctionalframework.testdata.TestClinician;
import org.motechproject.tamafunctionalframework.testdata.TestPatient;
import org.motechproject.tamafunctionalframework.testdata.ivrreponse.IVRResponse;
import org.motechproject.tamafunctionalframework.testdata.ivrrequest.OutgoingCallInfo;
import org.motechproject.tamafunctionalframework.testdata.treatmentadvice.TestDrugDosage;
import org.motechproject.tamafunctionalframework.testdata.treatmentadvice.TestTreatmentAdvice;
import org.motechproject.tamafunctionalframework.testdataservice.ClinicianDataService;
import org.motechproject.tamafunctionalframework.testdataservice.PatientDataService;
import org.motechproject.util.DateUtil;

import java.util.HashMap;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.motechproject.tama.ivr.TamaIVRMessage.*;
import static org.motechproject.tamafunctionalframework.ivr.IVRAssert.assertAudioFilesPresent;

public class BasicPillReminderTest extends BaseTest {

    public static final String INCORRECT_PASSCODE = "9888";
    public static final String PATIENT_PIN = "1234";

    private TestClinician clinician;
    private MyWebClient webClient = new MyWebClient();
    private ScheduledTaskManager scheduledTaskManager;

    @Before
    public void setUp() {
        super.setUp();
        clinician = TestClinician.withMandatory();
        scheduledTaskManager = new ScheduledTaskManager(webClient);
        new ClinicianDataService(webDriver).createWithClinic(clinician);
    }

    @Test
    public void shouldRegisterPatientAndTestIncomingCall() throws Exception {
        TestPatient patient = TestPatient.withMandatory();
        PatientDataService patientDataService = new PatientDataService(webDriver);

        int timeSlotDelta = DateUtil.now().getMinuteOfHour() % 15;
        TestTreatmentAdvice treatmentAdvice = TestTreatmentAdvice.withExtrinsic(
                TestDrugDosage.create(yesterday(), DateUtil.now().minusMinutes(timeSlotDelta).toLocalTime(), "Combivir", "Efferven")
            );
        patientDataService.registerAndActivate(treatmentAdvice, patient, clinician);

        final Caller caller = new Caller(unique("sid"), patient.mobileNumber(), webClient);

        verifyIncorrectPinRepeatsSignatureMusic(caller);

        verifyLoginWithCorrectPasscodeAndPillNamesInResponse(caller);

        enterOption3ForCannotTakeDoseAndVerifyMissedPillFeedback(caller);

        enter2ForDontHavePillsAndVerifyMessageToCarryPillsPlayed(caller);

        verifyPreviousDoseMessage(caller);

        enter1ForPreviousDoseTakenAndVerifyAdherence(caller);

        verifyRepeatMenuPlayed(caller);

        triggerAdherenceInRedJob(patient);

        verifyAdherenceInRedAlertIsRaised(patient);

        verifyHangup(caller);


        patientSwitchesFromDailyToWeekly(patient);

        verifyUnschedulingOfDailyReminderJobs(patient);
        verifySchedulingOfFDRJobs(patient);

        final Caller fourDayRecallCaller = new Caller(unique("sid"), patient.mobileNumber(), webClient);
        verifyIncorrectPinRepeatsSignatureMusic(fourDayRecallCaller);
        verifyFourDayRecallMenu(fourDayRecallCaller);

        enter2ForDaysMissedAndVerifyAdherencePercentageAs50Percent(fourDayRecallCaller);

        verifyRepeatMenuPlayed(fourDayRecallCaller);

        patientSwitchesFromWeeklyToDaily(patient);

        verifySchedulingOfDailyJobs(patient);
        verifyUnschedulingOfFDRJobs(patient);
    }

    private void verifyUnschedulingOfFDRJobs(TestPatient patient) {
        assertFalse(scheduledTaskManager.exists(FourDayRecallListener.class, "handle", "0" + patient.id()));
        assertFalse(scheduledTaskManager.exists(FourDayRecallListener.class, "handleWeeklyFallingAdherenceAndRedAlert", "0" + patient.id()));
    }

    private void verifySchedulingOfDailyJobs(TestPatient patient) {
        assertTrue(scheduledTaskManager.exists(AdherenceQualityListener.class, "determineAdherenceQualityAndRaiseAlert", patient.id()));
        //todo assertTrue(dailyPillreminderJobExists());
    }

    private void patientSwitchesFromWeeklyToDaily(TestPatient patient) {
        gotoListPatientPage().gotoShowPatientPage(patient).
                clickOnEditTAMAPreferences().
                changePatientToDailyCallPlan();

    }


    private void verifySchedulingOfFDRJobs(TestPatient patient) {
        assertTrue(scheduledTaskManager.exists(FourDayRecallListener.class, "handle", "0" + patient.id()));
        assertTrue(scheduledTaskManager.exists(FourDayRecallListener.class, "handleWeeklyFallingAdherenceAndRedAlert", "0" + patient.id()));
    }

    public void verifyUnschedulingOfDailyReminderJobs(TestPatient patient) {
        assertFalse(scheduledTaskManager.exists(AdherenceQualityListener.class, "determineAdherenceQualityAndRaiseAlert", patient.id()));
        //todo assertFalse(dailyPillreminderJobExists());
    }

    private void verifyAdherenceInRedAlertIsRaised(TestPatient patient) {
        final AlertsPage alertsPage = gotoListPatientPage()
                .goToAlertsPage();
        alertsPage.assertTableContainsAlert(patient.patientId(), patient.mobileNumber(), "", "");
        alertsPage.logout();
    }

    private ListPatientsPage gotoListPatientPage() {
        return MyPageFactory.initElements(webDriver, LoginPage.class)
                .loginWithClinicianUserNamePassword(clinician.userName(), clinician.password());
    }

    private void triggerAdherenceInRedJob(TestPatient patient) {
        scheduledTaskManager.trigger(AdherenceQualityListener.class, "determineAdherenceQualityAndRaiseAlert", patient.id());
    }

    private void enter2ForDaysMissedAndVerifyAdherencePercentageAs50Percent(Caller fourDayRecallCaller) {
        assertAudioFilesPresent(fourDayRecallCaller.enter("2"),
                FDR_MISSED_ONE_DOSAGE_ON_MULTIPLE_DAYS_PART_1,
                "num_002",
                FDR_MISSED_ONE_DOSAGE_ON_MULTIPLE_DAYS_PART_2,
                FDR_TAKE_DOSAGES_REGULARLY,
                FDR_YOUR_WEEKLY_ADHERENCE_IS,
                "num_050",
                FDR_PERCENT);
    }

    private void verifyFourDayRecallMenu(Caller fourDayRecallCaller) {
        assertAudioFilesPresent(fourDayRecallCaller.enter(PATIENT_PIN),
                DEFAULT_OUTBOUND_CLINIC_MESSAGE,
                FDR_GREETING,
                FDR_MENU_FOR_SINGLE_DOSAGE
                );
    }

    private void patientSwitchesFromDailyToWeekly(TestPatient patient) {
        gotoListPatientPage()
                .gotoShowPatientPage(patient)
                .clickOnEditTAMAPreferences().
                changePatientToWeeklyCallPlanWithBestCallDayAndTime("Monday", "09:05", true).logout();
    }

    private void verifyHangup(Caller caller) {
        caller.hangup();
    }

    private void verifyRepeatMenuPlayed(Caller caller) {
        assertAudioFilesPresent(caller.enter(""), HANGUP_OR_MAIN_MENU);
        assertAudioFilesPresent(caller.enter(""), SYMPTOMS_REPORTING_MENU_OPTION, HEALTH_TIPS_MENU_OPTION);
    }

    private void enter1ForPreviousDoseTakenAndVerifyAdherence(Caller caller) {
        assertAudioFilesPresent(caller.enter("1"),
                YOU_SAID_YOU_TOOK,
                YESTERDAYS_CONFIRMATION,
                DOSE_TAKEN,
                DOSE_RECORDED,
                YOUR_ADHERENCE_IS_NOW,
                "num_050",
                PERCENT);
    }

    private void verifyPreviousDoseMessage(Caller caller) {
        assertAudioFilesPresent(caller.enter(""),
                YOUR,
                YESTERDAYS,
                DOSE_NOT_RECORDED,
                YESTERDAY,
                YOU_WERE_SUPPOSED_TO_TAKE,
                "pillazt3tc_combivir",
                "pillefv_efferven",
                FROM_THE_BOTTLE_FOR_PREVIOUS_DOSAGE,
                PREVIOUS_DOSE_MENU);
    }

    private void enter2ForDontHavePillsAndVerifyMessageToCarryPillsPlayed(Caller caller) {
        assertAudioFilesPresent(caller.enter("2"),
                PLEASE_CARRY_SMALL_BOX);

    }

    private void enterOption3ForCannotTakeDoseAndVerifyMissedPillFeedback(Caller caller) {
        assertAudioFilesPresent(caller.enter("3"),
                MISSED_PILL_FEEDBACK_FIRST_TIME, //MISSED_PILL_FEEDBACK_SECOND_TO_FOURTH_TIME,
                DOSE_CANNOT_BE_TAKEN_MENU);
    }

    private void verifyLoginWithCorrectPasscodeAndPillNamesInResponse(Caller caller) {
        assertAudioFilesPresent(caller.enter(PATIENT_PIN),
                DEFAULT_OUTBOUND_CLINIC_MESSAGE,
                ITS_TIME_FOR_THE_PILL_OUTGOING_CALL_FOR_CURRENT_DOSAGE,
                "pillazt3tc_combivir",
                "pillefv_efferven",
                FROM_THE_BOTTLE_OUTGOING_CALL_FOR_CURRENT_DOSAGE,
                "001_02_05_pilltimemenu"
        );
    }

    private void verifyIncorrectPinRepeatsSignatureMusic(Caller caller) {
        final IVRResponse ivrResponse = caller.replyToCall(new OutgoingCallInfo(new HashMap<String, String>()));
        assertAudioFilesPresent(ivrResponse, SIGNATURE_MUSIC);
        assertAudioFilesPresent(caller.enter(INCORRECT_PASSCODE), SIGNATURE_MUSIC);
    }

    private LocalDate yesterday() {
        return DateUtil.today().minusDays(1);
    }
}
