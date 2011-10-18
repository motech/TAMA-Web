package org.motechproject.tama.platform.service;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.motechproject.model.*;
import org.motechproject.scheduler.MotechSchedulerService;
import org.motechproject.scheduler.builder.CronJobSimpleExpressionBuilder;
import org.motechproject.scheduler.builder.WeeklyCronJobExpressionBuilder;
import org.motechproject.server.pillreminder.builder.SchedulerPayloadBuilder;
import org.motechproject.tama.TAMAConstants;
import org.motechproject.tama.domain.Patient;
import org.motechproject.tama.domain.TimeOfDay;
import org.motechproject.tama.domain.TreatmentAdvice;
import org.motechproject.tama.repository.AllPatients;
import org.motechproject.tama.util.UUIDUtil;
import org.motechproject.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Map;
import java.util.Properties;

@Component
public class TamaSchedulerService {
    private static final String OUTBOX_CALL_JOB_ID_PREFIX = "outbox-";
	@Autowired
    private MotechSchedulerService motechSchedulerService;
    @Qualifier("ivrProperties")
    @Autowired
    private Properties properties;
    @Autowired
    private AllPatients allPatients;

    private String FOUR_DAY_RECALL_JOB_ID_PREFIX = "four_day_recall_";

    public TamaSchedulerService() {
    }

    public TamaSchedulerService(MotechSchedulerService motechSchedulerService, Properties properties, AllPatients allPatients) {
        this.motechSchedulerService = motechSchedulerService;
        this.properties = properties;
        this.allPatients = allPatients;
    }

    public void scheduleJobsForFourDayRecall(Patient patient, TreatmentAdvice treatmentAdvice) {
        String patientDocId = patient.getId();
        LocalDate treatmentAdviceStartDate = DateUtil.newDate(treatmentAdvice.getStartDate());
        LocalDate endDate = DateUtil.newDate(treatmentAdvice.getEndDate());
        DayOfWeek dayOfWeeklyCall = patient.getPatientPreferences().getDayOfWeeklyCall();
        Time callTime = patient.getPatientPreferences().getBestCallTime().toTime();
        Integer daysToRetry = Integer.valueOf(properties.getProperty(TAMAConstants.FOUR_DAY_RECALL_DAYS_TO_RETRY));

        for (int count = 0; count <= daysToRetry; count++) {
            Map<String, Object> eventParams = new FourDayRecallEventPayloadBuilder()
                    .withJobId(FOUR_DAY_RECALL_JOB_ID_PREFIX + count + patientDocId)
                    .withPatientDocId(patientDocId)
                    .withTreatmentAdviceId(treatmentAdvice.getId())
                    .withTreatmentAdviceStartDate(treatmentAdviceStartDate)
                    .payload();
            MotechEvent fourDayRecallEvent = new MotechEvent(TAMAConstants.FOUR_DAY_RECALL_SUBJECT, eventParams);
            String cronExpression = new WeeklyCronJobExpressionBuilder(dayOfWeek(dayOfWeeklyCall, count)).withTime(callTime).build();
            Date jobEndDate = endDate == null ? null : endDate.toDate();

            LocalDate startDate = treatmentAdviceStartDate.plusDays(4 + count);
            Date jobStartDate = getJobStartDate(startDate);
            CronSchedulableJob cronJobForForDayRecall = new CronSchedulableJob(fourDayRecallEvent, cronExpression, jobStartDate, jobEndDate);
            motechSchedulerService.scheduleJob(cronJobForForDayRecall);
        }
    }

    private DayOfWeek dayOfWeek(DayOfWeek dayOfWeek, int count) {
        int dayOfWeekNum = (dayOfWeek.getValue() + count) % 7;
        dayOfWeekNum = (dayOfWeekNum == 0) ? 7 : dayOfWeekNum;
        return DayOfWeek.getDayOfWeek(dayOfWeekNum);
    }

    public void scheduleRepeatingJobsForFourDayRecall(String patientDocId, String treatmentAdviceId, LocalDate treatmentAdviceStartDate) {
        Patient patient = allPatients.get(patientDocId);
        Integer maxOutboundRetries = Integer.valueOf(properties.getProperty(TAMAConstants.RETRIES_PER_DAY));
        int repeatIntervalInMinutes = Integer.valueOf(properties.getProperty(TAMAConstants.RETRY_INTERVAL));

        TimeOfDay callTime = patient.getPatientPreferences().getBestCallTime();
        DateTime todayCallTime = DateUtil.now().withHourOfDay(callTime.toTime().getHour()).withMinuteOfHour(callTime.toTime().getMinute());
        DateTime jobStartTime = todayCallTime.plusMinutes(repeatIntervalInMinutes);
        DateTime jobEndTime = jobStartTime.plusDays(1);

        Map<String, Object> eventParams = new FourDayRecallEventPayloadBuilder()
                .withJobId(FOUR_DAY_RECALL_JOB_ID_PREFIX + UUIDUtil.newUUID())
                .withPatientDocId(patientDocId)
                .withTreatmentAdviceId(treatmentAdviceId)
                .withTreatmentAdviceStartDate(treatmentAdviceStartDate)
                .withRetryFlag(true)
                .payload();
        MotechEvent fourDayRecallRepeatingEvent = new MotechEvent(TAMAConstants.FOUR_DAY_RECALL_SUBJECT, eventParams);
        RepeatingSchedulableJob repeatingSchedulableJob = new RepeatingSchedulableJob(fourDayRecallRepeatingEvent, jobStartTime.toDate(), jobEndTime.toDate(), maxOutboundRetries - 1, repeatIntervalInMinutes * 60 * 1000);
        motechSchedulerService.scheduleRepeatingJob(repeatingSchedulableJob);
    }

    public void scheduleJobForAdherenceTrendFeedback(TreatmentAdvice treatmentAdvice) {
        Map<String, Object> eventParams = new SchedulerPayloadBuilder().withJobId(treatmentAdvice.getId())
                .withExternalId(treatmentAdvice.getPatientId())
                .payload();
        MotechEvent adherenceWeeklyTrendEvent = new MotechEvent(TAMAConstants.ADHERENCE_WEEKLY_TREND_SCHEDULER_SUBJECT, eventParams);
        LocalDate startDate = DateUtil.newDate(treatmentAdvice.getStartDate()).plusWeeks(5);
        String cronExpression = new WeeklyCronJobExpressionBuilder(DayOfWeek.getDayOfWeek(startDate.getDayOfWeek())).build();
        Date jobStartDate = getJobStartDate(startDate);
        CronSchedulableJob adherenceJob = new CronSchedulableJob(adherenceWeeklyTrendEvent, cronExpression, jobStartDate, treatmentAdvice.getEndDate());
        motechSchedulerService.scheduleJob(adherenceJob);
    }

	private Date getJobStartDate(LocalDate startDate) {
		Date jobStartDate = DateUtil.newDateTime(startDate.toDate()).isBefore(DateUtil.now()) ? DateUtil.now().toDate() : startDate.toDate();
		return jobStartDate;
	}

    public void unscheduleJobForAdherenceTrendFeedback(TreatmentAdvice treatmentAdvice) {
        motechSchedulerService.unscheduleJob(treatmentAdvice.getId());
    }

	public void scheduleJobForOutboxCall(Patient patient) {
		String outboxCallJobCronExpression = new CronJobSimpleExpressionBuilder(patient.getPatientPreferences().getBestCallTime().toTime()).build();
		Map<String, Object> eventParams = new SchedulerPayloadBuilder().withJobId(OUTBOX_CALL_JOB_ID_PREFIX + patient.getId())
                .withExternalId(patient.getId())
                .payload();
        MotechEvent outboxCallEvent = new MotechEvent(TAMAConstants.OUTBOX_CALL_SCHEDULER_SUBJECT, eventParams);
		CronSchedulableJob outboxCallJob = new CronSchedulableJob(outboxCallEvent, outboxCallJobCronExpression, DateUtil.now().toDate(), null);
		motechSchedulerService.scheduleJob(outboxCallJob);		
	}
}