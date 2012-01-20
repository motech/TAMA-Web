package org.motechproject.tamaregression;

import org.joda.time.LocalDate;
import org.joda.time.LocalTime;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.motechproject.tama.ivr.TamaIVRMessage;
import org.motechproject.tamafunctional.framework.*;
import org.motechproject.tamafunctional.ivr.Caller;
import org.motechproject.tamafunctional.page.LoginPage;
import org.motechproject.tamafunctional.testdata.*;
import org.motechproject.tamafunctional.testdata.ivrreponse.IVRResponse;
import org.motechproject.tamafunctional.testdata.ivrrequest.OutgoingCallInfo;
import org.motechproject.tamafunctional.testdata.treatmentadvice.TestDrugDosage;
import org.motechproject.tamafunctional.testdata.treatmentadvice.TestTreatmentAdvice;
import org.motechproject.tamafunctional.testdataservice.PatientDataService;
import org.motechproject.util.DateUtil;

import java.util.HashMap;

import static org.motechproject.tama.ivr.TamaIVRMessage.*;
import static org.motechproject.tamafunctional.test.ivr.IVRAssert.assertAudioFilesPresent;

public class BasicPillReminderScenario extends BaseTest {

    public static final String INCORRECT_PASSCODE = "9888";

    private TestClinician clinician;
    private MyWebClient webClient = new MyWebClient();

    @BeforeClass
    public void setUpScenario() {
        final TestClinic clinic = TestClinic.withMandatory();
        clinician = TestClinician.withMandatory().clinic(clinic);
        MyPageFactory.initElements(webDriver, LoginPage.class)
                .loginWithCorrectAdminUserNamePassword()
                .goToClinicRegistrationPage()
                .registerClinic(clinic)
                .goToClinicianRegistrationPage()
                .registerClinician(clinician)
                .logout();
    }

    @Test
    public void shouldRegisterPatientAndTestIncomingCall() throws Exception {

        TestPatient patient = TestPatient.withMandatory();
        PatientDataService patientDataService = new PatientDataService(webDriver);

        patientDataService.registerAndActivate(patient, clinician);
        TestTreatmentAdvice treatmentAdvice = TestTreatmentAdvice.withExtrinsic(
                TestDrugDosage.create(yesterday(), new LocalTime().plusMinutes(1), "Efferven", "Combivir")
            );
        patientDataService.createARTRegimen(treatmentAdvice, patient, clinician);

        final Caller caller = new Caller(unique("sid"), patient.mobileNumber(), webClient);

        verifyIncorrectPinRepeatsSignatureMusic(caller);

        verifyLoginWithCorrectPasscodeAndPillNamesInResponse(caller);

        enterOption3ForCannotTakeDoseAndVerifyMissedPillFeedback(caller);

        enter2ForDontHavePillsAndVerifyMessageToCarryPillsPlayed(caller);

        verifyPreviousDoseMessage(caller);

        enter1ForPreviousDoseTakenAndVerifyAdherence(caller);

        verifyRepeatMenuPlayed(caller);

        verifyHangup(caller);


        patientSwitchesFromDailyToWeekly(patient);

    }

    private void patientSwitchesFromDailyToWeekly(TestPatient patient) {
        MyPageFactory.initElements(webDriver, LoginPage.class)
                .loginWithClinicianUserNamePassword(clinician.userName(), clinician.password())
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
                "pillefv_efavir",
                PILL_FROM_THE_BOTTLE,
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
        assertAudioFilesPresent(caller.enter("1234"),
                DEFAULT_OUTBOUND_CLINIC_MESSAGE,
                ITS_TIME_FOR_THE_PILL,
                "pillazt3tc_combivir",
                "pillefv_efavir",
                "001_07_07_fromthebottle1",
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
