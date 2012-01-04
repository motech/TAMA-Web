package org.motechproject.tamafunctional.serial;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.tamadatasetup.service.TAMADateTimeService;
import org.motechproject.tamafunctional.framework.ScheduledTaskManager;
import org.motechproject.tamafunctional.test.ivr.BaseIVRTest;
import org.motechproject.tamafunctional.test.ivr.IVRAssert;
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

import static org.motechproject.tama.ivr.TamaIVRMessage.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath*:**/applicationFunctionalTestContext.xml")
public class WeeklyToDailyTransitionTest extends BaseIVRTest {

    @Autowired
    private ScheduledJobDataService scheduledJobDataService;
    private TestPatient patient;
    private TestClinician clinician;
    private ScheduledTaskManager scheduledTaskManager;
    private TAMADateTimeService tamaDateTimeService;

    private DateTime now = DateUtil.now();

    @Before
    public void setUp() {
        super.setUp();
        scheduledTaskManager = new ScheduledTaskManager(webClient);
        tamaDateTimeService = new TAMADateTimeService(webClient);
        tamaDateTimeService.adjustDateTime(DateUtil.newDateTime(DateUtil.newDate(2012, 01, 03), now.getHourOfDay(), now.getMinuteOfHour(), 0));
        setupData();
    }

    @Test
    @Ignore
    public void shouldRecordAdherenceAfreshWhenPatientChangesFromWeeklyToDailyPillReminder() {
        for (int i = 3; i >= 0; i--) {
            tamaDateTimeService.adjustDateTime(DateUtil.newDateTime(DateUtil.newDate(2012, 01, 03), now.getHourOfDay(), now.getMinuteOfHour(), 0).minusDays(6).minusDays(i));
            recordCurrentDosageAsTaken();
        }

        tamaDateTimeService.adjustDateTime(DateUtil.newDateTime(DateUtil.newDate(2012, 01, 03), now.getHourOfDay(), now.getMinuteOfHour(), 0).minusDays(6));
        PatientDataService patientDataService = new PatientDataService(webDriver);
        patientDataService.changePatientToWeeklyPillReminderWithBestCallDay_AndBestCallTime(patient, clinician, "Monday", "09:05");

        tamaDateTimeService.adjustDateTime(DateUtil.newDateTime(DateUtil.newDate(2012, 01, 03), now.getHourOfDay(), now.getMinuteOfHour(), 0));
        patientDataService.changePatientToDailyPillReminder(patient, clinician);
        String currentDosageId = scheduledJobDataService.currentDosageId(patient.id());
        caller.replyToCall(new PillReminderCallInfo(currentDosageId, 1));
        IVRResponse ivrResponse = caller.enter(patient.patientPreferences().passcode());
        patientSaysHeDidNotTakeTheDose(ivrResponse);
        caller.hangup();
    }

    private void setupData() {
        super.setUp();
        clinician = TestClinician.withMandatory();
        patient = TestPatient.withMandatory();
        PatientDataService patientDataService = new PatientDataService(webDriver);
        TestTreatmentAdvice treatmentAdvice = setUpTreatmentAdviceToStartFrom1AndAHalfWeeksAgo();
        patientDataService.setupARTRegimenWithDependents(treatmentAdvice, patient, clinician);
        caller = caller(patient);
    }

    private TestTreatmentAdvice setUpTreatmentAdviceToStartFrom1AndAHalfWeeksAgo() {
        TestDrugDosage[] drugDosages = TestDrugDosage.create("Efferven", "Combivir");
        LocalDate oneAndAHalfWeeksAgo = DateUtil.newDate(2012, 01, 03).minusDays(9);
        drugDosages[0].startDate(oneAndAHalfWeeksAgo);
        drugDosages[1].startDate(oneAndAHalfWeeksAgo);
        return TestTreatmentAdvice.withExtrinsic(drugDosages);
    }

    public void recordCurrentDosageAsTaken() {
        caller = caller(patient);
        caller.call();
        IVRResponse ivrResponse = caller.enter(patient.patientPreferences().passcode());
        IVRAssert.asksForCollectDtmfWith(ivrResponse, ITS_TIME_FOR_THE_PILL, PILL_FROM_THE_BOTTLE, DOSE_TAKEN_MENU_OPTION, SYMPTOMS_REPORTING_MENU_OPTION, HEALTH_TIPS_MENU_OPTION);
        ivrResponse = caller.enter("1");
        IVRAssert.assertAudioFilesPresent(ivrResponse, DOSE_RECORDED);
        caller.hangup();
    }

    private void patientSaysHeDidNotTakeTheDose(IVRResponse ivrResponse) {
        IVRAssert.asksForCollectDtmfWith(ivrResponse, PILL_REMINDER_RESPONSE_MENU, ITS_TIME_FOR_THE_PILL, PILL_FROM_THE_BOTTLE);
        ivrResponse = caller.enter("3");
        ivrResponse = caller.enter("2");
        ivrResponse = caller.listenMore();
        IVRAssert.assertAudioFilesPresent(ivrResponse, YOUR_ADHERENCE_IS_NOW);
        IVRAssert.assertAudioFilesPresent(ivrResponse, "Num_000");
        IVRAssert.assertAudioFilesPresent(ivrResponse, PERCENT);
    }
}