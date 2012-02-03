package org.motechproject.tama.outbox.service;

import org.motechproject.model.CronSchedulableJob;
import org.motechproject.model.MotechEvent;
import org.motechproject.model.RepeatingSchedulableJob;
import org.motechproject.outbox.api.EventKeys;
import org.motechproject.scheduler.MotechSchedulerService;
import org.motechproject.scheduler.builder.CronJobSimpleExpressionBuilder;
import org.motechproject.tama.common.TAMAConstants;
import org.motechproject.tama.patient.domain.Patient;
import org.motechproject.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

@Component
public class OutboxSchedulerService {
    public static final String IS_RETRY = "isRetry";
    public static final String EXTERNAL_ID_KEY = "ExternalID";
    private MotechSchedulerService motechSchedulerService;
    private Properties properties;

    @Autowired
    public OutboxSchedulerService(MotechSchedulerService motechSchedulerService, @Qualifier("ivrProperties") Properties properties) {
        this.motechSchedulerService = motechSchedulerService;
        this.properties = properties;
    }

    public void scheduleOutboxJobs(Patient patient) {
        String outboxCallJobCronExpression = new CronJobSimpleExpressionBuilder(patient.getPatientPreferences().getBestCallTime().toTime()).build();
        Map<String, Object> eventParams = new HashMap<String, Object>();
        eventParams.put(EventKeys.SCHEDULE_JOB_ID_KEY, patient.getId());
        eventParams.put(EXTERNAL_ID_KEY, patient.getId());
        MotechEvent outboxCallEvent = new MotechEvent(TAMAConstants.OUTBOX_CALL_SCHEDULER_SUBJECT, eventParams);
        CronSchedulableJob outboxCallJob = new CronSchedulableJob(outboxCallEvent, outboxCallJobCronExpression, DateUtil.now().toDate(), null);
        motechSchedulerService.safeScheduleJob(outboxCallJob);
    }

    public void unscheduleOutboxJobs(Patient patient) {
        unscheduleJobForOutboxCall(patient);
        unscheduleRepeatingJobForOutboxCall(patient.getId());
    }

    public void scheduleRepeatingJobForOutBoxCall(Patient patient) {
        Map<String, Object> eventParams = new HashMap<String, Object>();
        eventParams.put(EventKeys.SCHEDULE_JOB_ID_KEY, patient.getId());
        eventParams.put(EXTERNAL_ID_KEY, patient.getId());
        eventParams.put(IS_RETRY, "true");
        MotechEvent outboxCallEvent = new MotechEvent(TAMAConstants.OUTBOX_CALL_SCHEDULER_SUBJECT, eventParams);
        Integer maxOutboundRetries = Integer.valueOf(properties.getProperty(TAMAConstants.RETRIES_PER_DAY)) - 1;
        int repeatIntervalInMinutes = Integer.valueOf(properties.getProperty(TAMAConstants.OUT_BOX_CALL_RETRY_INTERVAL));
        RepeatingSchedulableJob outboxCallJob = new RepeatingSchedulableJob(outboxCallEvent, DateUtil.now().plusMinutes(repeatIntervalInMinutes).toDate(), DateUtil.today().plusDays(1).toDate(), maxOutboundRetries, repeatIntervalInMinutes * 60 * 1000);
        motechSchedulerService.scheduleRepeatingJob(outboxCallJob);
    }

    public void unscheduleRepeatingJobForOutboxCall(String externalId) {
        motechSchedulerService.unscheduleRepeatingJob(TAMAConstants.OUTBOX_CALL_SCHEDULER_SUBJECT, externalId);
    }

    private void unscheduleJobForOutboxCall(Patient patient) {
        motechSchedulerService.unscheduleJob(TAMAConstants.OUTBOX_CALL_SCHEDULER_SUBJECT, patient.getId());
    }
}
