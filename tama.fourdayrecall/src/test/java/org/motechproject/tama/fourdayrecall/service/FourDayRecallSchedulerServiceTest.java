package org.motechproject.tama.fourdayrecall.service;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.motechproject.model.CronSchedulableJob;
import org.motechproject.model.DayOfWeek;
import org.motechproject.model.RepeatingSchedulableJob;
import org.motechproject.scheduler.MotechSchedulerService;
import org.motechproject.tama.common.TAMAConstants;
import org.motechproject.tama.fourdayrecall.listener.FourDayRecallListener;
import org.motechproject.tama.patient.builder.PatientBuilder;
import org.motechproject.tama.patient.builder.TreatmentAdviceBuilder;
import org.motechproject.tama.patient.domain.*;
import org.motechproject.testing.utils.BaseUnitTest;
import org.motechproject.util.DateUtil;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.powermock.api.mockito.PowerMockito.when;

public class FourDayRecallSchedulerServiceTest extends BaseUnitTest {
    private static final String PATIENT_ID = "patient_id";

    @Mock
    MotechSchedulerService motechSchedulerService;
    @Mock
    FourDayRecallAdherenceService fourDayRecallAdherenceService;
    @Mock
    private Properties ivrProperties;
    @Mock
    private Properties fourDayRecallProperties;

    private FourDayRecallSchedulerService schedulerService;
    private TreatmentAdvice treatmentAdvice;
    private Patient patient;

    @Before
    public void setUp() {
        initMocks(this);
        when(fourDayRecallProperties.getProperty(TAMAConstants.FOUR_DAY_RECALL_DAYS_TO_RETRY)).thenReturn("2");

        schedulerService = new FourDayRecallSchedulerService(motechSchedulerService, new FourDayRecallDateService(), ivrProperties, fourDayRecallProperties);
    }

    @Test
    public void shouldScheduleJobs_WhenTreatmentWasStartedThisWeek_AndFirstRecallDateIsInTheCurrentWeek() {
        mockCurrentDate(DateUtil.newDateTime(new LocalDate(2012, 1, 11), 10, 0, 0));
        patient = PatientBuilder.startRecording().withId(PATIENT_ID).withWeeklyCallPreference(DayOfWeek.Friday, new TimeOfDay(10, 30, TimeMeridiem.AM)).build();
        treatmentAdvice = TreatmentAdviceBuilder.startRecording().withStartDate(new LocalDate(2012, 1, 8)).build();

        schedulerService.scheduleFourDayRecallJobs(patient, treatmentAdvice);
        ArgumentCaptor<CronSchedulableJob> cronSchedulableJobArgumentCaptor = ArgumentCaptor.forClass(CronSchedulableJob.class);
        verify(motechSchedulerService, times(6)).safeScheduleJob(cronSchedulableJobArgumentCaptor.capture());
        List<CronSchedulableJob> cronSchedulableJobList = cronSchedulableJobArgumentCaptor.getAllValues();

        LocalDate expectedFirstCallDate = new LocalDate(2012, 1, 13);
        assertFourDayRecallCallJob(cronSchedulableJobList.get(0), "0 30 10 ? * 6", expectedFirstCallDate.toDate());
        assertFourDayRecallCallJob(cronSchedulableJobList.get(1), "0 30 10 ? * 7", expectedFirstCallDate.plusDays(1).toDate());
        assertFourDayRecallCallJob(cronSchedulableJobList.get(2), "0 30 10 ? * 1", expectedFirstCallDate.plusDays(2).toDate());
        assertFallingAdherenceAlertJob(cronSchedulableJobList.get(3), "0 0 0 ? * 7", false, expectedFirstCallDate);
        assertFallingAdherenceAlertJob(cronSchedulableJobList.get(4), "0 0 0 ? * 1", false, expectedFirstCallDate.plusDays(1));
        assertFallingAdherenceAlertJob(cronSchedulableJobList.get(5), "0 0 0 ? * 2", true, expectedFirstCallDate.plusDays(2));
    }

    @Test
    public void shouldScheduleJobs_WhenTreatmentWasStartedThisWeek_AndFirstRecallDateIsInTheNextWeek() {
        mockCurrentDate(DateUtil.newDateTime(new LocalDate(2012, 1, 11), 10, 0, 0));
        patient = PatientBuilder.startRecording().withId(PATIENT_ID).withWeeklyCallPreference(DayOfWeek.Friday, new TimeOfDay(10, 30, TimeMeridiem.AM)).build();
        treatmentAdvice = TreatmentAdviceBuilder.startRecording().withStartDate(new LocalDate(2012, 1, 10)).build();

        schedulerService.scheduleFourDayRecallJobs(patient, treatmentAdvice);
        ArgumentCaptor<CronSchedulableJob> cronSchedulableJobArgumentCaptor = ArgumentCaptor.forClass(CronSchedulableJob.class);
        verify(motechSchedulerService, times(6)).safeScheduleJob(cronSchedulableJobArgumentCaptor.capture());
        List<CronSchedulableJob> cronSchedulableJobList = cronSchedulableJobArgumentCaptor.getAllValues();

        LocalDate expectedFirstCallDate = new LocalDate(2012, 1, 20);
        assertFourDayRecallCallJob(cronSchedulableJobList.get(0), "0 30 10 ? * 6", expectedFirstCallDate.toDate());
        assertFourDayRecallCallJob(cronSchedulableJobList.get(1), "0 30 10 ? * 7", expectedFirstCallDate.plusDays(1).toDate());
        assertFourDayRecallCallJob(cronSchedulableJobList.get(2), "0 30 10 ? * 1", expectedFirstCallDate.plusDays(2).toDate());
        assertFallingAdherenceAlertJob(cronSchedulableJobList.get(3), "0 0 0 ? * 7", false, expectedFirstCallDate);
        assertFallingAdherenceAlertJob(cronSchedulableJobList.get(4), "0 0 0 ? * 1", false, expectedFirstCallDate.plusDays(1));
        assertFallingAdherenceAlertJob(cronSchedulableJobList.get(5), "0 0 0 ? * 2", true, expectedFirstCallDate.plusDays(2));
    }

    @Test
    public void shouldScheduleJobs_WhenTreatmentStartsNextWeek_AndFirstRecallDateIsInTheSameWeek() {
        mockCurrentDate(DateUtil.newDateTime(new LocalDate(2012, 1, 11), 10, 0, 0));
        patient = PatientBuilder.startRecording().withId(PATIENT_ID).withWeeklyCallPreference(DayOfWeek.Friday, new TimeOfDay(10, 30, TimeMeridiem.AM)).build();
        treatmentAdvice = TreatmentAdviceBuilder.startRecording().withStartDate(new LocalDate(2012, 1, 15)).build();

        schedulerService.scheduleFourDayRecallJobs(patient, treatmentAdvice);
        ArgumentCaptor<CronSchedulableJob> cronSchedulableJobArgumentCaptor = ArgumentCaptor.forClass(CronSchedulableJob.class);
        verify(motechSchedulerService, times(6)).safeScheduleJob(cronSchedulableJobArgumentCaptor.capture());
        List<CronSchedulableJob> cronSchedulableJobList = cronSchedulableJobArgumentCaptor.getAllValues();

        LocalDate expectedFirstCallDate = new LocalDate(2012, 1, 20);
        assertFourDayRecallCallJob(cronSchedulableJobList.get(0), "0 30 10 ? * 6", expectedFirstCallDate.toDate());
        assertFourDayRecallCallJob(cronSchedulableJobList.get(1), "0 30 10 ? * 7", expectedFirstCallDate.plusDays(1).toDate());
        assertFourDayRecallCallJob(cronSchedulableJobList.get(2), "0 30 10 ? * 1", expectedFirstCallDate.plusDays(2).toDate());
        assertFallingAdherenceAlertJob(cronSchedulableJobList.get(3), "0 0 0 ? * 7", false, expectedFirstCallDate);
        assertFallingAdherenceAlertJob(cronSchedulableJobList.get(4), "0 0 0 ? * 1", false, expectedFirstCallDate.plusDays(1));
        assertFallingAdherenceAlertJob(cronSchedulableJobList.get(5), "0 0 0 ? * 2", true, expectedFirstCallDate.plusDays(2));
    }

    @Test
    public void shouldScheduleJobs_WhenTreatmentStartsNextWeek_AndFirstRecallDateIsInTheFollowingWeek() {
        mockCurrentDate(DateUtil.newDateTime(new LocalDate(2012, 1, 11), 10, 0, 0));
        patient = PatientBuilder.startRecording().withId(PATIENT_ID).withWeeklyCallPreference(DayOfWeek.Friday, new TimeOfDay(10, 30, TimeMeridiem.AM)).build();
        treatmentAdvice = TreatmentAdviceBuilder.startRecording().withStartDate(new LocalDate(2012, 1, 18)).build();

        schedulerService.scheduleFourDayRecallJobs(patient, treatmentAdvice);
        ArgumentCaptor<CronSchedulableJob> cronSchedulableJobArgumentCaptor = ArgumentCaptor.forClass(CronSchedulableJob.class);
        verify(motechSchedulerService, times(6)).safeScheduleJob(cronSchedulableJobArgumentCaptor.capture());
        List<CronSchedulableJob> cronSchedulableJobList = cronSchedulableJobArgumentCaptor.getAllValues();

        LocalDate expectedFirstCallDate = new LocalDate(2012, 1, 27);
        assertFourDayRecallCallJob(cronSchedulableJobList.get(0), "0 30 10 ? * 6", expectedFirstCallDate.toDate());
        assertFourDayRecallCallJob(cronSchedulableJobList.get(1), "0 30 10 ? * 7", expectedFirstCallDate.plusDays(1).toDate());
        assertFourDayRecallCallJob(cronSchedulableJobList.get(2), "0 30 10 ? * 1", expectedFirstCallDate.plusDays(2).toDate());
        assertFallingAdherenceAlertJob(cronSchedulableJobList.get(3), "0 0 0 ? * 7", false, expectedFirstCallDate);
        assertFallingAdherenceAlertJob(cronSchedulableJobList.get(4), "0 0 0 ? * 1", false, expectedFirstCallDate.plusDays(1));
        assertFallingAdherenceAlertJob(cronSchedulableJobList.get(5), "0 0 0 ? * 2", true, expectedFirstCallDate.plusDays(2));
    }

    @Test
    public void shouldScheduleJobs_WhenTreatmentStartedPreviousWeek_AndTheNextRecallDateIsInTheCurrentWeek() {
        mockCurrentDate(DateUtil.newDateTime(new LocalDate(2012, 1, 11), 10, 0, 0));
        patient = PatientBuilder.startRecording().withId(PATIENT_ID).withWeeklyCallPreference(DayOfWeek.Friday, new TimeOfDay(10, 30, TimeMeridiem.AM)).build();
        treatmentAdvice = TreatmentAdviceBuilder.startRecording().withStartDate(new LocalDate(2012, 1, 1)).build();

        schedulerService.scheduleFourDayRecallJobs(patient, treatmentAdvice);
        ArgumentCaptor<CronSchedulableJob> cronSchedulableJobArgumentCaptor = ArgumentCaptor.forClass(CronSchedulableJob.class);
        verify(motechSchedulerService, times(6)).safeScheduleJob(cronSchedulableJobArgumentCaptor.capture());
        List<CronSchedulableJob> cronSchedulableJobList = cronSchedulableJobArgumentCaptor.getAllValues();

        LocalDate expectedFirstCallDate = new LocalDate(2012, 1, 13);
        assertFourDayRecallCallJob(cronSchedulableJobList.get(0), "0 30 10 ? * 6", expectedFirstCallDate.toDate());
        assertFourDayRecallCallJob(cronSchedulableJobList.get(1), "0 30 10 ? * 7", expectedFirstCallDate.plusDays(1).toDate());
        assertFourDayRecallCallJob(cronSchedulableJobList.get(2), "0 30 10 ? * 1", expectedFirstCallDate.plusDays(2).toDate());
        assertFallingAdherenceAlertJob(cronSchedulableJobList.get(3), "0 0 0 ? * 7", false, expectedFirstCallDate);
        assertFallingAdherenceAlertJob(cronSchedulableJobList.get(4), "0 0 0 ? * 1", false, expectedFirstCallDate.plusDays(1));
        assertFallingAdherenceAlertJob(cronSchedulableJobList.get(5), "0 0 0 ? * 2", true, expectedFirstCallDate.plusDays(2));
    }

    @Test
    public void shouldScheduleJobs_WhenTreatmentStartedPreviousWeek_AndTheNextRecallDateIsInTheNextWeek() {
        mockCurrentDate(DateUtil.newDateTime(new LocalDate(2012, 1, 11), 10, 0, 0));
        patient = PatientBuilder.startRecording().withId(PATIENT_ID).withWeeklyCallPreference(DayOfWeek.Monday, new TimeOfDay(10, 30, TimeMeridiem.AM)).build();
        treatmentAdvice = TreatmentAdviceBuilder.startRecording().withStartDate(new LocalDate(2012, 1, 1)).build();

        schedulerService.scheduleFourDayRecallJobs(patient, treatmentAdvice);
        ArgumentCaptor<CronSchedulableJob> cronSchedulableJobArgumentCaptor = ArgumentCaptor.forClass(CronSchedulableJob.class);
        verify(motechSchedulerService, times(6)).safeScheduleJob(cronSchedulableJobArgumentCaptor.capture());
        List<CronSchedulableJob> cronSchedulableJobList = cronSchedulableJobArgumentCaptor.getAllValues();

        LocalDate expectedFirstCallDate = new LocalDate(2012, 1, 16);
        assertFourDayRecallCallJob(cronSchedulableJobList.get(0), "0 30 10 ? * 2", expectedFirstCallDate.toDate());
        assertFourDayRecallCallJob(cronSchedulableJobList.get(1), "0 30 10 ? * 3", expectedFirstCallDate.plusDays(1).toDate());
        assertFourDayRecallCallJob(cronSchedulableJobList.get(2), "0 30 10 ? * 4", expectedFirstCallDate.plusDays(2).toDate());
        assertFallingAdherenceAlertJob(cronSchedulableJobList.get(3), "0 0 0 ? * 3", false, expectedFirstCallDate);
        assertFallingAdherenceAlertJob(cronSchedulableJobList.get(4), "0 0 0 ? * 4", false, expectedFirstCallDate.plusDays(1));
        assertFallingAdherenceAlertJob(cronSchedulableJobList.get(5), "0 0 0 ? * 5", true, expectedFirstCallDate.plusDays(2));
    }

    @Test
    public void shouldScheduleJobs_WhenPatientTransitionedInTheCurrentWeek_AndTheNextRecallDateIsInTheSameWeek() {
        mockCurrentDate(DateUtil.newDateTime(new LocalDate(2012, 1, 4), 10, 0, 0));
        patient = PatientBuilder.startRecording().withId(PATIENT_ID).withWeeklyCallPreference(DayOfWeek.Saturday, new TimeOfDay(10, 30, TimeMeridiem.AM)).withTransitionDate(new LocalDate(2012, 1, 3)).build();
        treatmentAdvice = TreatmentAdviceBuilder.startRecording().withStartDate(new LocalDate(2011, 12, 5)).build();

        schedulerService.scheduleFourDayRecallJobs(patient, treatmentAdvice);
        ArgumentCaptor<CronSchedulableJob> cronSchedulableJobArgumentCaptor = ArgumentCaptor.forClass(CronSchedulableJob.class);
        verify(motechSchedulerService, times(6)).safeScheduleJob(cronSchedulableJobArgumentCaptor.capture());
        List<CronSchedulableJob> cronSchedulableJobList = cronSchedulableJobArgumentCaptor.getAllValues();

        LocalDate expectedFirstCallDate = new LocalDate(2012, 1, 7);
        assertFourDayRecallCallJob(cronSchedulableJobList.get(0), "0 30 10 ? * 7", expectedFirstCallDate.toDate());
        assertFourDayRecallCallJob(cronSchedulableJobList.get(1), "0 30 10 ? * 1", expectedFirstCallDate.plusDays(1).toDate());
        assertFourDayRecallCallJob(cronSchedulableJobList.get(2), "0 30 10 ? * 2", expectedFirstCallDate.plusDays(2).toDate());
        assertFallingAdherenceAlertJob(cronSchedulableJobList.get(3), "0 0 0 ? * 1", false, expectedFirstCallDate);
        assertFallingAdherenceAlertJob(cronSchedulableJobList.get(4), "0 0 0 ? * 2", false, expectedFirstCallDate.plusDays(1));
        assertFallingAdherenceAlertJob(cronSchedulableJobList.get(5), "0 0 0 ? * 3", true, expectedFirstCallDate.plusDays(2));
    }

    @Test
    public void shouldScheduleJobs_WhenPatientTransitionedInTheCurrentWeek_AndTheNextRecallDateIsInTheNextWeek() {
        mockCurrentDate(DateUtil.newDateTime(new LocalDate(2012, 1, 4), 10, 0, 0));
        patient = PatientBuilder.startRecording().withId(PATIENT_ID).withWeeklyCallPreference(DayOfWeek.Tuesday, new TimeOfDay(10, 30, TimeMeridiem.AM)).withTransitionDate(new LocalDate(2012, 1, 3)).build();
        treatmentAdvice = TreatmentAdviceBuilder.startRecording().withStartDate(new LocalDate(2011, 12, 5)).build();

        schedulerService.scheduleFourDayRecallJobs(patient, treatmentAdvice);
        ArgumentCaptor<CronSchedulableJob> cronSchedulableJobArgumentCaptor = ArgumentCaptor.forClass(CronSchedulableJob.class);
        verify(motechSchedulerService, times(6)).safeScheduleJob(cronSchedulableJobArgumentCaptor.capture());
        List<CronSchedulableJob> cronSchedulableJobList = cronSchedulableJobArgumentCaptor.getAllValues();

        LocalDate expectedFirstCallDate = new LocalDate(2012, 1, 10);
        assertFourDayRecallCallJob(cronSchedulableJobList.get(0), "0 30 10 ? * 3", expectedFirstCallDate.toDate());
        assertFourDayRecallCallJob(cronSchedulableJobList.get(1), "0 30 10 ? * 4", expectedFirstCallDate.plusDays(1).toDate());
        assertFourDayRecallCallJob(cronSchedulableJobList.get(2), "0 30 10 ? * 5", expectedFirstCallDate.plusDays(2).toDate());
        assertFallingAdherenceAlertJob(cronSchedulableJobList.get(3), "0 0 0 ? * 4", false, expectedFirstCallDate);
        assertFallingAdherenceAlertJob(cronSchedulableJobList.get(4), "0 0 0 ? * 5", false, expectedFirstCallDate.plusDays(1));
        assertFallingAdherenceAlertJob(cronSchedulableJobList.get(5), "0 0 0 ? * 6", true, expectedFirstCallDate.plusDays(2));
    }

    @Test
    public void shouldScheduleJobs_WhenPatientTransitionedInThePreviousWeek_AndTheNextRecallDateIsInTheNextWeek() {
        mockCurrentDate(DateUtil.newDateTime(new LocalDate(2012, 1, 11), 10, 0, 0));
        patient = PatientBuilder.startRecording().withId(PATIENT_ID).withWeeklyCallPreference(DayOfWeek.Tuesday, new TimeOfDay(10, 30, TimeMeridiem.AM)).withTransitionDate(new LocalDate(2012, 1, 3)).build();
        treatmentAdvice = TreatmentAdviceBuilder.startRecording().withStartDate(new LocalDate(2011, 12, 5)).build();

        schedulerService.scheduleFourDayRecallJobs(patient, treatmentAdvice);
        ArgumentCaptor<CronSchedulableJob> cronSchedulableJobArgumentCaptor = ArgumentCaptor.forClass(CronSchedulableJob.class);
        verify(motechSchedulerService, times(6)).safeScheduleJob(cronSchedulableJobArgumentCaptor.capture());
        List<CronSchedulableJob> cronSchedulableJobList = cronSchedulableJobArgumentCaptor.getAllValues();

        LocalDate expectedFirstCallDate = new LocalDate(2012, 1, 17);
        assertFourDayRecallCallJob(cronSchedulableJobList.get(0), "0 30 10 ? * 3", expectedFirstCallDate.toDate());
        assertFourDayRecallCallJob(cronSchedulableJobList.get(1), "0 30 10 ? * 4", expectedFirstCallDate.plusDays(1).toDate());
        assertFourDayRecallCallJob(cronSchedulableJobList.get(2), "0 30 10 ? * 5", expectedFirstCallDate.plusDays(2).toDate());
        assertFallingAdherenceAlertJob(cronSchedulableJobList.get(3), "0 0 0 ? * 4", false, expectedFirstCallDate);
        assertFallingAdherenceAlertJob(cronSchedulableJobList.get(4), "0 0 0 ? * 5", false, expectedFirstCallDate.plusDays(1));
        assertFallingAdherenceAlertJob(cronSchedulableJobList.get(5), "0 0 0 ? * 6", true, expectedFirstCallDate.plusDays(2));
    }

    @Test
    public void shouldScheduleRetryJobsForFourDayRecall() {
        patient = PatientBuilder.startRecording().withDefaults().withId(PATIENT_ID).withBestCallTime(new TimeOfDay(10, 30, TimeMeridiem.AM)).build();
        treatmentAdvice = TreatmentAdviceBuilder.startRecording().withStartDate(DateUtil.newDate(2011, 12, 12)).build();
        when(ivrProperties.getProperty(TAMAConstants.RETRIES_PER_DAY)).thenReturn("5");
        when(ivrProperties.getProperty(TAMAConstants.RETRY_INTERVAL)).thenReturn("15");

        schedulerService.scheduleRetryJobsForFourDayRecall(patient);

        ArgumentCaptor<RepeatingSchedulableJob> repeatingSchedulableJobArgumentCaptor = ArgumentCaptor.forClass(RepeatingSchedulableJob.class);
        verify(motechSchedulerService).safeScheduleRepeatingJob(repeatingSchedulableJobArgumentCaptor.capture());
        RepeatingSchedulableJob repeatingSchedulableJob = repeatingSchedulableJobArgumentCaptor.getValue();

        // 4 because repeatingSchedulableJobs are intern scheduled 1 + repeatCount number of times
        assertEquals(new Integer(4), repeatingSchedulableJob.getRepeatCount());
        assertEquals(15 * 60 * 1000, repeatingSchedulableJob.getRepeatInterval());
        assertDates(DateUtil.newDateTime(DateUtil.today(), 10, 45, 0), DateUtil.newDateTime(repeatingSchedulableJob.getStartTime()));
        assertDates(DateUtil.newDateTime(DateUtil.today(), 10, 45, 0).plusDays(1), DateUtil.newDateTime(repeatingSchedulableJob.getEndTime()));
        assertEquals(PATIENT_ID, repeatingSchedulableJob.getMotechEvent().getParameters().get(FourDayRecallListener.PATIENT_DOC_ID_KEY));
        assertEquals(true, repeatingSchedulableJob.getMotechEvent().getParameters().get(FourDayRecallListener.RETRY_EVENT_KEY));
    }

    @Test
    public void shouldUnscheduleFourDayRecallJobs() {
        treatmentAdvice = TreatmentAdviceBuilder.startRecording().withStartDate(DateUtil.newDate(2011, 12, 12)).build();
        patient = PatientBuilder.startRecording().withDefaults().withId(PATIENT_ID).withBestCallTime(new TimeOfDay(10, 0, TimeMeridiem.AM)).build();
        when(fourDayRecallProperties.getProperty(TAMAConstants.FOUR_DAY_RECALL_DAYS_TO_RETRY)).thenReturn("3");

        schedulerService.unscheduleFourDayRecallJobs(patient);

        verify(motechSchedulerService).safeUnscheduleRepeatingJob(TAMAConstants.FOUR_DAY_RECALL_SUBJECT, PATIENT_ID);
        for (int i = 0; i < 3; i++) {
            verify(motechSchedulerService).safeUnscheduleJob(TAMAConstants.FOUR_DAY_RECALL_SUBJECT, i + PATIENT_ID);
        }
        for (int i = 0; i <= 2; i++) {
            verify(motechSchedulerService).safeUnscheduleJob(TAMAConstants.WEEKLY_FALLING_TREND_AND_ADHERENCE_IN_RED_ALERT_SUBJECT, i + PATIENT_ID);
        }
    }

    @Test
    public void shouldCreateJobWithFirstCallParameterTrueWhenSchedulingTheFirstCallForTheWeek() {
        patient = PatientBuilder.startRecording().withDefaults().withId(PATIENT_ID).withWeeklyCallPreference(DayOfWeek.Friday, new TimeOfDay(10, 30, TimeMeridiem.AM)).build();
        treatmentAdvice = TreatmentAdviceBuilder.startRecording().withStartDate(DateUtil.newDate(2011, 12, 12)).build(); //Monday

        schedulerService.scheduleFourDayRecallJobs(patient, treatmentAdvice);

        ArgumentCaptor<CronSchedulableJob> cronJobForFourDayRecallArgumentCaptor = ArgumentCaptor.forClass(CronSchedulableJob.class);
        verify(motechSchedulerService, times(6)).safeScheduleJob(cronJobForFourDayRecallArgumentCaptor.capture());
        CronSchedulableJob cronSchedulableJob = cronJobForFourDayRecallArgumentCaptor.getAllValues().get(0);

        assertTrue((Boolean) cronSchedulableJob.getMotechEvent().getParameters().get(FourDayRecallListener.FIRST_CALL));
    }

    @Test
    public void shouldCreateJobWithFirstCallParameterFalseWhenSchedulingTheRepeatWeeklyJobs() {
        patient = PatientBuilder.startRecording().withDefaults().withId(PATIENT_ID).withWeeklyCallPreference(DayOfWeek.Friday, new TimeOfDay(10, 30, TimeMeridiem.AM)).build();
        treatmentAdvice = TreatmentAdviceBuilder.startRecording().withStartDate(DateUtil.newDate(2011, 12, 12)).build(); //Monday

        schedulerService.scheduleFourDayRecallJobs(patient, treatmentAdvice);

        ArgumentCaptor<CronSchedulableJob> cronJobForFourDayRecallArgumentCaptor = ArgumentCaptor.forClass(CronSchedulableJob.class);
        verify(motechSchedulerService, times(6)).safeScheduleJob(cronJobForFourDayRecallArgumentCaptor.capture());
        List<CronSchedulableJob> allValues = cronJobForFourDayRecallArgumentCaptor.getAllValues();
        System.out.println(Arrays.toString(allValues.toArray()));
        CronSchedulableJob cronSchedulableRepeatJob_1 = allValues.get(1);
        CronSchedulableJob cronSchedulableRepeatJob_2 = allValues.get(2);

        assertFalse((Boolean) cronSchedulableRepeatJob_1.getMotechEvent().getParameters().get(FourDayRecallListener.FIRST_CALL));
        assertFalse((Boolean) cronSchedulableRepeatJob_2.getMotechEvent().getParameters().get(FourDayRecallListener.FIRST_CALL));
    }

    private void assertDates(DateTime dateTime1, DateTime dateTime2) {
        String pattern = "yyyy-MM-dd HH:mm";
        assertEquals(dateTime1.toString(pattern), dateTime2.toString(pattern));
    }

    private void assertFourDayRecallCallJob(CronSchedulableJob cronSchedulableJob, String cronExpression, Date jobStartDate) {
        assertCronSchedulableJob(cronSchedulableJob, cronExpression, jobStartDate);

        assertEquals(TAMAConstants.FOUR_DAY_RECALL_SUBJECT, cronSchedulableJob.getMotechEvent().getSubject());
        assertEquals(PATIENT_ID, cronSchedulableJob.getMotechEvent().getParameters().get(FourDayRecallListener.PATIENT_DOC_ID_KEY));
        assertEquals(false, cronSchedulableJob.getMotechEvent().getParameters().get(FourDayRecallListener.RETRY_EVENT_KEY));
    }

    private void assertFallingAdherenceAlertJob(CronSchedulableJob cronSchedulableJob, String cronExpression, boolean isLastRetryJob, LocalDate jobStartDate) {
        assertCronSchedulableJob(cronSchedulableJob, cronExpression, jobStartDate.toDate());

        assertEquals(TAMAConstants.WEEKLY_FALLING_TREND_AND_ADHERENCE_IN_RED_ALERT_SUBJECT, cronSchedulableJob.getMotechEvent().getSubject());
        assertEquals(PATIENT_ID, cronSchedulableJob.getMotechEvent().getParameters().get(FourDayRecallListener.PATIENT_DOC_ID_KEY));

        if (isLastRetryJob) {
            assertEquals("true", cronSchedulableJob.getMotechEvent().getParameters().get(FourDayRecallListener.IS_LAST_RETRY_DAY));
        } else {
            assertEquals(false, cronSchedulableJob.getMotechEvent().getParameters().get(FourDayRecallListener.RETRY_EVENT_KEY));
        }
    }

    private void assertCronSchedulableJob(CronSchedulableJob cronSchedulableJob, String cronExpression, Date startTime) {
        assertEquals(cronExpression, cronSchedulableJob.getCronExpression());
        assertEquals(startTime, cronSchedulableJob.getStartTime());
    }

}
