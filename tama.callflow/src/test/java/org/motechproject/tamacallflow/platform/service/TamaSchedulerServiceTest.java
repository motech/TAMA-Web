package org.motechproject.tamacallflow.platform.service;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.motechproject.model.CronSchedulableJob;
import org.motechproject.model.DayOfWeek;
import org.motechproject.model.MotechEvent;
import org.motechproject.model.RepeatingSchedulableJob;
import org.motechproject.scheduler.MotechSchedulerService;
import org.motechproject.server.pillreminder.EventKeys;
import org.motechproject.tamacommon.TAMAConstants;
import org.motechproject.tamadomain.builder.PatientBuilder;
import org.motechproject.tamadomain.domain.*;
import org.motechproject.tamadomain.repository.AllPatients;
import org.motechproject.tamacallflow.listener.FourDayRecallListener;
import org.motechproject.util.DateUtil;

import java.util.*;

import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.powermock.api.mockito.PowerMockito.when;

public class TamaSchedulerServiceTest {
    public static final int DAYS_AFTER_CALL_PREFERENCE_CHANGES = 3;
    public static final int WAIT_TIME_TO_START_FOURDAY_RECALL = 4;
    private final LocalDate TREATMENT_ADVICE_START_DATE = DateUtil.newDate(2012, 12, 12);
    private final LocalDate TREATMENT_ADVICE_END_DATE = DateUtil.newDate(2012, 12, 24);
    private static final String PATIENT_ID = "patient_id";

    private TamaSchedulerService schedulerService;
    private TreatmentAdvice treatmentAdvice;
    private Patient patient;

    @Mock
    MotechSchedulerService motechSchedulerService;
    @Mock
    FourDayRecallService fourDayRecallService;
    @Mock
    private Properties properties;
    @Mock
    private AllPatients allPatients;

    @Before
    public void setUp() {
        initMocks(this);

        treatmentAdvice = getTreatmentAdvice();
        final TimeOfDay bestCallTime = new TimeOfDay(10, 30, TimeMeridiem.AM);
        patient = new Patient() {{
            setId(PATIENT_ID);
            getPatientPreferences().setBestCallTime(bestCallTime);
        }};
        schedulerService = new TamaSchedulerService(motechSchedulerService, properties, allPatients, fourDayRecallService);
    }

    @Test
    public void shouldScheduleFourDayRecallJobs_AndFallingAdherenceAlertJobs_StartDateIsToday() {
        DayOfWeek dayOfWeek = DayOfWeek.Friday;
        int numDaysToRetry = 2;
        patient.getPatientPreferences().setDayOfWeeklyCall(dayOfWeek);

        when(properties.getProperty(TAMAConstants.FOUR_DAY_RECALL_DAYS_TO_RETRY)).thenReturn(String.valueOf(numDaysToRetry));
        LocalDate dayWhenFirstCallIsMade = DateUtil.today().plusDays(10);
        LocalDate expectedFallingAdherenceAlertJobStartDate = dayWhenFirstCallIsMade.plusDays(1);
        Mockito.when(fourDayRecallService.findFirstFourDayRecallDateForTreatmentAdvice(PATIENT_ID, DateUtil.newDate(treatmentAdvice.getStartDate()))).thenReturn(dayWhenFirstCallIsMade);

        schedulerService.scheduleJobsForFourDayRecall(patient, treatmentAdvice);

        ArgumentCaptor<CronSchedulableJob> cronSchedulableJobArgumentCaptor = ArgumentCaptor.forClass(CronSchedulableJob.class);
        verify(motechSchedulerService, times(2 * (1 + numDaysToRetry))).scheduleJob(cronSchedulableJobArgumentCaptor.capture());
        List<CronSchedulableJob> cronSchedulableJobList = cronSchedulableJobArgumentCaptor.getAllValues();

        assertFourDayRecallCallJob(cronSchedulableJobList.get(0), "0 30 10 ? * 6");
        assertFourDayRecallCallJob(cronSchedulableJobList.get(1), "0 30 10 ? * 7");
        assertFourDayRecallCallJob(cronSchedulableJobList.get(2), "0 30 10 ? * 1");

        assertFallingAdherenceAlertJob(cronSchedulableJobList.get(3), "0 0 0 ? * 7", false, expectedFallingAdherenceAlertJobStartDate);
        assertFallingAdherenceAlertJob(cronSchedulableJobList.get(4), "0 0 0 ? * 1", false, expectedFallingAdherenceAlertJobStartDate);
        assertFallingAdherenceAlertJob(cronSchedulableJobList.get(5), "0 0 0 ? * 2", true, expectedFallingAdherenceAlertJobStartDate);

    }


    @Test
    public void shouldScheduleFourDayRecallJobs_AndFallingAdherenceAlertJobs_WhenCallPreferenceChanges() {
        DayOfWeek dayOfWeek = DayOfWeek.Friday;
        int numDaysToRetry = 2;
        patient.getPatientPreferences().setDayOfWeeklyCall(dayOfWeek);
        patient.getPatientPreferences().setCallPreferenceTransitionDate(TREATMENT_ADVICE_START_DATE.plusDays(DAYS_AFTER_CALL_PREFERENCE_CHANGES).toDateTimeAtCurrentTime());

        when(properties.getProperty(TAMAConstants.FOUR_DAY_RECALL_DAYS_TO_RETRY)).thenReturn(String.valueOf(numDaysToRetry));
        LocalDate dayWhenFirstCallIsMade = DateUtil.today().plusDays(10);
        LocalDate expectedFallingAdherenceAlertJobStartDate = dayWhenFirstCallIsMade.plusDays(1);

        Mockito.when(fourDayRecallService.findFirstFourDayRecallDateForTreatmentAdvice(PATIENT_ID, DateUtil.newDate(treatmentAdvice.getStartDate()).plusDays(DAYS_AFTER_CALL_PREFERENCE_CHANGES))).thenReturn(dayWhenFirstCallIsMade);

        schedulerService.scheduleJobsForFourDayRecall(patient, treatmentAdvice);

        ArgumentCaptor<CronSchedulableJob> cronSchedulableJobArgumentCaptor = ArgumentCaptor.forClass(CronSchedulableJob.class);
        verify(motechSchedulerService, times(2 * (1 + numDaysToRetry))).scheduleJob(cronSchedulableJobArgumentCaptor.capture());
        List<CronSchedulableJob> cronSchedulableJobList = cronSchedulableJobArgumentCaptor.getAllValues();

        assertFourDayRecallCallJobWhenCallPreferenceChanged(cronSchedulableJobList.get(0), "0 30 10 ? * 6");
        assertFourDayRecallCallJobWhenCallPreferenceChanged(cronSchedulableJobList.get(1), "0 30 10 ? * 7");
        assertFourDayRecallCallJobWhenCallPreferenceChanged(cronSchedulableJobList.get(2), "0 30 10 ? * 1");

        assertFallingAdherenceAlertJobWhenCallPreferenceChanged(cronSchedulableJobList.get(3), "0 0 0 ? * 7", false, expectedFallingAdherenceAlertJobStartDate);
        assertFallingAdherenceAlertJobWhenCallPreferenceChanged(cronSchedulableJobList.get(4), "0 0 0 ? * 1", false, expectedFallingAdherenceAlertJobStartDate);
        assertFallingAdherenceAlertJobWhenCallPreferenceChanged(cronSchedulableJobList.get(5), "0 0 0 ? * 2", true, expectedFallingAdherenceAlertJobStartDate);

    }

    @Test
    public void shouldScheduleFourDayRecallJobsStartingNow_StartDateIsBeforeToday() {
        DayOfWeek dayOfWeek = DayOfWeek.Friday;
        DateTime now = DateUtil.now();
        LocalDate today = now.toLocalDate();
        final LocalDate treatmentAdviceStartDate = today.minusDays(WAIT_TIME_TO_START_FOURDAY_RECALL);

        int numDaysToRetry = 2;
        patient.getPatientPreferences().setDayOfWeeklyCall(dayOfWeek);
        treatmentAdvice.getDrugDosages().get(0).setStartDate(treatmentAdviceStartDate);
        when(properties.getProperty(TAMAConstants.FOUR_DAY_RECALL_DAYS_TO_RETRY)).thenReturn(String.valueOf(numDaysToRetry));

        LocalDate dayWhenFirstCallIsMade = DateUtil.today();

        Mockito.when(fourDayRecallService.findFirstFourDayRecallDateForTreatmentAdvice(PATIENT_ID, treatmentAdviceStartDate)).thenReturn(dayWhenFirstCallIsMade);


        schedulerService.scheduleJobsForFourDayRecall(patient, treatmentAdvice);
        ArgumentCaptor<CronSchedulableJob> cronSchedulableJobArgumentCaptor = ArgumentCaptor.forClass(CronSchedulableJob.class);
        verify(motechSchedulerService, times(6)).scheduleJob(cronSchedulableJobArgumentCaptor.capture());
        List<CronSchedulableJob> cronSchedulableJobList = cronSchedulableJobArgumentCaptor.getAllValues();

        assertJobIsScheduledStartingNow(now, cronSchedulableJobList.get(0));
    }

    private void assertJobIsScheduledStartingNow(DateTime now, CronSchedulableJob cronSchedulableJobList) {
        assertTrue(now.minusMinutes(1).isBefore(new DateTime(cronSchedulableJobList.getStartTime())));
        assertTrue(now.plusMinutes(1).isAfter(new DateTime(cronSchedulableJobList.getStartTime())));
    }

    @Test
    public void shouldScheduleFallingAdherenceTrendAlertJob() {
        DayOfWeek dayOfWeek = DayOfWeek.Friday;
        int numDaysToRetry = 2;
        patient.getPatientPreferences().setDayOfWeeklyCall(dayOfWeek);

        when(properties.getProperty(TAMAConstants.FOUR_DAY_RECALL_DAYS_TO_RETRY)).thenReturn(String.valueOf(numDaysToRetry));
        LocalDate dayWhenFirstCallIsMade = DateUtil.today().plusDays(10);
        LocalDate expectedFallingAdherenceAlertJobStartDate = dayWhenFirstCallIsMade.plusDays(1);
        Mockito.when(fourDayRecallService.findFirstFourDayRecallDateForTreatmentAdvice(PATIENT_ID, DateUtil.newDate(treatmentAdvice.getStartDate()))).thenReturn(dayWhenFirstCallIsMade);

        schedulerService.scheduleFallingAdherenceAlertJobsForFourDayRecall(patient, treatmentAdvice);

        ArgumentCaptor<CronSchedulableJob> cronSchedulableJobArgumentCaptor = ArgumentCaptor.forClass(CronSchedulableJob.class);
        verify(motechSchedulerService, times(3)).scheduleJob(cronSchedulableJobArgumentCaptor.capture());
        List<CronSchedulableJob> cronSchedulableJobList = cronSchedulableJobArgumentCaptor.getAllValues();
        assertFallingAdherenceAlertJob(cronSchedulableJobList.get(0), "0 0 0 ? * 7", false, expectedFallingAdherenceAlertJobStartDate);
        assertFallingAdherenceAlertJob(cronSchedulableJobList.get(1), "0 0 0 ? * 1", false, expectedFallingAdherenceAlertJobStartDate);
        assertFallingAdherenceAlertJob(cronSchedulableJobList.get(2), "0 0 0 ? * 2", true, expectedFallingAdherenceAlertJobStartDate);
    }

    private void assertCronSchedulableJob(CronSchedulableJob cronSchedulableJob, String cronExpression, Date startTime, Date endTime) {
        assertEquals(cronExpression, cronSchedulableJob.getCronExpression());
        assertEquals(startTime, cronSchedulableJob.getStartTime());
        assertEquals(endTime, cronSchedulableJob.getEndTime());
    }

    @Test
    public void shouldScheduleRepeatingJobsForFourDayRecall() {
        when(allPatients.get(PATIENT_ID)).thenReturn(patient);
        when(properties.getProperty(TAMAConstants.RETRIES_PER_DAY)).thenReturn("5");
        when(properties.getProperty(TAMAConstants.RETRY_INTERVAL)).thenReturn("15");

        schedulerService.scheduleRepeatingJobsForFourDayRecall(PATIENT_ID);

        ArgumentCaptor<RepeatingSchedulableJob> repeatingSchedulableJobArgumentCaptor = ArgumentCaptor.forClass(RepeatingSchedulableJob.class);
        verify(motechSchedulerService).scheduleRepeatingJob(repeatingSchedulableJobArgumentCaptor.capture());
        RepeatingSchedulableJob repeatingSchedulableJob = repeatingSchedulableJobArgumentCaptor.getValue();
        // 4 because repeatingSchedulableJobs are intern scheduled 1 + repeatCount number of times
        assertEquals(new Integer(4), repeatingSchedulableJob.getRepeatCount());
        assertEquals(15 * 60 * 1000, repeatingSchedulableJob.getRepeatInterval());
        assertDates(DateUtil.newDateTime(DateUtil.today(), 10, 45, 0), DateUtil.newDateTime(repeatingSchedulableJob.getStartTime()));
        assertDates(DateUtil.newDateTime(DateUtil.today(), 10, 45, 0).plusDays(1), DateUtil.newDateTime(repeatingSchedulableJob.getEndTime()));
        assertEquals(PATIENT_ID, repeatingSchedulableJob.getMotechEvent().getParameters().get(FourDayRecallListener.PATIENT_DOC_ID_KEY));
        assertEquals(true, repeatingSchedulableJob.getMotechEvent().getParameters().get(FourDayRecallListener.RETRY_EVENT_KEY));
    }

    private void assertDates(DateTime dateTime1, DateTime dateTime2) {
        String pattern = "yyyy-MM-dd HH:mm";
        assertEquals(dateTime1.toString(pattern), dateTime2.toString(pattern));
    }

    @Test
    public void shouldScheduleWeeklyAdherenceTrendJob() {
        schedulerService.scheduleJobForAdherenceTrendFeedbackForDailyPillReminder(treatmentAdvice);

        ArgumentCaptor<CronSchedulableJob> jobCaptor = ArgumentCaptor.forClass(CronSchedulableJob.class);
        verify(motechSchedulerService).scheduleJob(jobCaptor.capture());
        Assert.assertEquals("0 0 0 ? * 4", jobCaptor.getValue().getCronExpression());
    }

    @Test
    public void shouldScheduleWeeklyAdherenceTrendJob_StartDateIsBeforeToday() {
        DateTime now = DateUtil.now();
        LocalDate today = now.toLocalDate();
        treatmentAdvice.getDrugDosages().get(0).setStartDate(today.minusMonths(2));

        schedulerService.scheduleJobForAdherenceTrendFeedbackForDailyPillReminder(treatmentAdvice);
        ArgumentCaptor<CronSchedulableJob> jobCaptor = ArgumentCaptor.forClass(CronSchedulableJob.class);
        verify(motechSchedulerService).scheduleJob(jobCaptor.capture());

        //assertTrue(now.minusMinutes(1).isBefore(new DateTime(jobCaptor.getValue().getStartTime())));
    }

    @Test
    public void shouldScheduleOutboxCall() {
        PatientPreferences patientPreferences = patient.getPatientPreferences();
        CallPreference callPreference = CallPreference.DailyPillReminder;
        patientPreferences.setCallPreference(callPreference);
        schedulerService.scheduleJobForOutboxCall(patient);
        ArgumentCaptor<CronSchedulableJob> jobCaptor = ArgumentCaptor.forClass(CronSchedulableJob.class);
        verify(motechSchedulerService).scheduleJob(jobCaptor.capture());
        Assert.assertEquals("0 30 10 * * ?", jobCaptor.getValue().getCronExpression());
    }

    private TreatmentAdvice getTreatmentAdvice() {
        return getTreatmentAdvice(TREATMENT_ADVICE_START_DATE, TREATMENT_ADVICE_END_DATE);
    }

    private TreatmentAdvice getTreatmentAdvice(LocalDate startDate, LocalDate endDate) {
        TreatmentAdvice treatmentAdvice = TreatmentAdvice.newDefault();
        String TREATMENT_ADVICE_ID = "treatmentAdviceId";
        treatmentAdvice.setId(TREATMENT_ADVICE_ID);
        treatmentAdvice.setPatientId(PATIENT_ID);
        ArrayList<DrugDosage> drugDosages = new ArrayList<DrugDosage>();
        DrugDosage drugDosage = new DrugDosage();
        treatmentAdvice.setDrugCompositionGroupId("");
        drugDosage.setStartDate(startDate);
        drugDosage.setEndDate(endDate);
        drugDosages.add(drugDosage);
        treatmentAdvice.setDrugDosages(drugDosages);
        return treatmentAdvice;
    }

    @Test
    public void shouldUnscheduleFourDayRecallJobs() {
        String patient_id = "patient_id";
        Patient patient = PatientBuilder.startRecording().withDefaults().withId(patient_id).withBestCallTime(new TimeOfDay(10, 0, TimeMeridiem.AM)).build();
        patient.getPatientPreferences().setCallPreference(CallPreference.FourDayRecall);
        when(properties.getProperty(TAMAConstants.FOUR_DAY_RECALL_DAYS_TO_RETRY)).thenReturn("3");
        schedulerService.unScheduleFourDayRecallJobs(patient);
        verify(motechSchedulerService).unscheduleRepeatingJob(TAMAConstants.FOUR_DAY_RECALL_SUBJECT, patient_id);
        for (int i = 0; i < 3; i++) {
            verify(motechSchedulerService).unscheduleJob(TAMAConstants.FOUR_DAY_RECALL_SUBJECT, i + patient_id);
        }
        verify(motechSchedulerService).unscheduleJob(TAMAConstants.WEEKLY_FALLING_TREND_SUBJECT, patient_id);
    }

    private void assertFourDayRecallCallJob(CronSchedulableJob cronSchedulableJob, String cronExpression) {
        assertCronSchedulableJob(cronSchedulableJob, cronExpression, TREATMENT_ADVICE_START_DATE.plusDays(WAIT_TIME_TO_START_FOURDAY_RECALL).toDate(), TREATMENT_ADVICE_END_DATE.toDate());
        assertEquals(TAMAConstants.FOUR_DAY_RECALL_SUBJECT, cronSchedulableJob.getMotechEvent().getSubject());
        assertEquals(PATIENT_ID, cronSchedulableJob.getMotechEvent().getParameters().get(FourDayRecallListener.PATIENT_DOC_ID_KEY));
        assertEquals(false, cronSchedulableJob.getMotechEvent().getParameters().get(FourDayRecallListener.RETRY_EVENT_KEY));
    }

    private void assertFourDayRecallCallJobWhenCallPreferenceChanged(CronSchedulableJob cronSchedulableJob, String cronExpression) {
        assertCronSchedulableJob(cronSchedulableJob, cronExpression, TREATMENT_ADVICE_START_DATE.plusDays(WAIT_TIME_TO_START_FOURDAY_RECALL + 3).toDate(), TREATMENT_ADVICE_END_DATE.toDate());
        assertEquals(TAMAConstants.FOUR_DAY_RECALL_SUBJECT, cronSchedulableJob.getMotechEvent().getSubject());
        assertEquals(PATIENT_ID, cronSchedulableJob.getMotechEvent().getParameters().get(FourDayRecallListener.PATIENT_DOC_ID_KEY));
        assertEquals(false, cronSchedulableJob.getMotechEvent().getParameters().get(FourDayRecallListener.RETRY_EVENT_KEY));
    }

    private void assertFallingAdherenceAlertJob(CronSchedulableJob cronSchedulableJob, String cronExpression, boolean isLastRetryJob, LocalDate jobStartDate) {
        
        assertCronJob(cronSchedulableJob, cronExpression, isLastRetryJob, jobStartDate);
    }

    private void assertFallingAdherenceAlertJobWhenCallPreferenceChanged(CronSchedulableJob cronSchedulableJob, String cronExpression, boolean isLastRetryJob, LocalDate jobStartDate) {

        assertCronJob(cronSchedulableJob, cronExpression, isLastRetryJob, jobStartDate);
    }

    private void assertCronJob(CronSchedulableJob cronSchedulableJob, String cronExpression, boolean isLastRetryJob, LocalDate jobStartDate) {
        assertCronSchedulableJob(cronSchedulableJob, cronExpression, jobStartDate.toDate(), TREATMENT_ADVICE_END_DATE.toDate());
        assertEquals(TAMAConstants.WEEKLY_FALLING_TREND_SUBJECT, cronSchedulableJob.getMotechEvent().getSubject());
        assertEquals(PATIENT_ID, cronSchedulableJob.getMotechEvent().getParameters().get(FourDayRecallListener.PATIENT_DOC_ID_KEY));
        if (isLastRetryJob) {
            assertEquals("true", cronSchedulableJob.getMotechEvent().getParameters().get(FourDayRecallListener.IS_LAST_RETRY_DAY));
        } else {
            assertEquals(false, cronSchedulableJob.getMotechEvent().getParameters().get(FourDayRecallListener.RETRY_EVENT_KEY));
        }
    }

    @Test
    public void shouldNotScheduleDailyAdherenceQualityJobsForAPatientOnWeeklyReminder() {
        Patient patient = PatientBuilder.startRecording().withCallPreference(CallPreference.FourDayRecall).build();
        schedulerService.scheduleJobForDeterminingAdherenceQualityInDailyPillReminder(patient, null);
        verify(motechSchedulerService, never()).scheduleJob(Matchers.<CronSchedulableJob>any());
    }

    @Test
    public void shouldScheduleDailyAdherenceQualityJobsForAPatientOnDailyReminderStartingFromTreatmentAdviceStartDate() {
        final String patientId = "123456";
        Patient patient = PatientBuilder.startRecording().withDefaults().withId(patientId).withCallPreference(CallPreference.DailyPillReminder).build();

        final LocalDate startDate = DateUtil.today().plusDays(1);
        final LocalDate endDate = startDate.plusDays(1);

        TreatmentAdvice advice = getTreatmentAdvice(startDate, endDate);
        schedulerService.scheduleJobForDeterminingAdherenceQualityInDailyPillReminder(patient, advice);

        final ArgumentCaptor<CronSchedulableJob> cronArgCaptor = ArgumentCaptor.forClass(CronSchedulableJob.class);
        verify(motechSchedulerService, times(1)).scheduleJob(cronArgCaptor.capture());

        CronSchedulableJob jobScheduledWithParams = cronArgCaptor.getValue();
        final MotechEvent motechEventInScheduledJob = jobScheduledWithParams.getMotechEvent();
        Map<String, Object> paramsInScheduledJob = motechEventInScheduledJob.getParameters();

        assertCronSchedulableJob(jobScheduledWithParams, "0 0 0 * * ?", startDate.toDate(), endDate.plusDays(1).toDate());

        assertEquals(paramsInScheduledJob.get(EventKeys.EXTERNAL_ID_KEY), patientId);
        assertEquals(motechEventInScheduledJob.getSubject(), TAMAConstants.DETERMINE_ADHERENCE_QUALITY_IN_DAILY_PILL_REMINDER);
    }

    @Test
    public void shouldScheduleDailyAdherenceQualityJobsForAPatientOnDailyReminderStartingFromTodayIfTreatmentAdviceStartDateIsInPast() {
        final String patientId = "123456";
        Patient patient = PatientBuilder.startRecording().withDefaults().withId(patientId).withCallPreference(CallPreference.DailyPillReminder).build();

        final LocalDate startDate = DateUtil.today().minusDays(2);
        final LocalDate endDate = DateUtil.today().plusDays(2);
        final DateTime timeFewMillisBack = DateUtil.now().minusMillis(1000);

        TreatmentAdvice advice = getTreatmentAdvice(startDate, endDate);
        schedulerService.scheduleJobForDeterminingAdherenceQualityInDailyPillReminder(patient, advice);

        final ArgumentCaptor<CronSchedulableJob> cronArgCaptor = ArgumentCaptor.forClass(CronSchedulableJob.class);
        verify(motechSchedulerService, times(1)).scheduleJob(cronArgCaptor.capture());

        CronSchedulableJob jobScheduledWithParams = cronArgCaptor.getValue();
        final MotechEvent motechEventInScheduledJob = jobScheduledWithParams.getMotechEvent();
        Map<String, Object> paramsInScheduledJob = motechEventInScheduledJob.getParameters();

        assertEquals("Should setup the cron expression to run at every midnight.", jobScheduledWithParams.getCronExpression(), "0 0 0 * * ?");

        DateTime actualTimeWhenTriggerWasActivated = DateUtil.newDateTime(jobScheduledWithParams.getStartTime());
        assertTrue("Since the advice has already started in past, we should schedule it starting now, which is after a time few milli seconds back.", timeFewMillisBack.isBefore(actualTimeWhenTriggerWasActivated));
        DateTime rightNow = DateUtil.now();
        assertTrue("And the time when it was scheduled should be a bit in past.", rightNow.isAfter(actualTimeWhenTriggerWasActivated));

        assertEquals(jobScheduledWithParams.getEndTime(), endDate.plusDays(1).toDate());

        assertEquals(paramsInScheduledJob.get(EventKeys.EXTERNAL_ID_KEY), patientId);
        assertEquals(motechEventInScheduledJob.getSubject(), TAMAConstants.DETERMINE_ADHERENCE_QUALITY_IN_DAILY_PILL_REMINDER);
    }

    @Test
    public void shouldUnscheduleFallingAdherenceAlertJobs() {
        String patient_id = "patient_id";
        int numDaysToRetry = 2;
        when(properties.getProperty(TAMAConstants.FOUR_DAY_RECALL_DAYS_TO_RETRY)).thenReturn(String.valueOf(numDaysToRetry));
        schedulerService.unscheduleFallingAdherenceAlertJobs(patient_id);
        verify(motechSchedulerService).unscheduleJob(TAMAConstants.ADHERENCE_WEEKLY_TREND_SCHEDULER_SUBJECT, 0 + patient_id);
        verify(motechSchedulerService).unscheduleJob(TAMAConstants.ADHERENCE_WEEKLY_TREND_SCHEDULER_SUBJECT, 1 + patient_id);
        verify(motechSchedulerService).unscheduleJob(TAMAConstants.ADHERENCE_WEEKLY_TREND_SCHEDULER_SUBJECT, 2 + patient_id);
    }

    @Test
    public void shouldUnScheduleJobForDeterminingAdherenceQualityInDailyPillReminder() {
        final String patientId = "123456";
        Patient patient = PatientBuilder.startRecording().withDefaults().withId(patientId).withCallPreference(CallPreference.DailyPillReminder).build();
        schedulerService.unscheduleJobForDeterminingAdherenceQualityInDailyPillReminder(patient);
        verify(motechSchedulerService).unscheduleJob(TAMAConstants.DETERMINE_ADHERENCE_QUALITY_IN_DAILY_PILL_REMINDER, patientId);
    }

}