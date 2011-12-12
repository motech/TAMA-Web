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

    public int getDosesIn(int numberOfWeeks, DateTime asOfDate) {
        DateTime dosageStartTime = DateUtil.newDateTime(dosageResponse.getStartDate(), getHour(), getMinute(), 0);
        int doses = getNumberOfDosesBetween(dosageStartTime, asOfDate);
        int days = numberOfWeeks * 7;
        return doses > days ? days : doses;
    }

    /* Range is inclusive of from and till*/
    public int getNumberOfDosesBetween(DateTime from, DateTime till) {
        DateTime dosageStartTime = DateUtil.newDateTime(dosageResponse.getStartDate(), getHour(), getMinute(), 0);
        LocalDate dosageEndDate = dosageResponse.getEndDate() != null ? dosageResponse.getEndDate() : till.toLocalDate();
        DateTime dosageEndTime = DateUtil.newDateTime(dosageEndDate, getHour(), getMinute(), 0);
        if (!from.isAfter(dosageStartTime))
            from = dosageStartTime;
        if (!till.isBefore(dosageEndTime))
            till = dosageEndTime;
        if (from.isEqual(till)) {
            return 1;
        } else {
            return numberOfDosesOnFirstDay(from) + numberOfDosesBetweenFirstAndLastDay(from, till) + numberOfDosesOnLastDay(till);
        }
    }

    private int numberOfDosesOnFirstDay(DateTime dateTime) {
        DateTime doseTime = dateTime.withTime(getHour(), getMinute(), 0, 0);
        return !dateTime.isAfter(doseTime) ? 1 : 0;
    }

    private int numberOfDosesBetweenFirstAndLastDay(DateTime from, DateTime till) {
        DateTime dayAfterFrom = from.plusDays(1);
        if (dayAfterFrom.isAfter(till))
            return 0;
        return Days.daysBetween(dayAfterFrom.toLocalDate(), till.toLocalDate()).getDays();
    }

    private int numberOfDosesOnLastDay(DateTime dateTime) {
        DateTime doseTime = dateTime.withTime(getHour(), getMinute(), 0, 0);
        return !dateTime.isBefore(doseTime) ? 1 : 0;
    }

    public int getHour() {
        return dosageResponse.getDosageHour();
    }

    public int getMinute() {
        return dosageResponse.getDosageMinute();
    }
}
