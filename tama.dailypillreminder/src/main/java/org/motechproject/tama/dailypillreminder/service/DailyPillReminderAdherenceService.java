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
import org.motechproject.tama.patient.repository.AllPatients;
import org.motechproject.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Properties;

@Service
public class DailyPillReminderAdherenceService implements AdherenceServiceStrategy {

    private AllPatients allPatients;
    private AllDosageAdherenceLogs allDosageAdherenceLogs;
    private TAMAPillReminderService pillReminderService;
    private Properties properties;
    private List<DosageResponse> dosageResponses;

    @Autowired
    public DailyPillReminderAdherenceService(AllPatients allPatients, AllDosageAdherenceLogs allDosageAdherenceLogs, TAMAPillReminderService pillReminderService, @Qualifier("dailyPillReminderProperties") Properties properties, AdherenceService adherenceService) {
        this.allPatients = allPatients;
        this.allDosageAdherenceLogs = allDosageAdherenceLogs;
        this.pillReminderService = pillReminderService;
        this.properties = properties;
        adherenceService.register(CallPreference.DailyPillReminder, this);
    }

    public double getAdherencePercentage(String patientId, DateTime asOfDate) {
        return getAdherencePercentage(patientId, asOfDate.minusWeeks(4).toLocalDate(), asOfDate);
    }

    private double getAdherencePercentage(String patientId, LocalDate fromDate, DateTime toDate) {
        PillRegimen pillRegimen = pillReminderService.getPillRegimen(patientId);
        int totalDoses = pillRegimen.getDosesBetween(fromDate, toDate);
        if (totalDoses == 0) return 100;
        int dosagesTakenForLastFourWeeks = allDosageAdherenceLogs.countBy(pillRegimen.getId(), DosageStatus.TAKEN, fromDate, toDate.toLocalDate());
        return ((double) dosagesTakenForLastFourWeeks) * 100 / totalDoses;
    }

    public void backFillAdherenceForPeriodOfSuspension(String patientId, boolean wasDoseTaken) {
        Patient patient = allPatients.get(patientId);
        PillRegimen pillRegimen = pillReminderService.getPillRegimen(patientId);
        dosageResponses = pillRegimen.getDosageResponses();
        resetSuspensionDateBasedOnPreviousDosageStatus(sort(dosageResponses), patient.getLastSuspendedDate());
        DosageStatus dosageStatus = wasDoseTaken ? DosageStatus.TAKEN : DosageStatus.NOT_TAKEN;
        for (DosageResponse dosageResponse : dosageResponses) {
            SingleDosageTimeLine dosageTimeLine = new SingleDosageTimeLine(dosageResponse, patient.getLastSuspendedDate(), DateUtil.now());
            while (dosageTimeLine.hasNext()) {
                Dose dose = dosageTimeLine.next();
                DosageAdherenceLog dosageAdherenceLog = new DosageAdherenceLog(patientId, pillRegimen.getId(), dose.getDosageId(), dosageStatus, dose.getDate());
                if (allDosageAdherenceLogs.findByDosageIdAndDate(dose.getDosageId(), dose.getDate()) == null) {
                    allDosageAdherenceLogs.add(dosageAdherenceLog);
                    pillReminderService.setLastCapturedDate(pillRegimen.getId(), dose.getDosageId(), dose.getDate());
                }
            }
        }
    }

    private Dose previous(int currentDosageIndex, DateTime iteratingDate, int previousDosageIndex) {
        DosageResponse dosageResponseToReturn;
        DateTime dateToReturn;
        if (currentDosageIndex == 0) {
            dosageResponseToReturn = dosageResponses.get(dosageResponses.size() - 1);
            dateToReturn = DateUtil.newDateTime(iteratingDate.toLocalDate().minusDays(1), dosageResponseToReturn.getDosageHour(), dosageResponseToReturn.getDosageMinute(), 0);
        } else {
            dosageResponseToReturn = dosageResponses.get(previousDosageIndex - 1);
            dateToReturn = DateUtil.newDateTime(iteratingDate.toLocalDate(), dosageResponseToReturn.getDosageHour(), dosageResponseToReturn.getDosageMinute(), 0);
        }
        return new Dose(dosageResponseToReturn, dateToReturn.toLocalDate());
    }

    private boolean doseIsLate(Dose dose, DateTime doseTakenTime) {
        Integer dosageInterval = Integer.parseInt(properties.getProperty(TAMAConstants.DOSAGE_INTERVAL));
        DateTime scheduledDoseInterval = dose.getDoseTime().plusMinutes(dosageInterval);
        return doseTakenTime.isAfter(scheduledDoseInterval);
    }

    private void resetSuspensionDateBasedOnPreviousDosageStatus(List<DosageResponse> dosageResponses, DateTime from) {
        DateTime iteratingDate = from;
        int currentDosageIndex = 0;
        int previousDosageIndex = 0;
        boolean found = false;
        DateTime actualSuspensionDateAndTime = from;
        for (DosageResponse dosageResponse : dosageResponses) {
            if (isDosageApplicableForDate(dosageResponse, from, from)) {
                previousDosageIndex = currentDosageIndex;
                found = true;
                break;
            }
            currentDosageIndex++;
        }
        if (!found) {
            iteratingDate = iteratingDate.plusDays(1);
            from = from.plusDays(1);
            DosageResponse nextDosage = dosageResponses.get(previousDosageIndex);
            iteratingDate = iteratingDate.withHourOfDay(nextDosage.getDosageHour()).withMinuteOfHour(nextDosage.getDosageMinute());
            from = from.withHourOfDay(nextDosage.getDosageHour()).withMinuteOfHour(nextDosage.getDosageMinute());
        }

        currentDosageIndex = previousDosageIndex;
        if (previousDosageIndex == 0) {
            previousDosageIndex = dosageResponses.size() - 1;
        } else {
            previousDosageIndex = previousDosageIndex - 1;
        }
        if (isSuspensionTimeWithinPillWindowOfPreviousDosage(
                new Dose(dosageResponses.get(currentDosageIndex), iteratingDate.toLocalDate()), currentDosageIndex, previousDosageIndex, iteratingDate, actualSuspensionDateAndTime)
                && isPreviousDosageNotCaptured(previous(currentDosageIndex, iteratingDate, previousDosageIndex))) {
            DosageResponse previousDosage = dosageResponses.get(previousDosageIndex);
            if (currentDosageIndex == 0) {
                from = from.minusDays(1);
                from = from.withHourOfDay(previousDosage.getDosageHour()).withMinuteOfHour(previousDosage.getDosageMinute());
            } else {
                from = from.withHourOfDay(previousDosage.getDosageHour()).withMinuteOfHour(previousDosage.getDosageMinute());
            }
        }
    }

    private boolean isPreviousDosageNotCaptured(Dose previousDose) {
        DosageAdherenceLog previousDosageAdherenceLog = allDosageAdherenceLogs.findByDosageIdAndDate(previousDose.getDosageId(), previousDose.getDate());
        return previousDosageAdherenceLog == null;
    }

    private boolean isDosageApplicableForDate(DosageResponse dosageResponse, DateTime day, DateTime from) {
        int dosageTimeInMinutes = (dosageResponse.getDosageHour() * 60) + dosageResponse.getDosageMinute();
        if ((day.isEqual(from)) && !isLastDay(day)) {
            int timeInMinutes = (from.getHourOfDay() * 60) + from.getMinuteOfHour();
            return timeInMinutes <= dosageTimeInMinutes;
        } else if (isLastDay(day) && !day.isEqual(from)) {
            int timeInMinutes = (DateUtil.now().getHourOfDay() * 60) + DateUtil.now().getMinuteOfHour();
            return timeInMinutes >= dosageTimeInMinutes;
        } else if (day.isAfter(from) && day.isBefore(DateUtil.now())) {
            return true;
        } else if (day.isEqual(from) && isLastDay(day)) {
            int startTime = (from.getHourOfDay() * 60) + from.getMinuteOfHour();
            int endTime = (DateUtil.now().getHourOfDay() * 60) + DateUtil.now().getMinuteOfHour();
            return dosageTimeInMinutes >= startTime ? dosageTimeInMinutes <= endTime : false;
        }
        return false;
    }

    private boolean isSuspensionTimeWithinPillWindowOfPreviousDosage(Dose currentDose, int currentDosageIndex, int previousDosageIndex, DateTime iteratingDate, DateTime actualSuspensionDateAndTime) {
        Dose previousDoseResponse = previous(currentDosageIndex, iteratingDate, previousDosageIndex);
        int previousDosageTimeInMinutes = (previousDoseResponse.getDosageHour() * 60) + previousDoseResponse.getDosageMinute();
        int currentDosageTimeInMinutes = (currentDose.getDosageHour() * 60) + currentDose.getDosageMinute();
        int timeInMinutes = (actualSuspensionDateAndTime.getHourOfDay() * 60) + actualSuspensionDateAndTime.getMinuteOfHour();
        int pillWindowInMinutes = (Integer.parseInt(properties.getProperty(TAMAConstants.PILL_WINDOW))) * 60;
        if (timeInMinutes <= (currentDosageTimeInMinutes - pillWindowInMinutes)) {
            LocalDate actualSuspensionDate = actualSuspensionDateAndTime.toLocalDate();
            LocalDate previousDosageDosageDate = previousDoseResponse.getDate();
            if (previousDosageDosageDate.isBefore(actualSuspensionDate) || (previousDosageDosageDate.isEqual(actualSuspensionDate) && previousDosageTimeInMinutes <= timeInMinutes))
                return true;
        }
        return false;
    }

    private boolean isLastDay(DateTime day) {
        return (day.toLocalDate().compareTo(DateUtil.now().toLocalDate())) == 0;
    }

    private List<DosageResponse> sort(List<DosageResponse> dosageResponses) {
        Collections.sort(dosageResponses, new Comparator<DosageResponse>() {
            @Override
            public int compare(DosageResponse o1, DosageResponse o2) {
                Integer time1InMinutes = (o1.getDosageHour() * 60) + o1.getDosageMinute();
                Integer time2InMinutes = (o2.getDosageHour() * 60) + o2.getDosageMinute();
                return time1InMinutes.compareTo(time2InMinutes);
            }
        });
        return dosageResponses;
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

    public void recordDosageAdherenceAsCaptured(String patientId, String regimenId, Dose dose, DosageStatus dosageStatus, DateTime doseTakenTime) {
        recordAdherence(patientId, regimenId, dose, dosageStatus, doseTakenTime);
        pillReminderService.setLastCapturedDate(regimenId, dose.getDosageId(), dose.getDate());
    }

    public void recordDosageAdherenceAsNotCaptured(String patientId, String regimenId, Dose dose, DosageStatus dosageStatus, DateTime doseTakenTime) {
        recordAdherence(patientId, regimenId, dose, dosageStatus, doseTakenTime);
    }

    private void recordAdherence(String patientId, String regimenId, Dose dose, DosageStatus status, DateTime doseTakenTime) {
        DosageAdherenceLog existingLog = allDosageAdherenceLogs.findByDosageIdAndDate(dose.getDosageId(), dose.getDate());

        if (existingLog == null) {
            DosageAdherenceLog adherenceLog = new DosageAdherenceLog(patientId, regimenId, dose.getDosageId(), status, dose.getDate());
            if (doseIsLate(dose, doseTakenTime)) adherenceLog.dosageIsTakenLate();
            allDosageAdherenceLogs.add(adherenceLog);
        } else {
            existingLog.setDosageStatus(status);
            if (doseIsLate(dose, doseTakenTime)) existingLog.dosageIsTakenLate();
            allDosageAdherenceLogs.update(existingLog);
        }
    }
}