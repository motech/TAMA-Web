package org.motechproject.tama.dailypillreminder.listener;

import org.motechproject.model.MotechEvent;
import org.motechproject.server.event.annotations.MotechListener;
import org.motechproject.server.pillreminder.EventKeys;
import org.motechproject.tama.common.TAMAConstants;
import org.motechproject.tama.dailypillreminder.service.DailyPillReminderAdherenceService;
import org.motechproject.tama.dailypillreminder.service.DailyPillReminderAdherenceTrendService;
import org.motechproject.tama.patient.domain.Patient;
import org.motechproject.tama.patient.repository.AllPatients;
import org.motechproject.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.Properties;

@Component
public class AdherenceQualityListener {

    private DailyPillReminderAdherenceTrendService dailyReminderAdherenceTrendService;
    private Properties properties;
    private DailyPillReminderAdherenceService dailyReminderAdherenceService;
    private AllPatients allPatients;

    @Autowired
    public AdherenceQualityListener(DailyPillReminderAdherenceTrendService dailyReminderAdherenceTrendService, @Qualifier("dailyPillReminderProperties") Properties properties, DailyPillReminderAdherenceService dailyReminderAdherenceService, AllPatients allPatients) {
        this.dailyReminderAdherenceTrendService = dailyReminderAdherenceTrendService;
        this.properties = properties;
        this.dailyReminderAdherenceService = dailyReminderAdherenceService;
        this.allPatients = allPatients;
    }

    @MotechListener(subjects = TAMAConstants.DAILY_ADHERENCE_IN_RED_ALERT_SUBJECT)
    public void determineAdherenceQualityAndRaiseAlert(MotechEvent motechEvent) {
        String patientId = motechEvent.getParameters().get(EventKeys.EXTERNAL_ID_KEY).toString();
        final Patient patient = allPatients.get(patientId);

        if (patient != null && patient.allowAdherenceCalls()) {
            double acceptableAdherencePercentage = Double.parseDouble(properties.getProperty(TAMAConstants.ACCEPTABLE_ADHERENCE_PERCENTAGE));
            double adherencePercentage = dailyReminderAdherenceService.getAdherencePercentage(patientId, DateUtil.now());
            if (adherencePercentage < acceptableAdherencePercentage) {
                dailyReminderAdherenceTrendService.raiseAdherenceInRedAlert(patientId, adherencePercentage);
            }
        }
    }
}
