package org.motechproject.tamacallflow.listener;

import org.motechproject.model.MotechEvent;
import org.motechproject.server.event.annotations.MotechListener;
import org.motechproject.server.pillreminder.EventKeys;
import org.motechproject.tamacallflow.service.DailyReminderAdherenceService;
import org.motechproject.tamacallflow.service.DailyReminderAdherenceTrendService;
import org.motechproject.tamacommon.TAMAConstants;
import org.motechproject.tamadomain.domain.Patient;
import org.motechproject.tamadomain.repository.AllPatients;
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
    private AllPatients allPatients;

    @Autowired
    public AdherenceQualityListener(DailyReminderAdherenceTrendService dailyReminderAdherenceTrendService, @Qualifier("ivrProperties") Properties properties, DailyReminderAdherenceService dailyReminderAdherenceService, AllPatients allPatients) {
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
            double adherencePercentage = dailyReminderAdherenceService.getAdherenceInPercentage(patientId, DateUtil.now());
            if (adherencePercentage < acceptableAdherencePercentage) {
                dailyReminderAdherenceTrendService.raiseAdherenceInRedAlert(patientId, adherencePercentage);
            }
        }
    }

}
