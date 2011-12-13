package org.motechproject.tamacallflow.listener;

import org.motechproject.model.MotechEvent;
import org.motechproject.server.event.annotations.MotechListener;
import org.motechproject.server.pillreminder.EventKeys;
import org.motechproject.tamacallflow.service.DailyReminderAdherenceService;
import org.motechproject.tamacommon.TAMAConstants;
import org.motechproject.tamacallflow.service.DailyReminderAdherenceTrendService;
import org.motechproject.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.Properties;

@Component
public class AdherenceQualityListener {

    private DailyReminderAdherenceTrendService dailyReminderAdherenceTrendService;
    private Properties properties;
    private DailyReminderAdherenceService dailyReminderAdherenceService;

    @Autowired
    public AdherenceQualityListener(DailyReminderAdherenceTrendService dailyReminderAdherenceTrendService, @Qualifier("ivrProperties") Properties properties, DailyReminderAdherenceService dailyReminderAdherenceService) {
        this.dailyReminderAdherenceTrendService = dailyReminderAdherenceTrendService;
        this.properties = properties;
        this.dailyReminderAdherenceService = dailyReminderAdherenceService;
    }

    @MotechListener(subjects = TAMAConstants.DAILY_ADHERENCE_IN_RED_ALERT_SUBJECT)
    public void determineAdherenceQualityAndRaiseAlert(MotechEvent motechEvent) {
        double acceptableAdherencePercentage = Double.parseDouble(properties.getProperty(TAMAConstants.ACCEPTABLE_ADHERENCE_PERCENTAGE));
        String patientId = motechEvent.getParameters().get(EventKeys.EXTERNAL_ID_KEY).toString();
        double adherencePercentage = dailyReminderAdherenceService.getAdherenceInPercentage(patientId, DateUtil.now());
        if (adherencePercentage < acceptableAdherencePercentage) {
            dailyReminderAdherenceTrendService.raiseAdherenceInRedAlert(patientId, adherencePercentage);
        }
    }

}
