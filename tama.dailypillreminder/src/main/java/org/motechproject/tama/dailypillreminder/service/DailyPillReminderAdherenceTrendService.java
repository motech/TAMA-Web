package org.motechproject.tama.dailypillreminder.service;

import org.joda.time.DateTime;
import org.motechproject.tama.common.TAMAConstants;
import org.motechproject.tama.common.TAMAMessages;
import org.motechproject.tama.patient.domain.PatientAlert;
import org.motechproject.tama.patient.domain.PatientAlertType;
import org.motechproject.tama.patient.service.PatientAlertService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class DailyPillReminderAdherenceTrendService {
    public DailyPillReminderAdherenceService dailyReminderAdherenceService;

    private PatientAlertService patientAlertService;

    @Autowired
    public DailyPillReminderAdherenceTrendService(PatientAlertService alertService, DailyPillReminderAdherenceService dailyReminderAdherenceService) {
        this.patientAlertService = alertService;
        this.dailyReminderAdherenceService = dailyReminderAdherenceService;
    }

    public boolean isAdherenceFallingAsOf(String patientId, DateTime asOf) {
        return dailyReminderAdherenceService.getAdherenceInPercentage(patientId, asOf) < dailyReminderAdherenceService.getAdherenceInPercentage(patientId, asOf.minusWeeks(1));
    }

    public void raiseAlertIfAdherenceTrendIsFalling(String patientId, DateTime asOf) {
        if (!isAdherenceFallingAsOf(patientId, asOf))
            return;
        Map<String, String> data = new HashMap<String, String>();
        double adherencePercentageAsOfLastWeek = dailyReminderAdherenceService.getAdherenceInPercentage(patientId, asOf.minusWeeks(1));
        double adherencePercentageAsOfCurrentWeek = dailyReminderAdherenceService.getAdherenceInPercentage(patientId, asOf);
        double fallPercent = ((adherencePercentageAsOfLastWeek - adherencePercentageAsOfCurrentWeek) / adherencePercentageAsOfLastWeek) * 100;
        String description = String.format(TAMAMessages.ADHERENCE_FALLING_FROM_TO, fallPercent, adherencePercentageAsOfLastWeek, adherencePercentageAsOfCurrentWeek);
        patientAlertService.createAlert(patientId, TAMAConstants.NO_ALERT_PRIORITY, TAMAConstants.FALLING_ADHERENCE, description, PatientAlertType.FallingAdherence, data);
    }

    public void raiseAdherenceInRedAlert(String patientId, Double adherencePercentage) {
        String description = String.format(TAMAMessages.ADHERENCE_PERCENTAGE_IS, adherencePercentage);
        Map<String, String> data = new HashMap<String, String>();
        data.put(PatientAlert.ADHERENCE, adherencePercentage.toString());
        patientAlertService.createAlert(patientId, TAMAConstants.NO_ALERT_PRIORITY, TAMAConstants.ADHERENCE_IN_RED_ALERT, description, PatientAlertType.AdherenceInRed, data);
    }
}
