package org.motechproject.tama.platform.service;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.motechproject.model.*;
import org.motechproject.scheduler.MotechSchedulerService;
import org.motechproject.scheduler.builder.CronJobSimpleExpressionBuilder;
import org.motechproject.scheduler.builder.WeeklyCronJobExpressionBuilder;
import org.motechproject.server.pillreminder.builder.SchedulerPayloadBuilder;
import org.motechproject.tama.TAMAConstants;
import org.motechproject.tama.domain.*;
import org.motechproject.tama.repository.AllPatients;
import org.motechproject.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

@Component
public class TamaSchedulerService {
    public static final String IS_RETRY = "isRetry";
    @Autowired
    private MotechSchedulerService motechSchedulerService;
    @Qualifier("ivrProperties")
    @Autowired
    private Properties properties;
    @Autowired
    private AllPatients allPatients;


    public TamaSchedulerService() {
    }

    public TamaSchedulerService(MotechSchedulerService motechSchedulerService, Properties properties, AllPatients allPatients) {
        this.motechSchedulerService = motechSchedulerService;
        this.properties = properties;
        this.allPatients = allPatients;
    }

    public void scheduleJobsForFourDayRecall(Patient patient, TreatmentAdvice treatmentAdvice) {
        scheduleFourDayRecallCalls(patient, treatmentAdvice);
        scheduleFallingAdherenceAlertJobs(patient, treatmentAdvice);
    }

    private void scheduleFourDayRecallCalls(Patient patient, TreatmentAdvice treatmentAdvice) {
        String patientDocId = patient.getId();
        DayOfWeek dayOfWeeklyCall = patient.getPatientPreferences().getDayOfWeeklyCall();
        Time callTime = patient.getPatientPreferences().getBestCallTime().toTime();
        Integer daysToRetry = Integer.valueOf(properties.getProperty(TAMAConstants.FOUR_DAY_RECALL_DAYS_TO_RETRY));

        LocalDate weeklyAdherenceTrackingStartDate = getWeeklyAdherenceTrackingStartDate(patient, treatmentAdvice);
        LocalDate startDate = weeklyAdherenceTrackingStartDate.plusDays(4);

        for (int count = 0; count <= daysToRetry; count++) {
            DayOfWeek day = dayOfWeek(dayOfWeeklyCall, count);
            HashMap<String, Object> eventParams = new FourDayRecallEventPayloadBuilder()
                    .withJobId(count + patientDocId)
                    .withPatientDocId(patientDocId)
                    .payload();

            scheduleWeeklyEvent(getJobStartDate(startDate), getJobEndDate(treatmentAdvice), day, callTime, eventParams, TAMAConstants.FOUR_DAY_RECALL_SUBJECT);
        }
    }

    private LocalDate getWeeklyAdherenceTrackingStartDate(Patient patient, TreatmentAdvice treatmentAdvice) {
        LocalDate weeklyAdherenceTrackingStartDate = DateUtil.newDate(treatmentAdvice.getStartDate());
        if (patient.getPatientPreferences().getCallPreferenceTransitionDate() != null && weeklyAdherenceTrackingStartDate.isBefore(patient.getPatientPreferences().getCallPreferenceTransitionDate().toLocalDate()))
            weeklyAdherenceTrackingStartDate = patient.getPatientPreferences().getCallPreferenceTransitionDate().toLocalDate();
        return weeklyAdherenceTrackingStartDate;
    }

    private void scheduleWeeklyEvent(Date jobStartDate, Date jobEndDate, DayOfWeek day, Time time, Map<String, Object> params, String eventName) {
        MotechEvent eventToFire = new MotechEvent(eventName, params);
        String cronExpression = new WeeklyCronJobExpressionBuilder(day).withTime(time).build();

        CronSchedulableJob cronJobForFourDayRecall = new CronSchedulableJob(eventToFire, cronExpression, jobStartDate, jobEndDate);
        motechSchedulerService.scheduleJob(cronJobForFourDayRecall);
    }

    public void scheduleFallingAdherenceAlertJobs(Patient patient, TreatmentAdvice treatmentAdvice) {
        String patientDocId = patient.getId();
        DayOfWeek dayOfWeeklyCall = patient.getPatientPreferences().getDayOfWeeklyCall();
        Time eventTime = new TimeOfDay(0, 0, TimeMeridiem.AM).toTime();
        Integer daysToRetry = Integer.valueOf(properties.getProperty(TAMAConstants.FOUR_DAY_RECALL_DAYS_TO_RETRY));



        LocalDate startDate = getWeeklyAdherenceTrackingStartDate(patient, treatmentAdvice).plusDays(4 + 14); // days to recall + 2 weeks offset

        for (int count = 0; count <= daysToRetry; count++) {
            DayOfWeek eventDay = dayOfWeek(dayOfWeeklyCall, count + 1); // +1 is so that it is scheduled at midnight. 12:00 AM of NEXT day
            FourDayRecallEventPayloadBuilder paramsBuilder = new FourDayRecallEventPayloadBuilder()
                    .withJobId(count + patientDocId)
                    .withPatientDocId(patientDocId);

            if (count == daysToRetry) paramsBuilder.withLastRetryDayFlagSet();

            scheduleWeeklyEvent(getJobStartDate(startDate), getJobEndDate(treatmentAdvice), eventDay, eventTime, paramsBuilder.payload(), TAMAConstants.WEEKLY_FALLING_TREND_SUBJECT);
        }
    }

    public void unscheduleFallingAdherenceAlertJobs(String patientId) {
        Integer daysToRetry = Integer.valueOf(properties.getProperty(TAMAConstants.FOUR_DAY_RECALL_DAYS_TO_RETRY));
        for (int count = 0; count <= daysToRetry; count++) {
            motechSchedulerService.unscheduleJob(TAMAConstants.ADHERENCE_WEEKLY_TREND_SCHEDULER_SUBJECT, count + patientId);
        }
    }

    private Date getJobEndDate(TreatmentAdvice treatmentAdvice) {
        return DateUtil.newDate(treatmentAdvice.getEndDate()) == null ? null : DateUtil.newDate(treatmentAdvice.getEndDate()).toDate();
    }

    private DayOfWeek dayOfWeek(DayOfWeek dayOfWeek, int count) {
        int dayOfWeekNum = (dayOfWeek.getValue() + count) % 7;
        dayOfWeekNum = (dayOfWeekNum == 0) ? 7 : dayOfWeekNum;
        return DayOfWeek.getDayOfWeek(dayOfWeekNum);
    }

    public void scheduleRepeatingJobsForFourDayRecall(String patientDocId) {
        Patient patient = allPatients.get(patientDocId);
        Integer maxOutboundRetries = Integer.valueOf(properties.getProperty(TAMAConstants.RETRIES_PER_DAY));
        int repeatIntervalInMinutes = Integer.valueOf(properties.getProperty(TAMAConstants.RETRY_INTERVAL));

        TimeOfDay callTime = patient.getPatientPreferences().getBestCallTime();
        DateTime todayCallTime = DateUtil.now().withHourOfDay(callTime.toTime().getHour()).withMinuteOfHour(callTime.toTime().getMinute());
        DateTime jobStartTime = todayCallTime.plusMinutes(repeatIntervalInMinutes);
        DateTime jobEndTime = jobStartTime.plusDays(1);

        Map<String, Object> eventParams = new FourDayRecallEventPayloadBuilder()
                .withJobId(patientDocId)
                .withPatientDocId(patientDocId)
                .withRetryFlag(true)
                .payload();
        MotechEvent fourDayRecallRepeatingEvent = new MotechEvent(TAMAConstants.FOUR_DAY_RECALL_SUBJECT, eventParams);
        RepeatingSchedulableJob repeatingSchedulableJob = new RepeatingSchedulableJob(fourDayRecallRepeatingEvent, jobStartTime.toDate(), jobEndTime.toDate(), maxOutboundRetries - 1, repeatIntervalInMinutes * 60 * 1000);
        motechSchedulerService.scheduleRepeatingJob(repeatingSchedulableJob);
    }

    public void scheduleJobForAdherenceTrendFeedback(TreatmentAdvice treatmentAdvice) {
        Map<String, Object> eventParams = new SchedulerPayloadBuilder().withJobId(treatmentAdvice.getPatientId())
                .withExternalId(treatmentAdvice.getPatientId())
                .payload();
        MotechEvent adherenceWeeklyTrendEvent = new MotechEvent(TAMAConstants.ADHERENCE_WEEKLY_TREND_SCHEDULER_SUBJECT, eventParams);
        LocalDate startDate = DateUtil.newDate(treatmentAdvice.getStartDate()).plusWeeks(5);
        String cronExpression = new WeeklyCronJobExpressionBuilder(DayOfWeek.getDayOfWeek(startDate.getDayOfWeek())).build();
        Date jobStartDate = startDate.toDate();//getJobStartDate(startDate);
        CronSchedulableJob adherenceJob = new CronSchedulableJob(adherenceWeeklyTrendEvent, cronExpression, jobStartDate, treatmentAdvice.getEndDate());
        motechSchedulerService.scheduleJob(adherenceJob);
    }

    private Date getJobStartDate(LocalDate startDate) {
        return DateUtil.newDateTime(startDate.toDate()).isBefore(DateUtil.now()) ? DateUtil.now().toDate() : startDate.toDate();
    }

    public void unscheduleJobForAdherenceTrendFeedback(TreatmentAdvice treatmentAdvice) {
        motechSchedulerService.unscheduleJob(TAMAConstants.ADHERENCE_WEEKLY_TREND_SCHEDULER_SUBJECT, treatmentAdvice.getPatientId());
    }

    public void scheduleJobForOutboxCall(Patient patient) {
        String outboxCallJobCronExpression = new CronJobSimpleExpressionBuilder(patient.getPatientPreferences().getBestCallTime().toTime()).build();
        Map<String, Object> eventParams = new SchedulerPayloadBuilder().withJobId(patient.getId())
                .withExternalId(patient.getId())
                .payload();
        MotechEvent outboxCallEvent = new MotechEvent(TAMAConstants.OUTBOX_CALL_SCHEDULER_SUBJECT, eventParams);
        CronSchedulableJob outboxCallJob = new CronSchedulableJob(outboxCallEvent, outboxCallJobCronExpression, DateUtil.now().toDate(), null);
        motechSchedulerService.scheduleJob(outboxCallJob);
    }

    public void unscheduleJobForOutboxCall(Patient patient) {
        motechSchedulerService.unscheduleJob(TAMAConstants.OUTBOX_CALL_SCHEDULER_SUBJECT, patient.getId());
    }

    public void scheduleRepeatingJobForOutBoxCall(Patient patient) {
        if (patient.getPatientPreferences().getCallPreference().equals(CallPreference.DailyPillReminder)) {
            Map<String, Object> eventParams = new SchedulerPayloadBuilder().withJobId(patient.getId())
                    .withExternalId(patient.getId())
                    .payload();
            eventParams.put(IS_RETRY, "true");
            MotechEvent outboxCallEvent = new MotechEvent(TAMAConstants.OUTBOX_CALL_SCHEDULER_SUBJECT, eventParams);
            Integer maxOutboundRetries = Integer.valueOf(properties.getProperty(TAMAConstants.RETRIES_PER_DAY)) - 1;
            int repeatIntervalInMinutes = Integer.valueOf(properties.getProperty(TAMAConstants.OUT_BOX_CALL_RETRY_INTERVAL));
            RepeatingSchedulableJob outboxCallJob = new RepeatingSchedulableJob(outboxCallEvent, DateUtil.now().plusMinutes(repeatIntervalInMinutes).toDate(), DateUtil.today().plusDays(1).toDate(), maxOutboundRetries, repeatIntervalInMinutes * 60 * 1000);
            motechSchedulerService.scheduleRepeatingJob(outboxCallJob);
        }
    }

    public void unscheduleRepeatingJobForOutboxCall(String externalId) {
        motechSchedulerService.unscheduleRepeatingJob(TAMAConstants.OUTBOX_CALL_SCHEDULER_SUBJECT, externalId);
    }

    public void unScheduleFourDayRecallJobs(Patient patient) {
        Integer daysToRetry = Integer.valueOf(properties.getProperty(TAMAConstants.FOUR_DAY_RECALL_DAYS_TO_RETRY));
        for (int count = 0; count <= daysToRetry; count++) {
            motechSchedulerService.unscheduleJob(TAMAConstants.FOUR_DAY_RECALL_SUBJECT, count + patient.getId());
        }

        motechSchedulerService.unscheduleRepeatingJob(TAMAConstants.FOUR_DAY_RECALL_SUBJECT, patient.getId());
        motechSchedulerService.unscheduleJob(TAMAConstants.WEEKLY_FALLING_TREND_SUBJECT, patient.getId());
    }

    public void scheduleJobForDeterminingAdherenceQualityInDailyPillReminder(Patient patient, TreatmentAdvice treatmentAdvice) {
        if(patient.getPatientPreferences().getCallPreference().equals(CallPreference.DailyPillReminder)) {
            Map<String, Object> eventParams = new SchedulerPayloadBuilder().withJobId(patient.getId())
                    .withExternalId(patient.getId())
                    .payload();
            MotechEvent eventToDetermineAdherenceInRed = new MotechEvent(TAMAConstants.DETERMINE_ADHERENCE_QUALITY_IN_DAILY_PILL_REMINDER, eventParams);

            Date jobStartDate = getJobStartDate(DateUtil.newDate(treatmentAdvice.getStartDate()));
            Date jobEndDate = treatmentAdvice.getEndDate() == null ? null : DateUtil.newDate(treatmentAdvice.getEndDate()).plusDays(1).toDate();

            Time eventTime = new TimeOfDay(0, 0, TimeMeridiem.AM).toTime();

            CronJobSimpleExpressionBuilder cronJobSimpleExpressionBuilder = new CronJobSimpleExpressionBuilder(eventTime);
            CronSchedulableJob jobToDetermineAdherenceQuality = new CronSchedulableJob(eventToDetermineAdherenceInRed, cronJobSimpleExpressionBuilder.build(), jobStartDate, jobEndDate);

            motechSchedulerService.scheduleJob(jobToDetermineAdherenceQuality);
        }
    }

    public void unscheduleJobForDeterminingAdherenceQualityInDailyPillReminder(Patient patient) {
        motechSchedulerService.unscheduleJob(TAMAConstants.DETERMINE_ADHERENCE_QUALITY_IN_DAILY_PILL_REMINDER, patient.getId());
    }

    public void scheduleJobForDeterminingAdherenceQualityInFourDayRecall(Patient patient, TreatmentAdvice treatmentAdvice) {
        if(patient.getPatientPreferences().getCallPreference().equals(CallPreference.FourDayRecall)) {
            String patientDocId = patient.getId();
            DayOfWeek dayOfWeeklyCall = patient.getPatientPreferences().getDayOfWeeklyCall();
            Time eventTime = new TimeOfDay(0, 0, TimeMeridiem.AM).toTime();
            Integer daysToRetry = Integer.valueOf(properties.getProperty(TAMAConstants.FOUR_DAY_RECALL_DAYS_TO_RETRY));

            LocalDate fourDaysAfterAdviceStarts = DateUtil.newDate(treatmentAdvice.getStartDate()).plusDays(4);
            Date jobStartDate = getJobStartDate(fourDaysAfterAdviceStarts);
            Date jobEndDate = getJobEndDate(treatmentAdvice);

            for (int count = 0; count <= daysToRetry; count++) {
                DayOfWeek eventDay = dayOfWeek(dayOfWeeklyCall, count + 1);
                FourDayRecallEventPayloadBuilder paramsBuilder = new FourDayRecallEventPayloadBuilder()
                        .withJobId(count + patientDocId)
                        .withPatientDocId(patientDocId);

                if (count == daysToRetry) paramsBuilder.withLastRetryDayFlagSet();

                scheduleWeeklyEvent(jobStartDate, jobEndDate, eventDay, eventTime, paramsBuilder.payload(), TAMAConstants.DETERMINE_ADHERENCE_QUALITY_IN_FOUR_DAY_RECALL);
            }
        }
    }

    public void unscheduleJobForDeterminingAdheranceQualityInFourDayRecall(Patient patient) {
        Integer daysToRetry = Integer.valueOf(properties.getProperty(TAMAConstants.FOUR_DAY_RECALL_DAYS_TO_RETRY));
        for (int count = 0; count <= daysToRetry; count++) {
            motechSchedulerService.unscheduleJob(TAMAConstants.DETERMINE_ADHERENCE_QUALITY_IN_FOUR_DAY_RECALL, count + patient.getId());
        }
    }
}