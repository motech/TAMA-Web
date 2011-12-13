package org.motechproject.tamacallflow.service;

import org.joda.time.DateTime;
import org.motechproject.tamacommon.TAMAConstants;
import org.motechproject.tamacommon.TAMAMessages;
import org.motechproject.tamadomain.domain.PatientAlert;
import org.motechproject.tamadomain.domain.PatientAlertType;
import org.motechproject.tamadomain.repository.AllDosageAdherenceLogs;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class DailyReminderAdherenceTrendService {

    public static final String ADHERENCE_IN_RED_ALERT = "Adherence in Red";
    public static final String FALLING_ADHERENCE = "Falling Adherence";
    private AllDosageAdherenceLogs allDosageAdherenceLogs;
    public DailyReminderAdherenceService dailyReminderAdherenceService;
    private TAMAPillReminderService pillReminderService;

    private PatientAlertService patientAlertService;

    @Autowired
    public DailyReminderAdherenceTrendService(AllDosageAdherenceLogs allDosageAdherenceLogs, TAMAPillReminderService pillReminderService, PatientAlertService alertService, DailyReminderAdherenceService dailyReminderAdherenceService) {
        this.allDosageAdherenceLogs = allDosageAdherenceLogs;
        this.pillReminderService = pillReminderService;
        this.patientAlertService = alertService;
        this.dailyReminderAdherenceService = dailyReminderAdherenceService;
    }

    public boolean isAdherenceFallingAsOf(String patientId, DateTime asOf) {
        return dailyReminderAdherenceService.getAdherence(patientId, asOf) < dailyReminderAdherenceService.getAdherence(patientId, asOf.minusWeeks(1));
    }

    public void raiseAlertIfAdherenceTrendIsFalling(String patientId, DateTime asOf) {
        if (!isAdherenceFallingAsOf(patientId, asOf))
            return;
        Map<String, String> data = new HashMap<String, String>();
        double adherenceAsOfLastWeek = dailyReminderAdherenceService.getAdherence(patientId, asOf.minusWeeks(1));
        double adherenceAsOfCurrentWeek = dailyReminderAdherenceService.getAdherence(patientId, asOf);
        double fallPercent = ((adherenceAsOfLastWeek - adherenceAsOfCurrentWeek) / adherenceAsOfLastWeek) * 100;
        String description = String.format(TAMAMessages.ADHERENCE_FALLING_FROM_TO, fallPercent, adherenceAsOfLastWeek, adherenceAsOfCurrentWeek);
        patientAlertService.createAlert(patientId, TAMAConstants.NO_ALERT_PRIORITY, FALLING_ADHERENCE, description, PatientAlertType.FallingAdherence, data);
    }

    public void raiseAdherenceInRedAlert(String patientId, Double adherencePercentage) {
        String description = String.format(TAMAMessages.ADHERENCE_PERCENTAGE_IS, adherencePercentage);
        Map<String, String> data = new HashMap<String, String>();
        data.put(PatientAlert.ADHERENCE, adherencePercentage.toString());
        patientAlertService.createAlert(patientId, TAMAConstants.NO_ALERT_PRIORITY, ADHERENCE_IN_RED_ALERT, description, PatientAlertType.AdherenceInRed, data);

    }
}
