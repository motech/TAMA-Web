package org.motechproject.tama.dailypillreminder.service;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.motechproject.model.CronSchedulableJob;
import org.motechproject.model.MotechEvent;
import org.motechproject.scheduler.MotechSchedulerService;
import org.motechproject.server.pillreminder.EventKeys;
import org.motechproject.tama.common.TAMAConstants;
import org.motechproject.tama.patient.builder.PatientBuilder;
import org.motechproject.tama.patient.domain.CallPreference;
import org.motechproject.tama.patient.domain.DrugDosage;
import org.motechproject.tama.patient.domain.Patient;
import org.motechproject.tama.patient.domain.TreatmentAdvice;
import org.motechproject.util.DateUtil;

import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;

public class DailyPillReminderSchedulerServiceTest {

    private final LocalDate TREATMENT_ADVICE_START_DATE = DateUtil.newDate(2012, 12, 12);
    private final LocalDate TREATMENT_ADVICE_END_DATE = DateUtil.newDate(2012, 12, 24);
    private static final String PATIENT_ID = "patient_id";

    private DailyPillReminderSchedulerService schedulerService;
    private TreatmentAdvice treatmentAdvice;

    @Mock
    MotechSchedulerService motechSchedulerService;

    @Before
    public void setUp() {
        initMocks(this);
        treatmentAdvice = getTreatmentAdvice();
        schedulerService = new DailyPillReminderSchedulerService(motechSchedulerService);
    }

    @Test
    public void shouldScheduleWeeklyAdherenceTrendJob() {
        Patient patient = PatientBuilder.startRecording().withDefaults().withId("patientId").withCallPreference(CallPreference.DailyPillReminder).build();

        schedulerService.scheduleJobForAdherenceTrendFeedbackForDailyPillReminder(patient, treatmentAdvice);

        ArgumentCaptor<CronSchedulableJob> jobCaptor = ArgumentCaptor.forClass(CronSchedulableJob.class);
        verify(motechSchedulerService).safeScheduleJob(jobCaptor.capture());
        Assert.assertEquals("0 0 0 ? * 4", jobCaptor.getValue().getCronExpression());
    }

    @Test
    public void shouldScheduleWeeklyAdherenceTrendJob_StartDateIsBeforeToday() {
        DateTime now = DateUtil.now();
        LocalDate today = now.toLocalDate();
        Patient patient = PatientBuilder.startRecording().withDefaults().withId("patientId").withCallPreference(CallPreference.DailyPillReminder).build();
        treatmentAdvice.getDrugDosages().get(0).setStartDate(today.minusMonths(2));

        schedulerService.scheduleJobForAdherenceTrendFeedbackForDailyPillReminder(patient , treatmentAdvice);
        ArgumentCaptor<CronSchedulableJob> jobCaptor = ArgumentCaptor.forClass(CronSchedulableJob.class);
        verify(motechSchedulerService).safeScheduleJob(jobCaptor.capture());
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
        verify(motechSchedulerService, times(1)).safeScheduleJob(cronArgCaptor.capture());

        CronSchedulableJob jobScheduledWithParams = cronArgCaptor.getValue();
        final MotechEvent motechEventInScheduledJob = jobScheduledWithParams.getMotechEvent();
        Map<String, Object> paramsInScheduledJob = motechEventInScheduledJob.getParameters();

        assertCronSchedulableJob(jobScheduledWithParams, "0 0 0 * * ?", startDate.toDate(), endDate.plusDays(1).toDate());

        assertEquals(paramsInScheduledJob.get(EventKeys.EXTERNAL_ID_KEY), patientId);
        assertEquals(motechEventInScheduledJob.getSubject(), TAMAConstants.DAILY_ADHERENCE_IN_RED_ALERT_SUBJECT);
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
        verify(motechSchedulerService).safeScheduleJob(cronArgCaptor.capture());

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
        assertEquals(motechEventInScheduledJob.getSubject(), TAMAConstants.DAILY_ADHERENCE_IN_RED_ALERT_SUBJECT);
    }

    @Test
    public void shouldUnScheduleJobForDeterminingAdherenceQualityInDailyPillReminder() {
        final String patientId = "123456";
        Patient patient = PatientBuilder.startRecording().withDefaults().withId(patientId).withCallPreference(CallPreference.DailyPillReminder).build();
        schedulerService.unscheduleJobForDeterminingAdherenceQualityInDailyPillReminder(patient);
        verify(motechSchedulerService).unscheduleJob(TAMAConstants.DAILY_ADHERENCE_IN_RED_ALERT_SUBJECT, patientId);
    }

    private void assertCronSchedulableJob(CronSchedulableJob cronSchedulableJob, String cronExpression, Date startTime, Date endTime) {
        assertEquals(cronExpression, cronSchedulableJob.getCronExpression());
        assertEquals(startTime, cronSchedulableJob.getStartTime());
        assertEquals(endTime, cronSchedulableJob.getEndTime());
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

    private TreatmentAdvice getTreatmentAdvice() {
        return getTreatmentAdvice(TREATMENT_ADVICE_START_DATE, TREATMENT_ADVICE_END_DATE);
    }
}
