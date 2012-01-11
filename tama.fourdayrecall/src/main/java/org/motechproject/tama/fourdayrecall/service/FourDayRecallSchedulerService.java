package org.motechproject.tama.fourdayrecall.service;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.motechproject.model.*;
import org.motechproject.scheduler.MotechSchedulerService;
import org.motechproject.scheduler.builder.WeeklyCronJobExpressionBuilder;
import org.motechproject.tama.common.TAMAConstants;
import org.motechproject.tama.fourdayrecall.builder.FourDayRecallEventPayloadBuilder;
import org.motechproject.tama.patient.domain.Patient;
import org.motechproject.tama.patient.domain.TimeMeridiem;
import org.motechproject.tama.patient.domain.TimeOfDay;
import org.motechproject.tama.patient.domain.TreatmentAdvice;
import org.motechproject.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

@Component
public class FourDayRecallSchedulerService {
    private MotechSchedulerService motechSchedulerService;
    private FourDayRecallDateService fourDayRecallDateService;
    private Properties ivrProperties;
    private Properties fourDayRecallProperties;

    @Autowired
    public FourDayRecallSchedulerService(MotechSchedulerService motechSchedulerService, FourDayRecallDateService fourDayRecallDateService,
                                         @Qualifier("ivrProperties") Properties ivrProperties, @Qualifier("fourDayRecallProperties") Properties fourDayRecallProperties) {
        this.motechSchedulerService = motechSchedulerService;
        this.fourDayRecallDateService = fourDayRecallDateService;
        this.ivrProperties = ivrProperties;
        this.fourDayRecallProperties = fourDayRecallProperties;
    }

    public void scheduleFourDayRecallJobs(Patient patient, TreatmentAdvice treatmentAdvice) {
        scheduleFourDayRecallCalls(patient, treatmentAdvice);
        scheduleFallingAdherenceAlertJobsForFourDayRecall(patient, treatmentAdvice);
    }

    public void unscheduleFourDayRecallJobs(Patient patient) {
        Integer daysToRetry = Integer.valueOf(fourDayRecallProperties.getProperty(TAMAConstants.FOUR_DAY_RECALL_DAYS_TO_RETRY));
        for (int count = 0; count <= daysToRetry; count++) {
            motechSchedulerService.unscheduleJob(TAMAConstants.FOUR_DAY_RECALL_SUBJECT, count + patient.getId());
        }
        motechSchedulerService.unscheduleRepeatingJob(TAMAConstants.FOUR_DAY_RECALL_SUBJECT, patient.getId());

        //The number of WEEKLY_FALLING_TREND_AND_ADHERENCE_IN_RED_ALERT_SUBJECT jobs is not configurable. Three are scheduled one 3 successive days.
        for (int count = 0; count <= 2; count++) {
            motechSchedulerService.unscheduleJob(TAMAConstants.WEEKLY_FALLING_TREND_AND_ADHERENCE_IN_RED_ALERT_SUBJECT, count + patient.getId());
        }
    }

    public void scheduleRepeatingJobsForFourDayRecall(Patient patient) {
        Integer maxOutboundRetries = Integer.valueOf(ivrProperties.getProperty(TAMAConstants.RETRIES_PER_DAY));
        int repeatIntervalInMinutes = Integer.valueOf(ivrProperties.getProperty(TAMAConstants.RETRY_INTERVAL));

        TimeOfDay callTime = patient.getPatientPreferences().getBestCallTime();
        DateTime todayCallTime = DateUtil.now().withHourOfDay(callTime.toTime().getHour()).withMinuteOfHour(callTime.toTime().getMinute());
        DateTime jobStartTime = todayCallTime.plusMinutes(repeatIntervalInMinutes);
        DateTime jobEndTime = jobStartTime.plusDays(1);

        Map<String, Object> eventParams = new FourDayRecallEventPayloadBuilder()
                .withJobId(patient.getId())
                .withPatientDocId(patient.getId())
                .withRetryFlag(true)
                .payload();
        MotechEvent fourDayRecallRepeatingEvent = new MotechEvent(TAMAConstants.FOUR_DAY_RECALL_SUBJECT, eventParams);
        RepeatingSchedulableJob repeatingSchedulableJob = new RepeatingSchedulableJob(fourDayRecallRepeatingEvent, jobStartTime.toDate(), jobEndTime.toDate(), maxOutboundRetries - 1, repeatIntervalInMinutes * 60 * 1000);
        motechSchedulerService.scheduleRepeatingJob(repeatingSchedulableJob);
    }

    private void scheduleFourDayRecallCalls(Patient patient, TreatmentAdvice treatmentAdvice) {
        String patientDocId = patient.getId();
        DayOfWeek dayOfWeeklyCall = patient.getPatientPreferences().getDayOfWeeklyCall();
        Time callTime = patient.getPatientPreferences().getBestCallTime().toTime();
        Integer daysToRetry = Integer.valueOf(fourDayRecallProperties.getProperty(TAMAConstants.FOUR_DAY_RECALL_DAYS_TO_RETRY));

        LocalDate startDate = fourDayRecallDateService.firstRecallDate(patient, treatmentAdvice);

        for (int count = 0; count <= daysToRetry; count++) {
            DayOfWeek day = dayOfWeek(dayOfWeeklyCall, count);
            HashMap<String, Object> eventParams = new FourDayRecallEventPayloadBuilder()
                    .withJobId(count + patientDocId)
                    .withPatientDocId(patientDocId)
                    .payload();

            scheduleWeeklyEvent(getJobStartDate(startDate, patient, treatmentAdvice).plusDays(count).toDate(), getJobEndDate(treatmentAdvice), day, callTime, eventParams, TAMAConstants.FOUR_DAY_RECALL_SUBJECT);
        }
    }

    private void scheduleFallingAdherenceAlertJobsForFourDayRecall(Patient patient, TreatmentAdvice treatmentAdvice) {
        String patientDocId = patient.getId();
        DayOfWeek dayOfWeeklyCall = patient.getPatientPreferences().getDayOfWeeklyCall();
        Time eventTime = new TimeOfDay(0, 0, TimeMeridiem.AM).toTime();
        Integer daysToRetry = Integer.valueOf(fourDayRecallProperties.getProperty(TAMAConstants.FOUR_DAY_RECALL_DAYS_TO_RETRY));

        LocalDate startDate = fourDayRecallDateService.firstRecallDate(patient, treatmentAdvice);
        for (int count = 0; count <= daysToRetry; count++) {
            DayOfWeek eventDay = dayOfWeek(dayOfWeeklyCall, count + 1); // +1 is so that it is scheduled at midnight. 12:00 AM of NEXT day
            FourDayRecallEventPayloadBuilder paramsBuilder = new FourDayRecallEventPayloadBuilder()
                    .withJobId(count + patientDocId)
                    .withPatientDocId(patientDocId);

            if (count == daysToRetry) paramsBuilder.withLastRetryDayFlagSet();

            scheduleWeeklyEvent(getJobStartDate(startDate, patient, treatmentAdvice).plusDays(count).toDate(), getJobEndDate(treatmentAdvice), eventDay, eventTime, paramsBuilder.payload(), TAMAConstants.WEEKLY_FALLING_TREND_AND_ADHERENCE_IN_RED_ALERT_SUBJECT);
        }
    }

    private void scheduleWeeklyEvent(Date jobStartDate, Date jobEndDate, DayOfWeek day, Time time, Map<String, Object> params, String eventName) {
        MotechEvent eventToFire = new MotechEvent(eventName, params);
        String cronExpression = new WeeklyCronJobExpressionBuilder(day).withTime(time).build();

        CronSchedulableJob cronJobForFourDayRecall = new CronSchedulableJob(eventToFire, cronExpression, jobStartDate, jobEndDate);
        motechSchedulerService.scheduleJob(cronJobForFourDayRecall);
    }

    private DayOfWeek dayOfWeek(DayOfWeek dayOfWeek, int count) {
        int dayOfWeekNum = (dayOfWeek.getValue() + count) % 7;
        dayOfWeekNum = (dayOfWeekNum == 0) ? 7 : dayOfWeekNum;
        return DayOfWeek.getDayOfWeek(dayOfWeekNum);
    }

    private LocalDate getJobStartDate(LocalDate firstRecallDate, Patient patient, TreatmentAdvice treatmentAdvice) {
        LocalDate currentWeekStartDate = fourDayRecallDateService.treatmentWeekStartDate(DateUtil.today(), treatmentAdvice);
        final LocalDate currentWeekRecallDate = fourDayRecallDateService.nextRecallOn(currentWeekStartDate, patient).toLocalDate();
        if (firstRecallDate.isBefore(currentWeekRecallDate)) {
            return currentWeekRecallDate;
        }
        return firstRecallDate;
    }

    private Date getJobEndDate(TreatmentAdvice treatmentAdvice) {
        return DateUtil.newDate(treatmentAdvice.getEndDate()) == null ? null : DateUtil.newDate(treatmentAdvice.getEndDate()).toDate();
    }
}