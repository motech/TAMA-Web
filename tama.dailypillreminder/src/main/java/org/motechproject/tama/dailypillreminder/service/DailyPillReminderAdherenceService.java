package org.motechproject.tama.dailypillreminder.service;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.motechproject.server.pillreminder.contract.DosageResponse;
import org.motechproject.tama.common.NoAdherenceRecordedException;
import org.motechproject.tama.common.TAMAConstants;
import org.motechproject.tama.dailypillreminder.domain.*;
import org.motechproject.tama.dailypillreminder.repository.AllDosageAdherenceLogs;
import org.motechproject.tama.ivr.service.AdherenceService;
import org.motechproject.tama.ivr.service.AdherenceServiceStrategy;
import org.motechproject.tama.patient.domain.CallPreference;
import org.motechproject.tama.patient.domain.Patient;
import org.motechproject.tama.patient.repository.AllPatients;
import org.motechproject.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Properties;

@Service
public class DailyPillReminderAdherenceService implements AdherenceServiceStrategy {
    private AllDosageAdherenceLogs allDosageAdherenceLogs;
    private DailyPillReminderService dailyPillReminderService;
    private Properties properties;
    private AllPatients allPatients;

    @Autowired
    public DailyPillReminderAdherenceService(AllDosageAdherenceLogs allDosageAdherenceLogs, DailyPillReminderService dailyPillReminderService, @Qualifier("dailyPillReminderProperties") Properties properties, AdherenceService adherenceService, AllPatients allPatients) {
        this.allDosageAdherenceLogs = allDosageAdherenceLogs;
        this.dailyPillReminderService = dailyPillReminderService;
        this.properties = properties;
        this.allPatients = allPatients;
        adherenceService.register(CallPreference.DailyPillReminder, this);
    }

    public double getAdherencePercentage(String patientId, DateTime asOfDate) throws NoAdherenceRecordedException {
        Patient patient = allPatients.get(patientId);
        DateTime fromDate = asOfDate.minusWeeks(4);
        DateTime callPreferenceTransitionDate = patient.getPatientPreferences().getCallPreferenceTransitionDate();
        if (callPreferenceTransitionDate != null) {
            fromDate = asOfDate.minusWeeks(4).isBefore(callPreferenceTransitionDate) ? callPreferenceTransitionDate : asOfDate.minusWeeks(4);
        }
        return getAdherencePercentage(patientId, fromDate.toLocalDate(), asOfDate);
    }

    private double getAdherencePercentage(String patientId, LocalDate fromDate, DateTime toDate) throws NoAdherenceRecordedException {
        PillRegimen pillRegimen = dailyPillReminderService.getPillRegimen(patientId);
        String pillRegimenId = pillRegimen.getId();
        int totalLogs = allDosageAdherenceLogs.countByDosageDate(pillRegimenId, fromDate, toDate.toLocalDate());
        if (totalLogs == 0) {
            throw new NoAdherenceRecordedException("No Adherence Log was recorded for given date range");
        }
        int dosagesTakenForLastFourWeeks = allDosageAdherenceLogs.countByDosageStatusAndDate(pillRegimenId, DosageStatus.TAKEN, fromDate, toDate.toLocalDate());
        return ((double) dosagesTakenForLastFourWeeks) * 100 / totalLogs;
    }

    @Override
    public boolean wasAnyDoseMissedLastWeek(Patient patient) {
        LocalDate oneWeekAgo = DateUtil.now().minusWeeks(1).toLocalDate();
        DateTime yesterday = DateUtil.newDateTime(DateUtil.today().minusDays(1), 23, 59, 59);
        PillRegimen pillRegimen = dailyPillReminderService.getPillRegimen(patient.getId());
        String pillRegimenId = pillRegimen.getId();
        int totalLogs = allDosageAdherenceLogs.countByDosageDate(pillRegimenId, oneWeekAgo, yesterday.toLocalDate());
        int dosagesTaken = allDosageAdherenceLogs.countByDosageStatusAndDate(pillRegimenId, DosageStatus.TAKEN, oneWeekAgo, yesterday.toLocalDate());
        return totalLogs == 0 ? false : totalLogs - dosagesTaken > 0;
    }

    @Override
    public boolean wasAnyDoseTakenLateLastWeek(Patient patient) {
        PillRegimen pillRegimen = dailyPillReminderService.getPillRegimen(patient.getId());
        String pillRegimenId = pillRegimen.getId();

        return allDosageAdherenceLogs.getDoseTakenLateCount(pillRegimenId, DateUtil.today().minusDays(6), true) > 0;
    }

    public void backFillAdherence(String patientId, DateTime startDate, DateTime endDate, boolean wasDoseTaken) {
        DosageStatus dosageStatus = wasDoseTaken ? DosageStatus.TAKEN : DosageStatus.NOT_TAKEN;
        backFillAdherence(patientId, startDate, endDate, dosageStatus);
    }

    public void backFillAdherence(String patientId, DateTime startDate, DateTime endDate, DosageStatus dosageStatus) {
        PillRegimen pillRegimen = dailyPillReminderService.getPillRegimen(patientId);

        Dose firstProbableDose = pillRegimen.getDoseAt(startDate);
        if (firstProbableDose != null && firstProbableDose.getDoseTime().isBefore(startDate)) {
            startDate = firstProbableDose.getDoseTime();
        }

        List<DosageResponse> dosageResponses = pillRegimen.getDosageResponses();
        for (DosageResponse dosageResponse : dosageResponses) {
            DosageTimeLine dosageTimeLine = new DosageTimeLine(dosageResponse, startDate, endDate);
            while (dosageTimeLine.hasNext()) {
                Dose dose = dosageTimeLine.next();
                DosageAdherenceLog dosageAdherenceLog = allDosageAdherenceLogs.findByDosageIdAndDate(dose.getDosageId(), dose.getDate());
                if (dosageAdherenceLog == null || dosageAdherenceLog.getDosageStatus().wasNotReported()) {
                    recordDosageAdherenceAsCaptured(patientId, pillRegimen.getId(), dose, dosageStatus, dose.getDoseTime());
                }
            }
        }
    }

    public void recordDosageAdherenceAsCaptured(String patientId, String regimenId, Dose dose, DosageStatus dosageStatus, DateTime doseTakenTime) {
        recordAdherence(patientId, regimenId, dose, dosageStatus, doseTakenTime);
        dailyPillReminderService.setLastCapturedDate(regimenId, dose.getDosageId(), dose.getDate());
    }

    public void recordDosageAdherenceAsNotCaptured(String patientId, String regimenId, Dose dose, DosageStatus dosageStatus, DateTime doseTakenTime) {
        recordAdherence(patientId, regimenId, dose, dosageStatus, doseTakenTime);
    }

    private void recordAdherence(String patientId, String regimenId, Dose dose, DosageStatus status, DateTime doseTakenTime) {
        final int dosageInterval = Integer.parseInt(properties.getProperty(TAMAConstants.DOSAGE_INTERVAL));

        DosageAdherenceLog existingLog = allDosageAdherenceLogs.findByDosageIdAndDate(dose.getDosageId(), dose.getDate());
        if (existingLog == null) {
            allDosageAdherenceLogs.add(DosageAdherenceLog.create(patientId, regimenId, status, dose, doseTakenTime, dosageInterval));
        } else {
            existingLog.updateStatus(status, doseTakenTime, dosageInterval, dose);
            allDosageAdherenceLogs.update(existingLog);
        }
    }
}