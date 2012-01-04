package org.motechproject.tama.dailypillreminder.service;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.motechproject.server.pillreminder.contract.DosageResponse;
import org.motechproject.tama.common.TAMAConstants;
import org.motechproject.tama.dailypillreminder.domain.*;
import org.motechproject.tama.dailypillreminder.repository.AllDosageAdherenceLogs;
import org.motechproject.tama.ivr.service.AdherenceService;
import org.motechproject.tama.ivr.service.AdherenceServiceStrategy;
import org.motechproject.tama.patient.domain.CallPreference;
import org.motechproject.tama.patient.domain.Patient;
import org.motechproject.tama.patient.domain.TreatmentAdvice;
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
    private TAMAPillReminderService pillReminderService;
    private Properties properties;
    private AllPatients allPatients;

    @Autowired
    public DailyPillReminderAdherenceService(AllDosageAdherenceLogs allDosageAdherenceLogs, TAMAPillReminderService pillReminderService, @Qualifier("dailyPillReminderProperties") Properties properties, AdherenceService adherenceService, AllPatients allPatients) {
        this.allDosageAdherenceLogs = allDosageAdherenceLogs;
        this.pillReminderService = pillReminderService;
        this.properties = properties;
        this.allPatients = allPatients;
        adherenceService.register(CallPreference.DailyPillReminder, this);
    }

    public double getAdherencePercentage(String patientId, DateTime asOfDate) {
        Patient patient = allPatients.get(patientId);
        DateTime fromDate = asOfDate.minusWeeks(4);
        DateTime callPreferenceTransitionDate = patient.getPatientPreferences().getCallPreferenceTransitionDate();
        if(callPreferenceTransitionDate != null){
            fromDate = asOfDate.minusWeeks(4).isBefore(callPreferenceTransitionDate) ? callPreferenceTransitionDate : asOfDate.minusWeeks(4);
        }
        return getAdherencePercentage(patientId, fromDate.toLocalDate(), asOfDate);
     }

    @Override
    public boolean wasAnyDoseMissedLastWeek(Patient patient) {
        LocalDate oneWeekAgo = DateUtil.now().minusWeeks(1).toLocalDate();
        DateTime yesterday = DateUtil.newDateTime(DateUtil.today().minusDays(1), 23, 59, 59);
        return getAdherencePercentage(patient.getId(), oneWeekAgo, yesterday) != 100;
    }

    @Override
    public boolean wasAnyDoseTakenLateSince(Patient patient, LocalDate since) {
        return allDosageAdherenceLogs.getDoseTakenLateCount(patient.getId(), since, true) > 0;
    }

    public void backFillAdherence(String patientId, boolean wasDoseTaken, DateTime startDate, DateTime endDate) {
        PillRegimen pillRegimen = pillReminderService.getPillRegimen(patientId);

        Dose firstProbableDose = pillRegimen.getDoseAt(startDate);
        if(firstProbableDose != null && firstProbableDose.getDoseTime().isBefore(startDate)) {
            startDate = firstProbableDose.getDoseTime();
        }

        List<DosageResponse> dosageResponses = pillRegimen.getDosageResponses();
        DosageStatus dosageStatus = wasDoseTaken ? DosageStatus.TAKEN : DosageStatus.NOT_TAKEN;
        for (DosageResponse dosageResponse : dosageResponses) {
            DosageTimeLine dosageTimeLine = new DosageTimeLine(dosageResponse, startDate, endDate);
            while (dosageTimeLine.hasNext()) {
                Dose dose = dosageTimeLine.next();
                if (allDosageAdherenceLogs.findByDosageIdAndDate(dose.getDosageId(), dose.getDate()) == null) {
                    recordDosageAdherenceAsCaptured(patientId, pillRegimen.getId(), dose, dosageStatus, dose.getDoseTime());
                }
            }
        }
    }

    public void recordDosageAdherenceAsCaptured(String patientId, String regimenId, Dose dose, DosageStatus dosageStatus, DateTime doseTakenTime) {
        recordAdherence(patientId, regimenId, dose, dosageStatus, doseTakenTime);
        pillReminderService.setLastCapturedDate(regimenId, dose.getDosageId(), dose.getDate());
    }

    public void recordDosageAdherenceAsNotCaptured(String patientId, String regimenId, Dose dose, DosageStatus dosageStatus, DateTime doseTakenTime) {
        recordAdherence(patientId, regimenId, dose, dosageStatus, doseTakenTime);
    }

    public LocalDate getDailyPillReminderAdherenceTrackingStartDate(Patient patient, TreatmentAdvice treatmentAdvice) {
        LocalDate dailyPillReminderAdherenceTrackingStartDate = DateUtil.newDate(treatmentAdvice.getStartDate());
        if (patient.getPatientPreferences().getCallPreferenceTransitionDate() != null && dailyPillReminderAdherenceTrackingStartDate.isBefore(patient.getPatientPreferences().getCallPreferenceTransitionDate().toLocalDate()))
            dailyPillReminderAdherenceTrackingStartDate = patient.getPatientPreferences().getCallPreferenceTransitionDate().toLocalDate();
        return dailyPillReminderAdherenceTrackingStartDate;
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

    private double getAdherencePercentage(String patientId, LocalDate fromDate, DateTime toDate) {
        PillRegimen pillRegimen = pillReminderService.getPillRegimen(patientId);
        int totalDoses = pillRegimen.getDosesBetween(fromDate, toDate);
        if (totalDoses == 0) return 100;
        int dosagesTakenForLastFourWeeks = allDosageAdherenceLogs.countBy(pillRegimen.getId(), DosageStatus.TAKEN, fromDate, toDate.toLocalDate());
        return ((double) dosagesTakenForLastFourWeeks) * 100 / totalDoses;
    }
}