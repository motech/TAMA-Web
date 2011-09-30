package org.motechproject.tama.service;

import org.joda.time.LocalDate;
import org.motechproject.model.*;
import org.motechproject.scheduler.MotechSchedulerService;
import org.motechproject.scheduler.builder.WeeklyCronJobExpressionBuilder;
import org.motechproject.server.pillreminder.builder.SchedulerPayloadBuilder;
import org.motechproject.server.pillreminder.service.PillReminderService;
import org.motechproject.tama.TAMAConstants;
import org.motechproject.tama.domain.CallPreference;
import org.motechproject.tama.domain.Patient;
import org.motechproject.tama.domain.PatientPreferences;
import org.motechproject.tama.domain.TreatmentAdvice;
import org.motechproject.tama.mapper.PillRegimenRequestMapper;
import org.motechproject.tama.repository.AllPatients;
import org.motechproject.tama.util.UUIDUtil;
import org.motechproject.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Properties;

@Service
public class SchedulerService {
    private AllPatients allPatients;
    private MotechSchedulerService motechSchedulerService;
    private PillReminderService pillReminderService;
    private PillRegimenRequestMapper pillRegimenRequestMapper;
    private String FOUR_DAY_RECALL_JOB_ID_PREFIX = "four_day_recall_";

    @Autowired
    @Qualifier("ivrProperties")
    private Properties properties;

    @Autowired
    public SchedulerService(MotechSchedulerService motechSchedulerService, PillReminderService pillReminderService, AllPatients allPatients, PillRegimenRequestMapper pillRegimenRequestMapper, Properties properties) {
        this.motechSchedulerService = motechSchedulerService;
        this.pillReminderService = pillReminderService;
        this.allPatients = allPatients;
        this.pillRegimenRequestMapper = pillRegimenRequestMapper;
        this.properties = properties;
    }

    public void scheduleJobsForTreatmentAdviceCalls(TreatmentAdvice treatmentAdvice) {
        Patient patient = allPatients.get(treatmentAdvice.getPatientId());
        PatientPreferences patientPreferences = patient.getPatientPreferences();
        if (patientPreferences.getCallPreference().equals(CallPreference.FourDayRecall)) {
            scheduleJobsForFourDayRecall(patient.getId(), new LocalDate(treatmentAdvice.getStartDate()).plusDays(4), new LocalDate(treatmentAdvice.getEndDate()), patientPreferences.getDayOfWeeklyCall(), patientPreferences.getBestCallTime().toTime());
        } else {
            pillReminderService.createNew(pillRegimenRequestMapper.map(treatmentAdvice));
            scheduleJobForAdherenceTrendFeedback(treatmentAdvice);
        }
    }

    private void scheduleJobsForFourDayRecall(String patientId, LocalDate startDate, LocalDate endDate, DayOfWeek dayOfWeek, Time callTime) {
        Map<String, Object> eventParams = new SchedulerPayloadBuilder()
                .withJobId(FOUR_DAY_RECALL_JOB_ID_PREFIX + patientId)
                .withExternalId(patientId)
                .payload();
        MotechEvent fourDayRecallEvent = new MotechEvent(TAMAConstants.FOUR_DAY_RECALL_SUBJECT, eventParams);
        String cronExpression = new WeeklyCronJobExpressionBuilder(dayOfWeek).withTime(callTime).build();
        CronSchedulableJob cronJobForForDayRecall = new CronSchedulableJob(fourDayRecallEvent, cronExpression, startDate.toDate(), endDate.toDate());
        motechSchedulerService.scheduleJob(cronJobForForDayRecall);

        scheduleRepeatingJobsForFourDayRecall(patientId, startDate, endDate);
    }

    private void scheduleRepeatingJobsForFourDayRecall(String patientId, LocalDate startDate, LocalDate endDate) {
        Integer maxOutboundRetries = Integer.valueOf(properties.getProperty(TAMAConstants.MAX_OUTBOUND_RETRIES));
        Integer retryInterval = Integer.valueOf(properties.getProperty(TAMAConstants.RETRY_INTERVAL));

        Map<String, Object> eventParams = new SchedulerPayloadBuilder()
                .withJobId(FOUR_DAY_RECALL_JOB_ID_PREFIX + UUIDUtil.newUUID())
                .withExternalId(patientId)
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