package org.motechproject.tama.service;

import org.joda.time.DateTime;
import org.motechproject.server.pillreminder.contract.PillRegimenResponse;
import org.motechproject.server.pillreminder.service.PillReminderService;
import org.motechproject.tama.domain.PatientAlertType;
import org.motechproject.tama.repository.AllDosageAdherenceLogs;
import org.motechproject.tama.util.DosageUtil;
import org.motechproject.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DailyReminderAdherenceTrendService {

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

    private double getAdherencePercentage(String patientId, DateTime asOfDate) {
        PillRegimenResponse pillRegimen = pillReminderService.getPillRegimen(patientId);
        String regimenId = pillRegimen.getPillRegimenId();
        int scheduledDosagesTotalCountForLastFourWeeksAsOfNow = getScheduledDosagesTotalCount(pillRegimen, asOfDate.minusWeeks(4), asOfDate);
        int dosagesTakenForLastFourWeeksAsOfNow = allDosageAdherenceLogs.findScheduledDosagesSuccessCount(regimenId,
                asOfDate.minusWeeks(4).toLocalDate(), asOfDate.toLocalDate());
        return ((double) dosagesTakenForLastFourWeeksAsOfNow) / scheduledDosagesTotalCountForLastFourWeeksAsOfNow;
    }

    private int getScheduledDosagesTotalCount(PillRegimenResponse pillRegimen, DateTime startDate, DateTime endDate) {
        return DosageUtil.getScheduledDosagesTotalCount(startDate, endDate, pillRegimen);
    }

    public void raiseAdherenceFallingAlert(String patientId) {
        if (!isAdherenceFalling(patientId)) return;
        patientAlertService.createAlert(patientId,3,"Falling Adherence", "Falling Adherence", PatientAlertType.FallingAdherence);
    }

}
