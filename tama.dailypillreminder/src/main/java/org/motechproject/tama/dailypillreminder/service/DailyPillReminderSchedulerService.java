package org.motechproject.tama.dailypillreminder.service;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.motechproject.model.CronSchedulableJob;
import org.motechproject.model.DayOfWeek;
import org.motechproject.model.MotechEvent;
import org.motechproject.model.Time;
import org.motechproject.scheduler.MotechSchedulerService;
import org.motechproject.scheduler.builder.CronJobSimpleExpressionBuilder;
import org.motechproject.scheduler.builder.WeeklyCronJobExpressionBuilder;
import org.motechproject.server.pillreminder.api.builder.SchedulerPayloadBuilder;
import org.motechproject.tama.common.TAMAConstants;
import org.motechproject.tama.common.domain.TimeMeridiem;
import org.motechproject.tama.common.domain.TimeOfDay;
import org.motechproject.tama.patient.domain.Patient;
import org.motechproject.tama.patient.domain.TreatmentAdvice;
import org.motechproject.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Map;

import static org.motechproject.tama.patient.util.CallPlanUtil.callPlanStartDate;

@Component
public class DailyPillReminderSchedulerService {
    private MotechSchedulerService motechSchedulerService;

    @Autowired
    public DailyPillReminderSchedulerService(MotechSchedulerService motechSchedulerService) {
        this.motechSchedulerService = motechSchedulerService;
    }

    public void scheduleDailyPillReminderJobs(Patient patient, TreatmentAdvice treatmentAdvice) {
        scheduleJobForAdherenceTrendFeedbackForDailyPillReminder(patient, treatmentAdvice);
        scheduleJobForDeterminingAdherenceQualityInDailyPillReminder(patient, treatmentAdvice);
    }

    public void unscheduleDailyPillReminderJobs(Patient patient) {
        unscheduleJobForAdherenceTrendFeedbackForDailyPillReminder(patient);
        unscheduleJobForDeterminingAdherenceQualityInDailyPillReminder(patient);
    }

    void scheduleJobForAdherenceTrendFeedbackForDailyPillReminder(Patient patient, TreatmentAdvice treatmentAdvice) {
        Map<String, Object> eventParams = new SchedulerPayloadBuilder().withJobId(treatmentAdvice.getPatientId())
                .withExternalId(treatmentAdvice.getPatientId())
                .payload();
        MotechEvent adherenceWeeklyTrendEvent = new MotechEvent(TAMAConstants.ADHERENCE_WEEKLY_TREND_SCHEDULER_SUBJECT, eventParams);
        LocalDate startDate = callPlanStartDate(patient, treatmentAdvice).plusWeeks(5);

        String cronExpression = new WeeklyCronJobExpressionBuilder(DayOfWeek.getDayOfWeek(startDate.getDayOfWeek())).build();
        Date jobStartDate = getJobStartDate(startDate);
        CronSchedulableJob adherenceJob = new CronSchedulableJob(adherenceWeeklyTrendEvent, cronExpression, jobStartDate, treatmentAdvice.getEndDate());
        motechSchedulerService.safeScheduleJob(adherenceJob);
    }

    void scheduleJobForDeterminingAdherenceQualityInDailyPillReminder(Patient patient, TreatmentAdvice treatmentAdvice) {
        Map<String, Object> eventParams = new SchedulerPayloadBuilder().withJobId(patient.getId())
                .withExternalId(patient.getId())
                .payload();
        MotechEvent eventToDetermineAdherenceInRed = new MotechEvent(TAMAConstants.DAILY_ADHERENCE_IN_RED_ALERT_SUBJECT, eventParams);

        Date jobStartDate = getJobStartDate(callPlanStartDate(patient, treatmentAdvice));
        Date jobEndDate = treatmentAdvice.getEndDate() == null ? null : DateUtil.newDate(treatmentAdvice.getEndDate()).plusDays(1).toDate();

        Time eventTime = new TimeOfDay(0, 0, TimeMeridiem.AM).toTime();

        CronJobSimpleExpressionBuilder cronJobSimpleExpressionBuilder = new CronJobSimpleExpressionBuilder(eventTime);
        CronSchedulableJob jobToDetermineAdherenceQuality = new CronSchedulableJob(eventToDetermineAdherenceInRed, cronJobSimpleExpressionBuilder.build(), jobStartDate, jobEndDate);

        motechSchedulerService.safeScheduleJob(jobToDetermineAdherenceQuality);
    }

    void unscheduleJobForAdherenceTrendFeedbackForDailyPillReminder(Patient patient) {
        motechSchedulerService.safeUnscheduleJob(TAMAConstants.ADHERENCE_WEEKLY_TREND_SCHEDULER_SUBJECT, patient.getId());
    }

    void unscheduleJobForDeterminingAdherenceQualityInDailyPillReminder(Patient patient) {
        motechSchedulerService.safeUnscheduleJob(TAMAConstants.DAILY_ADHERENCE_IN_RED_ALERT_SUBJECT, patient.getId());
    }

    private Date getJobStartDate(LocalDate startDate) {
        DateTime now = DateUtil.now();
        return DateUtil.newDateTime(startDate.toDate()).isBefore(now) ? now.toDate() : startDate.toDate();
    }
}