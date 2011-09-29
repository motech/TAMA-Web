package org.motechproject.tama.service;

import org.joda.time.LocalDate;
import org.motechproject.model.CronSchedulableJob;
import org.motechproject.model.DayOfWeek;
import org.motechproject.model.MotechEvent;
import org.motechproject.model.Time;
import org.motechproject.scheduler.MotechSchedulerService;
import org.motechproject.scheduler.builder.WeeklyCronJobExpressionBuilder;
import org.motechproject.server.pillreminder.builder.SchedulerPayloadBuilder;
import org.motechproject.tama.TAMAConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class SchedulerService {
    private MotechSchedulerService motechSchedulerService;

    @Autowired
    public SchedulerService(MotechSchedulerService motechSchedulerService) {
        this.motechSchedulerService = motechSchedulerService;
    }

    public void scheduleJobsForFourDayRecall(String jobId, String externalId, LocalDate startDate, DayOfWeek dayOfWeek, Time callTime) {
        Map<String, Object> eventParams = new SchedulerPayloadBuilder()
                .withJobId(jobId)
                .withExternalId(externalId)
                .payload();
        MotechEvent fourDayRecallEvent = new MotechEvent(TAMAConstants.FOUR_DAY_RECALL_SUBJECT, eventParams);
        String cronExpression = new WeeklyCronJobExpressionBuilder(dayOfWeek).withTime(callTime).build();
        CronSchedulableJob cronJobForForDayRecall = new CronSchedulableJob(fourDayRecallEvent, cronExpression, startDate.toDate(), null);
        motechSchedulerService.scheduleJob(cronJobForForDayRecall);
    }
}