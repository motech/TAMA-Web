package org.motechproject.tama.listener;

import org.motechproject.model.MotechEvent;
import org.motechproject.server.event.annotations.MotechListener;
import org.motechproject.server.pillreminder.EventKeys;
import org.motechproject.tama.TAMAConstants;
import org.motechproject.tama.service.DailyReminderAdherenceTrendService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.Properties;

@Component
public class AdherenceQualityListener {

    private DailyReminderAdherenceTrendService dailyReminderAdherenceTrendService;
    private Properties properties;

    @Autowired
    public AdherenceQualityListener(DailyReminderAdherenceTrendService dailyReminderAdherenceTrendService, @Qualifier("ivrProperties") Properties properties) {
        this.dailyReminderAdherenceTrendService = dailyReminderAdherenceTrendService;
        this.properties = properties;
    }

    @MotechListener(subjects = TAMAConstants.DETERMINE_ADHERENCE_QUALITY_IN_DAILY_PILL_REMINDER)
    public void determineAdherenceQualityAndRaiseAlert(MotechEvent motechEvent) {
        double acceptableAdherencePercentage = Double.parseDouble(properties.getProperty(TAMAConstants.ACCEPTABLE_ADHERENCE_PERCENTAGE));
        String patientId = motechEvent.getParameters().get(EventKeys.EXTERNAL_ID_KEY).toString();
        double adherencePercentage = dailyReminderAdherenceTrendService.getAdherencePercentage(patientId);
        if(adherencePercentage < acceptableAdherencePercentage) {
            dailyReminderAdherenceTrendService.raiseRedAlert(patientId, adherencePercentage);
        }
    }

}
