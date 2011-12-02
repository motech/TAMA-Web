package org.motechproject.tamacallflow.service;

import org.joda.time.DateTime;
import org.motechproject.tamacommon.TAMAConstants;
import org.motechproject.tamadomain.domain.DosageStatus;
import org.motechproject.tamadomain.domain.PatientAlert;
import org.motechproject.tamadomain.domain.PatientAlertType;
import org.motechproject.tamadomain.repository.AllDosageAdherenceLogs;
import org.motechproject.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class DailyReminderAdherenceTrendService {

    public static final String ADHERENCE_IN_RED_ALERT = "Adherence in Red";
    public static final String FALLING_ADHERENCE = "Falling Adherence";
    private AllDosageAdherenceLogs allDosageAdherenceLogs;

    private TAMAPillReminderService pillReminderService;

    private PatientAlertService patientAlertService;

    @Autowired
    public DailyReminderAdherenceTrendService(AllDosageAdherenceLogs allDosageAdherenceLogs, TAMAPillReminderService pillReminderService, PatientAlertService alertService) {
        this.allDosageAdherenceLogs = allDosageAdherenceLogs;
        this.pillReminderService = pillReminderService;
        this.patientAlertService = alertService;
    }

    public boolean isAdherenceFalling(String patientId) {
        DateTime now = DateUtil.now();
        return getAdherenceAsOf(patientId, now) < getAdherenceAsOf(patientId, now.minusWeeks(1));
    }

    public double getAdherence(String patientId) {
        return getAdherenceAsOf(patientId, DateUtil.now());
    }

    protected double getAdherenceAsOf(String patientId, DateTime asOfDate) {
        int totalDosages = pillReminderService.getPillRegimen(patientId).getNumberOfDosagesBetween(asOfDate.minusWeeks(4), asOfDate);
        int dosagesTakenForLastFourWeeks =  allDosageAdherenceLogs.findByStatusAndDateRange(DosageStatus.TAKEN, asOfDate.minusWeeks(4).toLocalDate(), asOfDate.toLocalDate()).size();
        return ((double) dosagesTakenForLastFourWeeks) / totalDosages;
    }

    public void raiseAlertIfAdherenceTrendIsFalling(String patientId) {
        if (!isAdherenceFalling(patientId))
            return;
        Map<String, String> data = new HashMap<String, String>();
        double adherenceAsOfLastWeek = getAdherenceAsOf(patientId, DateUtil.now().minusWeeks(1));
        double adherenceAsOfCurrentWeek = getAdherence(patientId);
        double fallPercent = ((adherenceAsOfLastWeek - adherenceAsOfCurrentWeek) / adherenceAsOfLastWeek) * 100;
        String description = String.format("Adherence fell by %2.2f%%, from %2.2f%% to %2.2f%%", fallPercent, adherenceAsOfLastWeek, adherenceAsOfCurrentWeek);
        patientAlertService.createAlert(patientId, TAMAConstants.NO_ALERT_PRIORITY, FALLING_ADHERENCE, description, PatientAlertType.FallingAdherence, data);
    }

    public void raiseAdherenceInRedAlert(String patientId, Double adherencePercentage) {
        String description = String.format("Adherence percentage is %.2f%%", adherencePercentage);
        Map<String, String> data = new HashMap<String, String>();
        data.put(PatientAlert.ADHERENCE, adherencePercentage.toString());
        patientAlertService.createAlert(patientId, TAMAConstants.NO_ALERT_PRIORITY, ADHERENCE_IN_RED_ALERT, description, PatientAlertType.AdherenceInRed, data);

    }
}
