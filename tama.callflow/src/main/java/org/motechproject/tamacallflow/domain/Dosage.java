package org.motechproject.tamacallflow.domain;

import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.LocalDate;
import org.motechproject.server.pillreminder.contract.DosageResponse;
import org.motechproject.util.DateUtil;

public class Dosage {

    private DosageResponse dosageResponse;

    public Dosage(DosageResponse dosageResponse) {
        this.dosageResponse = dosageResponse;
    }

    // range is inclusive of from and exclusive of till
    public int getNumberOfDosesBetween(DateTime from, DateTime till) {
        DateTime dosageStartDateTime = DateUtil.newDateTime(dosageResponse.getStartDate(), dosageResponse.getDosageHour(), dosageResponse.getDosageMinute(), 0);
        LocalDate dosageEndDate = dosageResponse.getEndDate() != null ? dosageResponse.getEndDate() : till.toLocalDate();
        DateTime dosageEndDateTime = DateUtil.newDateTime(dosageEndDate, dosageResponse.getDosageHour(), dosageResponse.getDosageMinute(), 0);
        if (!from.isAfter(dosageStartDateTime))
            from = dosageStartDateTime;
        if (!till.isBefore(dosageEndDateTime))
            till = dosageEndDateTime;
        return numberOfDosesOnFirstDay(from) + numberOfDosesOnLastDay(till) + numberOfDosesBetweenFirstAndLastDay(from, till);
    }

    private int numberOfDosesBetweenFirstAndLastDay(DateTime from, DateTime till) {
        DateTime dayAfterFrom = from.plusDays(1);
        if (dayAfterFrom.isAfter(till))
            return 0;
        return Days.daysBetween(dayAfterFrom.toLocalDate(), till.toLocalDate()).getDays();
    }

    private int numberOfDosesOnFirstDay(DateTime dateTime) {
        DateTime doseDateTime = dateTime.withTime(dosageResponse.getDosageHour(), dosageResponse.getDosageMinute(), 0, 0);
        return !dateTime.isAfter(doseDateTime)? 1 : 0;
    }

    private int numberOfDosesOnLastDay(DateTime dateTime) {
        DateTime doseDateTime = dateTime.withTime(dosageResponse.getDosageHour(), dosageResponse.getDosageMinute(), 0, 0);
        return dateTime.isAfter(doseDateTime)? 1 : 0;
    }

    public int getHour() {
        return dosageResponse.getDosageHour();
    }

    public int getMinute() {
        return dosageResponse.getDosageMinute();
    }
}
