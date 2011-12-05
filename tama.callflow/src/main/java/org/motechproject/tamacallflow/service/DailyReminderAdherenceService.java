package org.motechproject.tamacallflow.service;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.motechproject.server.pillreminder.contract.DosageResponse;
import org.motechproject.tamacallflow.domain.DosageTimeLine;
import org.motechproject.tamacallflow.domain.PillRegimen;
import org.motechproject.tamacallflow.ivr.Dose;
import org.motechproject.tamacommon.TAMAConstants;
import org.motechproject.tamadomain.domain.DosageAdherenceLog;
import org.motechproject.tamadomain.domain.SuspendedAdherenceData;
import org.motechproject.tamadomain.repository.AllDosageAdherenceLogs;
import org.motechproject.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Properties;

@Service
public class DailyReminderAdherenceService {

    private AllDosageAdherenceLogs allDosageAdherenceLogs;
    private TAMAPillReminderService pillReminderService;
    private Properties properties;
    private DateTime from;
    private DateTime actualSuspensionDateAndTime;
    private int previousDosageIndex = 0;
    private List<DosageResponse> dosageResponses;
    private DateTime iteratingDate;
    private int currentDosageIndex = 0;

    @Autowired
    public DailyReminderAdherenceService(AllDosageAdherenceLogs allDosageAdherenceLogs, TAMAPillReminderService pillReminderService, @Qualifier("ivrProperties") Properties properties) {
        this.allDosageAdherenceLogs = allDosageAdherenceLogs;
        this.pillReminderService = pillReminderService;
        this.properties = properties;
    }

    public void recordAdherence(SuspendedAdherenceData suspendedAdherenceData) {
        PillRegimen pillRegimen = pillReminderService.getPillRegimen(suspendedAdherenceData.patientId());
        from = suspendedAdherenceData.suspendedFrom();
        dosageResponses = pillRegimen.getDosageResponses();
        resetSuspensionDateBasedOnPreviousDosageStatus(sort(dosageResponses));
        DosageTimeLine dosageTimeLine = pillRegimen.getDosageTimeLine(from, DateUtil.now());
        while (dosageTimeLine.hasNext()) {
            Dose dose = dosageTimeLine.next();
            DosageAdherenceLog dosageAdherenceLog = new DosageAdherenceLog(suspendedAdherenceData.patientId(), pillRegimen.getId(), dose.getDosageId(), suspendedAdherenceData.getAdherenceDataWhenPatientWasSuspended().getStatus(), dose.getDosageDate());
            allDosageAdherenceLogs.add(dosageAdherenceLog);
        }
    }

    private void resetSuspensionDateBasedOnPreviousDosageStatus(List<DosageResponse> dosageResponses) {
        currentDosageIndex = 0;
        previousDosageIndex = 0;
        boolean found = false;
        iteratingDate = from;
        actualSuspensionDateAndTime = from;
        for (DosageResponse dosageResponse : dosageResponses) {
            if (isDosageApplicableForDate(dosageResponse, from)) {
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
        if (isSuspensionTimeWithinPillWindowOfPreviousDosage(new Dose(dosageResponses.get(currentDosageIndex), iteratingDate.toLocalDate())) && isPreviousDosageNotCaptured(previous())) {
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
        DosageAdherenceLog previousDosageAdherenceLog = allDosageAdherenceLogs.findByDosageIdAndDate(previousDose.getDosageId(), previousDose.getDosageDate());
        return previousDosageAdherenceLog == null ? true : false;
    }

    private boolean isDosageApplicableForDate(DosageResponse dosageResponse, DateTime day) {
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

    private boolean isSuspensionTimeWithinPillWindowOfPreviousDosage(Dose currentDose) {
        Dose previousDoseResponse = previous();
        int previousDosageTimeInMinutes = (previousDoseResponse.getDosageHour() * 60) + previousDoseResponse.getDosageMinute();
        int currentDosageTimeInMinutes = (currentDose.getDosageHour() * 60) + currentDose.getDosageMinute();
        int timeInMinutes = (actualSuspensionDateAndTime.getHourOfDay() * 60) + actualSuspensionDateAndTime.getMinuteOfHour();
        int pillWindowInMinutes = (Integer.parseInt(properties.getProperty(TAMAConstants.PILL_WINDOW))) * 60;
        if (timeInMinutes <= (currentDosageTimeInMinutes - pillWindowInMinutes)) {
            LocalDate actualSuspensionDate = actualSuspensionDateAndTime.toLocalDate();
            LocalDate previousDosageDosageDate = previousDoseResponse.getDosageDate();
            if(previousDosageDosageDate.isBefore(actualSuspensionDate) || (previousDosageDosageDate.isEqual(actualSuspensionDate) && previousDosageTimeInMinutes <= timeInMinutes))
            return true;
        }
        return false;
    }

    public Dose previous() {
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


}