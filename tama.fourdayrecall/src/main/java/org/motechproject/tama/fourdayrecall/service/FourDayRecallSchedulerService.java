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
import org.motechproject.tama.patient.repository.AllPatients;
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
    @Autowired
    private MotechSchedulerService motechSchedulerService;
    @Qualifier("ivrProperties")
    @Autowired
    private Properties ivrProperties;
    @Qualifier("fourDayRecallProperties")
    @Autowired
    private Properties fourDayRecallProperties;
    @Autowired
    private AllPatients allPatients;
    @Autowired
    private FourDayRecallService fourDayRecallService;


    public FourDayRecallSchedulerService() {
    }

    public FourDayRecallSchedulerService(MotechSchedulerService motechSchedulerService, Properties ivrProperties, Properties fourDayRecallProperties, AllPatients allPatients, FourDayRecallService fourDayRecallService) {
        this.motechSchedulerService = motechSchedulerService;
        this.ivrProperties = ivrProperties;
        this.fourDayRecallProperties = fourDayRecallProperties;
        this.allPatients = allPatients;
        this.fourDayRecallService = fourDayRecallService;
    }

    public void scheduleJobsForFourDayRecall(Patient patient, TreatmentAdvice treatmentAdvice) {
        scheduleFourDayRecallCalls(patient, treatmentAdvice);
        scheduleFallingAdherenceAlertJobsForFourDayRecall(patient, treatmentAdvice);
    }

    public void scheduleRepeatingJobsForFourDayRecall(String patientDocId) {
        Patient patient = allPatients.get(patientDocId);
        Integer maxOutboundRetries = Integer.valueOf(ivrProperties.getProperty(TAMAConstants.RETRIES_PER_DAY));
        int repeatIntervalInMinutes = Integer.valueOf(ivrProperties.getProperty(TAMAConstants.RETRY_INTERVAL));

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

    private void scheduleFourDayRecallCalls(Patient patient, TreatmentAdvice treatmentAdvice) {
        String patientDocId = patient.getId();
        DayOfWeek dayOfWeeklyCall = patient.getPatientPreferences().getDayOfWeeklyCall();
        Time callTime = patient.getPatientPreferences().getBestCallTime().toTime();
        Integer daysToRetry = Integer.valueOf(fourDayRecallProperties.getProperty(TAMAConstants.FOUR_DAY_RECALL_DAYS_TO_RETRY));

        LocalDate weeklyAdherenceTrackingStartDate = fourDayRecallService.getWeeklyAdherenceTrackingStartDate(patient, treatmentAdvice);
        LocalDate startDate = weeklyAdherenceTrackingStartDate.plusDays(FourDayRecallService.DAYS_TO_RECALL);

        for (int count = 0; count <= daysToRetry; count++) {
            DayOfWeek day = dayOfWeek(dayOfWeeklyCall, count);
            HashMap<String, Object> eventParams = new FourDayRecallEventPayloadBuilder()
                    .withJobId(count + patientDocId)
                    .withPatientDocId(patientDocId)
                    .payload();

            scheduleWeeklyEvent(getJobStartDate(startDate), getJobEndDate(treatmentAdvice), day, callTime, eventParams, TAMAConstants.FOUR_DAY_RECALL_SUBJECT);
        }
    }

    public void scheduleFallingAdherenceAlertJobsForFourDayRecall(Patient patient, TreatmentAdvice treatmentAdvice) {
        String patientDocId = patient.getId();
        DayOfWeek dayOfWeeklyCall = patient.getPatientPreferences().getDayOfWeeklyCall();
        Time eventTime = new TimeOfDay(0, 0, TimeMeridiem.AM).toTime();
        Integer daysToRetry = Integer.valueOf(fourDayRecallProperties.getProperty(TAMAConstants.FOUR_DAY_RECALL_DAYS_TO_RETRY));

        LocalDate startDate = fourDayRecallService.findFirstFourDayRecallDateForTreatmentAdvice(patientDocId, fourDayRecallService.getWeeklyAdherenceTrackingStartDate(patient, treatmentAdvice)).minusDays(1);

        for (int count = 0; count <= daysToRetry; count++) {
            DayOfWeek eventDay = dayOfWeek(dayOfWeeklyCall, count + 1); // +1 is so that it is scheduled at midnight. 12:00 AM of NEXT day
            FourDayRecallEventPayloadBuilder paramsBuilder = new FourDayRecallEventPayloadBuilder()
                    .withJobId(count + patientDocId)
                    .withPatientDocId(patientDocId);

            if (count == daysToRetry) paramsBuilder.withLastRetryDayFlagSet();

            scheduleWeeklyEvent(getJobStartDate(startDate), getJobEndDate(treatmentAdvice), eventDay, eventTime, paramsBuilder.payload(), TAMAConstants.WEEKLY_FALLING_TREND_AND_ADHERENCE_IN_RED_ALERT_SUBJECT);
        }
    }

    private void scheduleWeeklyEvent(Date jobStartDate, Date jobEndDate, DayOfWeek day, Time time, Map<String, Object> params, String eventName) {
        MotechEvent eventToFire = new MotechEvent(eventName, params);
        String cronExpression = new WeeklyCronJobExpressionBuilder(day).withTime(time).build();

        CronSchedulableJob cronJobForFourDayRecall = new CronSchedulableJob(eventToFire, cronExpression, jobStartDate, jobEndDate);
        motechSchedulerService.scheduleJob(cronJobForFourDayRecall);
    }

    private Date getJobStartDate(LocalDate startDate) {
        return DateUtil.newDateTime(startDate.toDate()).isBefore(DateUtil.now()) ? DateUtil.now().toDate() : startDate.toDate();
    }

    private Date getJobEndDate(TreatmentAdvice treatmentAdvice) {
        return DateUtil.newDate(treatmentAdvice.getEndDate()) == null ? null : DateUtil.newDate(treatmentAdvice.getEndDate()).toDate();
    }

    private DayOfWeek dayOfWeek(DayOfWeek dayOfWeek, int count) {
        int dayOfWeekNum = (dayOfWeek.getValue() + count) % 7;
        dayOfWeekNum = (dayOfWeekNum == 0) ? 7 : dayOfWeekNum;
        return DayOfWeek.getDayOfWeek(dayOfWeekNum);
    }

    public void unScheduleFourDayRecallJobs(Patient patient) {
        Integer daysToRetry = Integer.valueOf(fourDayRecallProperties.getProperty(TAMAConstants.FOUR_DAY_RECALL_DAYS_TO_RETRY));
        for (int count = 0; count <= daysToRetry; count++) {
            motechSchedulerService.unscheduleJob(TAMAConstants.FOUR_DAY_RECALL_SUBJECT, count + patient.getId());
        }

        motechSchedulerService.unscheduleRepeatingJob(TAMAConstants.FOUR_DAY_RECALL_SUBJECT, patient.getId());
        motechSchedulerService.unscheduleJob(TAMAConstants.WEEKLY_FALLING_TREND_AND_ADHERENCE_IN_RED_ALERT_SUBJECT, patient.getId());
    }
}