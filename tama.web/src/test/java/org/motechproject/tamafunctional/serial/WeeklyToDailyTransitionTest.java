package org.motechproject.tamafunctional.serial;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;
import org.motechproject.tamadatasetup.service.TAMADateTimeService;
import org.motechproject.tamafunctional.framework.MyPageFactory;
import org.motechproject.tamafunctional.page.LoginPage;
import org.motechproject.tamafunctional.test.ivr.BaseIVRTest;
import org.motechproject.tamafunctional.test.ivr.IVRAssert;
import org.motechproject.tamafunctional.testdata.PillReminderCallInfo;
import org.motechproject.tamafunctional.testdata.TestClinician;
import org.motechproject.tamafunctional.testdata.TestPatient;
import org.motechproject.tamafunctional.testdata.TestPatientPreferences;
import org.motechproject.tamafunctional.testdata.ivrreponse.IVRResponse;
import org.motechproject.tamafunctional.testdata.treatmentadvice.TestDrugDosage;
import org.motechproject.tamafunctional.testdata.treatmentadvice.TestTreatmentAdvice;
import org.motechproject.tamafunctional.testdataservice.PatientDataService;
import org.motechproject.util.DateUtil;

import static org.motechproject.tama.ivr.TamaIVRMessage.*;

public class WeeklyToDailyTransitionTest extends BaseIVRTest {

    private TestPatient patient;
    private TestClinician clinician;
    private TAMADateTimeService tamaDateTimeService;

    private DateTime now = DateUtil.now();
    private DateTime registrationTime;

    @Before
    public void setUp() {
        super.setUp();
        tamaDateTimeService = new TAMADateTimeService(webClient);
        setUpPatientOnDailyPillReminder();
    }

    private void setUpPatientOnDailyPillReminder() {
        registrationTime = DateUtil.newDateTime(DateUtil.newDate(2011, 12, 25), now.getHourOfDay(), now.getMinuteOfHour(), 0);
        tamaDateTimeService.adjustDateTime(registrationTime);

        clinician = TestClinician.withMandatory();
        patient = TestPatient.withMandatory().callPreference(TestPatientPreferences.CallPreference.DAILY_CALL);
        PatientDataService patientDataService = new PatientDataService(webDriver);
        TestTreatmentAdvice treatmentAdvice = setUpTreatmentAdviceOn(registrationTime.toLocalDate());
        patientDataService.setupARTRegimenWithDependents(treatmentAdvice, patient, clinician);
        caller = caller(patient);
    }

    @Test
    public void shouldRecordAdherenceAfreshWhenPatientChangesFromWeeklyToDailyPillReminder() {
        for (int i=0; i<=3; i++) {
            tamaDateTimeService.adjustDateTime(registrationTime.plusDays(i));
            recordCurrentDoseAsTaken();
        }

        DateTime transitionTime = registrationTime.plusDays(3);

        tamaDateTimeService.adjustDateTime(transitionTime);

        MyPageFactory.initElements(webDriver, LoginPage.class)
                .loginWithClinicianUserNamePassword(clinician.userName(), clinician.password())
                .gotoShowPatientPage(patient)
                .clickOnEditTAMAPreferences().
                changePatientToWeeklyCallPlanWithBestCallDayAndTime("Monday", "09:05", true).logout();

        DateTime firstWeeklyCallTime = now;
        tamaDateTimeService.adjustDateTime(firstWeeklyCallTime);

        MyPageFactory.initElements(webDriver, LoginPage.class)
                .loginWithClinicianUserNamePassword(clinician.userName(), clinician.password())
                .gotoShowPatientPage(patient)
                .clickOnEditTAMAPreferences()
                .changePatientToDailyCallPlan().logout();

        caller.replyToCall(new PillReminderCallInfo(1));
        IVRResponse ivrResponse = caller.enter(patient.patientPreferences().passcode());
        assertIsPatientOnDailyPillReminder(ivrResponse);
        caller.hangup();
    }

    private TestTreatmentAdvice setUpTreatmentAdviceOn(LocalDate treatmentAdviceStartDate) {
        TestDrugDosage[] drugDosages = TestDrugDosage.create("Efferven", "Combivir");
        drugDosages[0].startDate(treatmentAdviceStartDate);
        drugDosages[1].startDate(treatmentAdviceStartDate);
        return TestTreatmentAdvice.withExtrinsic(drugDosages);
    }

    private void recordCurrentDoseAsTaken() {
        caller = caller(patient);
        caller.call();
        IVRResponse ivrResponse = caller.enter(patient.patientPreferences().passcode());
        IVRAssert.asksForCollectDtmfWith(ivrResponse, ITS_TIME_FOR_THE_PILL, PILL_FROM_THE_BOTTLE, DOSE_TAKEN_MENU_OPTION, SYMPTOMS_REPORTING_MENU_OPTION, HEALTH_TIPS_MENU_OPTION);
        ivrResponse = caller.enter("1");
        IVRAssert.assertAudioFilesPresent(ivrResponse, DOSE_RECORDED);
        caller.hangup();
    }

    private void assertIsPatientOnDailyPillReminder(IVRResponse ivrResponse){
        assertPatientCanCallTAMAAndReportAdherence(ivrResponse);
    }

    private void assertPatientCanCallTAMAAndReportAdherence(IVRResponse ivrResponse) {
        IVRAssert.asksForCollectDtmfWith(ivrResponse, PILL_REMINDER_RESPONSE_MENU, ITS_TIME_FOR_THE_PILL, PILL_FROM_THE_BOTTLE);
        ivrResponse = caller.enter("3");
        ivrResponse = caller.enter("2");
        IVRAssert.assertAudioFilesPresent(ivrResponse, YOUR_ADHERENCE_IS_NOW);
        IVRAssert.assertAudioFilesPresent(ivrResponse, "Num_000");
        IVRAssert.assertAudioFilesPresent(ivrResponse, PERCENT);
    }
}