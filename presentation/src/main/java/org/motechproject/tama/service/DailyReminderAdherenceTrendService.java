package org.motechproject.tama.service;

import org.joda.time.DateTime;
import org.motechproject.server.pillreminder.contract.PillRegimenResponse;
import org.motechproject.server.pillreminder.service.PillReminderService;
import org.motechproject.tama.TAMAConstants;
import org.motechproject.tama.domain.PatientAlert;
import org.motechproject.tama.domain.PatientAlertType;
import org.motechproject.tama.repository.AllDosageAdherenceLogs;
import org.motechproject.tama.util.DosageUtil;
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

    private PillReminderService pillReminderService;

    private PatientAlertService patientAlertService;

    @Autowired
    public DailyReminderAdherenceTrendService(AllDosageAdherenceLogs allDosageAdherenceLogs, PillReminderService pillReminderService, PatientAlertService alertService) {
        this.allDosageAdherenceLogs = allDosageAdherenceLogs;
        this.pillReminderService = pillReminderService;
        this.patientAlertService = alertService;
    }

    public boolean isAdherenceFalling(String patientId) {
        return getAdherencePercentageForCurrentWeek(patientId) < getAdherencePercentageForLastWeek(patientId);
    }

    public double getAdherencePercentage(String patientId) {
        return getAdherencePercentageForCurrentWeek(patientId);
    }

    protected double getAdherencePercentageForCurrentWeek(String patientId) {
        return getAdherencePercentage(patientId, DateUtil.now());
    }

    protected double getAdherencePercentageForLastWeek(String patientId) {
        return getAdherencePercentage(patientId, DateUtil.now().minusWeeks(1));
    }

    protected double getAdherencePercentage(String patientId, DateTime asOfDate) {
        PillRegimenResponse pillRegimen = pillReminderService.getPillRegimen(patientId);
        String regimenId = pillRegimen.getPillRegimenId();
        int scheduledDosagesTotalCountForLastFourWeeksAsOfNow = DosageUtil.getScheduledDosagesTotalCountForLastFourWeeks(asOfDate.minusWeeks(4), asOfDate, pillRegimen);
        int dosagesTakenForLastFourWeeksAsOfNow = allDosageAdherenceLogs.findScheduledDosagesSuccessCount(regimenId,
                asOfDate.minusWeeks(4).toLocalDate(), asOfDate.toLocalDate());
        return ((double) dosagesTakenForLastFourWeeksAsOfNow) / scheduledDosagesTotalCountForLastFourWeeksAsOfNow;
    }

    public void raiseAdherenceFallingAlert(String patientId) {
        if (!isAdherenceFalling(patientId)) return;
        final Map<String, String> data = new HashMap<String, String>();
        final double adherencePercentageForLastWeek =  getAdherencePercentageForLastWeek(patientId);
        final double adherencePercentageForCurrentWeek =  getAdherencePercentageForCurrentWeek(patientId);
        final double fallPercent =  ((adherencePercentageForLastWeek - adherencePercentageForCurrentWeek)/adherencePercentageForLastWeek)*100;
        final String description = String.format("Adherence fell by %2.2f%%, from %2.2f%% to %2.2f%%",fallPercent, adherencePercentageForLastWeek, adherencePercentageForCurrentWeek);
        patientAlertService.createAlert(patientId, TAMAConstants.NO_ALERT_PRIORITY, FALLING_ADHERENCE, description, PatientAlertType.FallingAdherence, data);
    }

    public void raiseRedAlert(String patientId, Double adherencePercentage) {
        String description = String.format("Adherence percentage is %.2f%%", adherencePercentage);
        Map<String, String> data = new HashMap<String, String>();
        data.put(PatientAlert.ADHERENCE, adherencePercentage.toString());
        patientAlertService.createAlert(patientId, TAMAConstants.NO_ALERT_PRIORITY, ADHERENCE_IN_RED_ALERT, description, PatientAlertType.AdherenceInRed, data);

    }
}
