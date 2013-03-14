package org.motechproject.tamaregression.serial;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.motechproject.tamadatasetup.service.TAMADateTimeService;
import org.motechproject.tamafunctionalframework.framework.MyPageFactory;
import org.motechproject.tamafunctionalframework.ivr.BaseIVRTest;
import org.motechproject.tamafunctionalframework.ivr.IVRAssert;
import org.motechproject.tamafunctionalframework.page.LoginPage;
import org.motechproject.tamafunctionalframework.testdata.PillReminderCallInfo;
import org.motechproject.tamafunctionalframework.testdata.TestClinician;
import org.motechproject.tamafunctionalframework.testdata.TestPatient;
import org.motechproject.tamafunctionalframework.testdata.TestPatientPreferences;
import org.motechproject.tamafunctionalframework.testdata.ivrreponse.IVRResponse;
import org.motechproject.tamafunctionalframework.testdata.treatmentadvice.TestDrugDosage;
import org.motechproject.tamafunctionalframework.testdata.treatmentadvice.TestTreatmentAdvice;
import org.motechproject.tamafunctionalframework.testdataservice.PatientDataService;
import org.motechproject.util.DateUtil;

import java.io.IOException;

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

    @After
    public void tearDown() throws IOException {
        tamaDateTimeService.adjustDateTime(DateUtil.now());
        super.tearDown();
    }

    private void setUpPatientOnDailyPillReminder() {
        registrationTime = DateUtil.newDateTime(DateUtil.newDate(2011, 12, 25), now.getHourOfDay(), now.getMinuteOfHour(), 0);
        tamaDateTimeService.adjustDateTime(registrationTime);

        clinician = TestClinician.withMandatory();
        patient = TestPatient.withMandatory().callPreference(TestPatientPreferences.CallPreference.DAILY_CALL);
        PatientDataService patientDataService = new PatientDataService(webDriver);
        TestTreatmentAdvice treatmentAdvice = setUpTreatmentAdviceOn(registrationTime.toLocalDate());
        patientDataService.setupRegimenWithDependents(treatmentAdvice, patient, clinician);
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
        TestDrugDosage[] drugDosages = TestDrugDosage.create("Combivir", "Efferven");
        drugDosages[0].startDate(treatmentAdviceStartDate);
        drugDosages[1].startDate(treatmentAdviceStartDate);
        return TestTreatmentAdvice.withExtrinsic(drugDosages);
    }

    private void recordCurrentDoseAsTaken() {
        caller = caller(patient);
        caller.call();
        IVRResponse ivrResponse = caller.enter(patient.patientPreferences().passcode());
        IVRAssert.asksForCollectDtmfWith(ivrResponse, ITS_TIME_FOR_THE_PILL_INCOMING_CALL_INSIDE_PILL_WINDOW, FROM_THE_BOTTLE_INCOMING_CALL_INSIDE_PILL_WINDOW, DOSE_TAKEN_MENU_OPTION, SYMPTOMS_REPORTING_MENU_OPTION);
        ivrResponse = caller.enter("1");
        IVRAssert.assertAudioFilesPresent(ivrResponse, DOSE_RECORDED);
        caller.hangup();
    }

    private void assertIsPatientOnDailyPillReminder(IVRResponse ivrResponse){
        assertPatientCanCallTAMAAndReportAdherence(ivrResponse);
    }

    private void assertPatientCanCallTAMAAndReportAdherence(IVRResponse ivrResponse) {
        IVRAssert.asksForCollectDtmfWith(ivrResponse, PILL_REMINDER_RESPONSE_MENU, ITS_TIME_FOR_THE_PILL_OUTGOING_CALL_FOR_CURRENT_DOSAGE, FROM_THE_BOTTLE_OUTGOING_CALL_FOR_CURRENT_DOSAGE);
        caller.enter("3");
        ivrResponse = caller.enter("2");
        IVRAssert.assertAudioFilesPresent(ivrResponse, YOUR_ADHERENCE_IS_NOW);
        IVRAssert.assertAudioFilesPresent(ivrResponse, "Num_000");
        IVRAssert.assertAudioFilesPresent(ivrResponse, PERCENT);
    }
}