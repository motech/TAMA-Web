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
    public void shouldScheduleFourDayRecallJobs_StartDateIsToday() {
        DayOfWeek dayOfWeek = DayOfWeek.Friday;
        int numDaysToRetry = 2;
        patient.getPatientPreferences().setDayOfWeeklyCall(dayOfWeek);

        when(properties.getProperty(TAMAConstants.FOUR_DAY_RECALL_DAYS_TO_RETRY)).thenReturn(String.valueOf(numDaysToRetry));

        schedulerService.scheduleJobsForFourDayRecall(patient, treatmentAdvice);

        ArgumentCaptor<CronSchedulableJob> cronSchedulableJobArgumentCaptor = ArgumentCaptor.forClass(CronSchedulableJob.class);
        verify(motechSchedulerService, times(numDaysToRetry + 1)).scheduleJob(cronSchedulableJobArgumentCaptor.capture());
        List<CronSchedulableJob> cronSchedulableJobList = cronSchedulableJobArgumentCaptor.getAllValues();

        assertCronSchedulableJob(cronSchedulableJobList.get(0), "0 30 10 ? * 6", TREATMENT_ADVICE_START_DATE.plusDays(4).toDate(), TREATMENT_ADVICE_END_DATE.toDate());
        assertCronSchedulableJob(cronSchedulableJobList.get(1), "0 30 10 ? * 7", TREATMENT_ADVICE_START_DATE.plusDays(5).toDate(), TREATMENT_ADVICE_END_DATE.toDate());
        assertCronSchedulableJob(cronSchedulableJobList.get(2), "0 30 10 ? * 1", TREATMENT_ADVICE_START_DATE.plusDays(6).toDate(), TREATMENT_ADVICE_END_DATE.toDate());
    }

    @Test
    public void shouldScheduleFourDayRecallJobs_StartDateIsBeforeToday() {
        DayOfWeek dayOfWeek = DayOfWeek.Friday;
        DateTime now = DateUtil.now();
        LocalDate today = now.toLocalDate();

        int numDaysToRetry = 2;
        patient.getPatientPreferences().setDayOfWeeklyCall(dayOfWeek);
        treatmentAdvice.getDrugDosages().get(0).setStartDate(today.minusDays(4));
        when(properties.getProperty(TAMAConstants.FOUR_DAY_RECALL_DAYS_TO_RETRY)).thenReturn(String.valueOf(numDaysToRetry));
        schedulerService.scheduleJobsForFourDayRecall(patient, treatmentAdvice);
        ArgumentCaptor<CronSchedulableJob> cronSchedulableJobArgumentCaptor = ArgumentCaptor.forClass(CronSchedulableJob.class);
        verify(motechSchedulerService, times(numDaysToRetry + 1)).scheduleJob(cronSchedulableJobArgumentCaptor.capture());
        List<CronSchedulableJob> cronSchedulableJobList = cronSchedulableJobArgumentCaptor.getAllValues();

        assertTrue(now.minusMinutes(1).isBefore(new DateTime(cronSchedulableJobList.get(0).getStartTime())));
    }

    @Test
    public void shouldScheduleFallingAdherenceTrendAlertJob() {
        DayOfWeek dayOfWeek = DayOfWeek.Friday;
        int numDaysToRetry = 2;
        patient.getPatientPreferences().setDayOfWeeklyCall(dayOfWeek);

        when(properties.getProperty(TAMAConstants.FOUR_DAY_RECALL_DAYS_TO_RETRY)).thenReturn(String.valueOf(numDaysToRetry));

        schedulerService.scheduleFallingAdherenceAlertJob(patient, treatmentAdvice);

        ArgumentCaptor<CronSchedulableJob> cronSchedulableJobArgumentCaptor = ArgumentCaptor.forClass(CronSchedulableJob.class);
        verify(motechSchedulerService, times(1)).scheduleJob(cronSchedulableJobArgumentCaptor.capture());
        List<CronSchedulableJob> cronSchedulableJobList = cronSchedulableJobArgumentCaptor.getAllValues();
        CronSchedulableJob cronSchedulableJob = cronSchedulableJobList.get(0);
        assertEquals("0 30 10 ? * 2", cronSchedulableJob.getCronExpression());
        assertEquals(TREATMENT_ADVICE_START_DATE.plusWeeks(2).toDate(), cronSchedulableJob.getStartTime());
        assertEquals(TREATMENT_ADVICE_END_DATE.toDate(), cronSchedulableJob.getEndTime());
        assertEquals(PATIENT_ID, cronSchedulableJob.getMotechEvent().getParameters().get(FourDayRecallListener.PATIENT_DOC_ID_KEY));

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
}