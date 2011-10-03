package org.motechproject.tama.service;

import org.joda.time.LocalDate;
import org.motechproject.model.*;
import org.motechproject.scheduler.MotechSchedulerService;
import org.motechproject.scheduler.builder.WeeklyCronJobExpressionBuilder;
import org.motechproject.server.pillreminder.builder.SchedulerPayloadBuilder;
import org.motechproject.tama.TAMAConstants;
import org.motechproject.tama.domain.Patient;
import org.motechproject.tama.domain.TreatmentAdvice;
import org.motechproject.tama.util.UUIDUtil;
import org.motechproject.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Properties;

@Component
public class TamaSchedulerService {
    @Autowired
    private MotechSchedulerService motechSchedulerService;
    @Qualifier("ivrProperties")
    @Autowired
    private Properties properties;

    private String FOUR_DAY_RECALL_JOB_ID_PREFIX = "four_day_recall_";

    public TamaSchedulerService() {
    }

    public TamaSchedulerService(MotechSchedulerService motechSchedulerService, Properties properties) {
        this.motechSchedulerService = motechSchedulerService;
        this.properties = properties;
    }

    public void scheduleJobsForFourDayRecall(Patient patient, TreatmentAdvice treatmentAdvice) {
        String patientId = patient.getPatientId();
        LocalDate startDate = new LocalDate(treatmentAdvice.getStartDate()).plusDays(4);
        LocalDate endDate =  new LocalDate(treatmentAdvice.getEndDate());
        DayOfWeek dayOfWeek = patient.getPatientPreferences().getDayOfWeeklyCall();
        Time callTime = patient.getPatientPreferences().getBestCallTime().toTime();
        Integer daysToRetry = Integer.valueOf(properties.getProperty(TAMAConstants.FOUR_DAY_RECALL_DAYS_TO_RETRY));

        for (int count = 0; count < daysToRetry; count++) {
            Map<String, Object> eventParams = new FourDayRecallEventPayloadBuilder()
                    .withJobId(FOUR_DAY_RECALL_JOB_ID_PREFIX + patientId)
                    .withPatientId(patientId)
                    .withStartDate(startDate)
                    .withEndDate(endDate)
                    .payload();
            MotechEvent fourDayRecallEvent = new MotechEvent(TAMAConstants.FOUR_DAY_RECALL_SUBJECT, eventParams);
            DayOfWeek dayOfWeekForReminder = DayOfWeek.getDayOfWeek(dayOfWeek.getValue() + count);
            String cronExpression = new WeeklyCronJobExpressionBuilder(dayOfWeekForReminder).withTime(callTime).build();
            CronSchedulableJob cronJobForForDayRecall = new CronSchedulableJob(fourDayRecallEvent, cronExpression, startDate.plusDays(count).toDate(), endDate.toDate());
            motechSchedulerService.scheduleJob(cronJobForForDayRecall);
        }
    }

    public void scheduleRepeatingJobsForFourDayRecall(String patientId, LocalDate startDate, LocalDate endDate) {
        Integer maxOutboundRetries = Integer.valueOf(properties.getProperty(TAMAConstants.RETRIES_PER_DAY));
        Integer retryInterval = Integer.valueOf(properties.getProperty(TAMAConstants.RETRY_INTERVAL));

        Map<String, Object> eventParams = new FourDayRecallEventPayloadBuilder()
                .withJobId(FOUR_DAY_RECALL_JOB_ID_PREFIX + UUIDUtil.newUUID())
                .withPatientId(patientId)
                .payload();
        MotechEvent fourDayRecallRepeatingEvent = new MotechEvent(TAMAConstants.FOUR_DAY_RECALL_SUBJECT, eventParams);
        RepeatingSchedulableJob repeatingSchedulableJob = new RepeatingSchedulableJob(fourDayRecallRepeatingEvent, startDate.toDate(), endDate.toDate(), maxOutboundRetries, retryInterval);
        motechSchedulerService.scheduleRepeatingJob(repeatingSchedulableJob);
    }

    public void scheduleJobForAdherenceTrendFeedback(TreatmentAdvice treatmentAdvice) {
        Map<String, Object> eventParams = new SchedulerPayloadBuilder().withJobId(treatmentAdvice.getId())
                .withExternalId(treatmentAdvice.getPatientId())
                .payload();
        LocalDate startDate = DateUtil.newDate(treatmentAdvice.getStartDate()).plusWeeks(5);
        MotechEvent adherenceWeeklyTrendEvent = new MotechEvent(TAMAConstants.ADHERENCE_WEEKLY_TREND_SCHEDULER_SUBJECT, eventParams);
        String cronExpression = new WeeklyCronJobExpressionBuilder(DayOfWeek.getDayOfWeek(startDate.getDayOfWeek())).build();
        CronSchedulableJob adherenceJob = new CronSchedulableJob(adherenceWeeklyTrendEvent, cronExpression, startDate.toDate(), treatmentAdvice.getEndDate());
        motechSchedulerService.scheduleJob(adherenceJob);
    }

    public void unscheduleJobForAdherenceTrendFeedback(TreatmentAdvice treatmentAdvice) {
        motechSchedulerService.unscheduleJob(treatmentAdvice.getId());
    }
}