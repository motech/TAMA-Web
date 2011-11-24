package org.motechproject.tama.platform.service;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.motechproject.model.CronSchedulableJob;
import org.motechproject.model.DayOfWeek;
import org.motechproject.model.RepeatingSchedulableJob;
import org.motechproject.scheduler.MotechSchedulerService;
import org.motechproject.tama.TAMAConstants;
import org.motechproject.tama.builder.PatientBuilder;
import org.motechproject.tama.domain.*;
import org.motechproject.tama.listener.FourDayRecallListener;
import org.motechproject.tama.repository.AllPatients;
import org.motechproject.util.DateUtil;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.powermock.api.mockito.PowerMockito.when;

public class TamaSchedulerServiceTest {
    private final LocalDate TREATMENT_ADVICE_START_DATE = DateUtil.newDate(2012, 12, 12);
    private final LocalDate TREATMENT_ADVICE_END_DATE = DateUtil.newDate(2012, 12, 24);
    private static final String PATIENT_ID = "patient_id";

    private TamaSchedulerService schedulerService;
    private TreatmentAdvice treatmentAdvice;
    private Patient patient;

    @Mock
    MotechSchedulerService motechSchedulerService;
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
        schedulerService = new TamaSchedulerService(motechSchedulerService, properties, allPatients);
    }

    @Test
    public void shouldScheduleFourDayRecallJobs_AndFallingAdherenceAlertJobs_StartDateIsToday() {
        DayOfWeek dayOfWeek = DayOfWeek.Friday;
        int numDaysToRetry = 2;
        patient.getPatientPreferences().setDayOfWeeklyCall(dayOfWeek);

        when(properties.getProperty(TAMAConstants.FOUR_DAY_RECALL_DAYS_TO_RETRY)).thenReturn(String.valueOf(numDaysToRetry));

        schedulerService.scheduleJobsForFourDayRecall(patient, treatmentAdvice);

        ArgumentCaptor<CronSchedulableJob> cronSchedulableJobArgumentCaptor = ArgumentCaptor.forClass(CronSchedulableJob.class);
        verify(motechSchedulerService, times(2 * (1 + numDaysToRetry))).scheduleJob(cronSchedulableJobArgumentCaptor.capture());
        List<CronSchedulableJob> cronSchedulableJobList = cronSchedulableJobArgumentCaptor.getAllValues();

        assertFourDayRecallCallJob(cronSchedulableJobList.get(0), "0 30 10 ? * 6");
        assertFourDayRecallCallJob(cronSchedulableJobList.get(1), "0 30 10 ? * 7");
        assertFourDayRecallCallJob(cronSchedulableJobList.get(2), "0 30 10 ? * 1");

        assertFallingAdherenceAlertJob(cronSchedulableJobList.get(3), "0 0 0 ? * 7", false);
        assertFallingAdherenceAlertJob(cronSchedulableJobList.get(4), "0 0 0 ? * 1", false);
        assertFallingAdherenceAlertJob(cronSchedulableJobList.get(5), "0 0 0 ? * 2", true);

    }

    @Test
    public void shouldScheduleFourDayRecallJobsStartingNow_StartDateIsBeforeToday() {
        DayOfWeek dayOfWeek = DayOfWeek.Friday;
        DateTime now = DateUtil.now();
        LocalDate today = now.toLocalDate();
        final LocalDate treatmentAdviceStartDate = today.minusDays(4);

        int numDaysToRetry = 2;
        patient.getPatientPreferences().setDayOfWeeklyCall(dayOfWeek);
        treatmentAdvice.getDrugDosages().get(0).setStartDate(treatmentAdviceStartDate);
        when(properties.getProperty(TAMAConstants.FOUR_DAY_RECALL_DAYS_TO_RETRY)).thenReturn(String.valueOf(numDaysToRetry));
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

        schedulerService.scheduleFallingAdherenceAlertJobs(patient, treatmentAdvice);

        ArgumentCaptor<CronSchedulableJob> cronSchedulableJobArgumentCaptor = ArgumentCaptor.forClass(CronSchedulableJob.class);
        verify(motechSchedulerService, times(3)).scheduleJob(cronSchedulableJobArgumentCaptor.capture());
        List<CronSchedulableJob> cronSchedulableJobList = cronSchedulableJobArgumentCaptor.getAllValues();
        assertFallingAdherenceAlertJob(cronSchedulableJobList.get(0), "0 0 0 ? * 7", false);
        assertFallingAdherenceAlertJob(cronSchedulableJobList.get(1), "0 0 0 ? * 1", false);
        assertFallingAdherenceAlertJob(cronSchedulableJobList.get(2), "0 0 0 ? * 2", true);
    }

    private void assertCronSchedulableJob(CronSchedulableJob cronSchedulableJob, String cronExpression, Date startTime, Date endTime) {
        assertEquals(cronExpression, cronSchedulableJob.getCronExpression());
        assertEquals(startTime, cronSchedulableJob.getStartTime());
        assertEquals(endTime, cronSchedulableJob.getEndTime());
        assertEquals(PATIENT_ID, cronSchedulableJob.getMotechEvent().getParameters().get(FourDayRecallListener.PATIENT_DOC_ID_KEY));
        assertEquals(false, cronSchedulableJob.getMotechEvent().getParameters().get(FourDayRecallListener.RETRY_EVENT_KEY));
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
        schedulerService.scheduleJobForAdherenceTrendFeedback(treatmentAdvice);

        ArgumentCaptor<CronSchedulableJob> jobCaptor = ArgumentCaptor.forClass(CronSchedulableJob.class);
        verify(motechSchedulerService).scheduleJob(jobCaptor.capture());
        Assert.assertEquals("0 0 0 ? * 4", jobCaptor.getValue().getCronExpression());
    }

    @Test
    public void shouldScheduleWeeklyAdherenceTrendJob_StartDateIsBeforeToday() {
        DateTime now = DateUtil.now();
        LocalDate today = now.toLocalDate();
        treatmentAdvice.getDrugDosages().get(0).setStartDate(today.minusMonths(2));

        schedulerService.scheduleJobForAdherenceTrendFeedback(treatmentAdvice);
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
        TreatmentAdvice treatmentAdvice = TreatmentAdvice.newDefault();
        String TREATMENT_ADVICE_ID = "treatmentAdviceId";
        treatmentAdvice.setId(TREATMENT_ADVICE_ID);
        treatmentAdvice.setPatientId(PATIENT_ID);
        ArrayList<DrugDosage> drugDosages = new ArrayList<DrugDosage>();
        DrugDosage drugDosage = new DrugDosage();
        treatmentAdvice.setDrugCompositionGroupId("");
        drugDosage.setStartDate(TREATMENT_ADVICE_START_DATE);
        drugDosage.setEndDate(TREATMENT_ADVICE_END_DATE);
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
        assertCronSchedulableJob(cronSchedulableJob, cronExpression, TREATMENT_ADVICE_START_DATE.plusDays(4).toDate(), TREATMENT_ADVICE_END_DATE.toDate());
        assertEquals(TAMAConstants.FOUR_DAY_RECALL_SUBJECT, cronSchedulableJob.getMotechEvent().getSubject());
        assertEquals(PATIENT_ID, cronSchedulableJob.getMotechEvent().getParameters().get(FourDayRecallListener.PATIENT_DOC_ID_KEY));
    }

    private void assertFallingAdherenceAlertJob(CronSchedulableJob cronSchedulableJob, String cronExpression, boolean isLastRetryJob) {
        assertCronSchedulableJob(cronSchedulableJob, cronExpression, TREATMENT_ADVICE_START_DATE.plusDays(4 + 14).toDate(), TREATMENT_ADVICE_END_DATE.toDate());
        assertEquals(TAMAConstants.WEEKLY_FALLING_TREND_SUBJECT, cronSchedulableJob.getMotechEvent().getSubject());
        assertEquals(PATIENT_ID, cronSchedulableJob.getMotechEvent().getParameters().get(FourDayRecallListener.PATIENT_DOC_ID_KEY));
        if (isLastRetryJob) assertEquals("true", cronSchedulableJob.getMotechEvent().getParameters().get(FourDayRecallListener.IS_LAST_RETRY_DAY));
    }
}