package org.motechproject.tama.dailypillreminder.domain;

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

    public int getHour() {
        return dosageResponse.getDosageHour();
    }

    public int getMinute() {
        return dosageResponse.getDosageMinute();
    }

    public int getDosesBetween(LocalDate from, DateTime to) {
        DateTime fromDateTime = DateUtil.newDateTime(from, 0, 0, 0);
        DateTime dosageStartTime = DateUtil.newDateTime(dosageResponse.getStartDate(), getHour(), getMinute(), 0);
        LocalDate dosageEndDate = dosageResponse.getEndDate() != null ? dosageResponse.getEndDate() : to.toLocalDate();
        DateTime dosageEndTime = DateUtil.newDateTime(dosageEndDate, getHour(), getMinute(), 0);

        if (!fromDateTime.isAfter(dosageStartTime)) {
            fromDateTime = dosageStartTime;
        }
        if (!to.isBefore(dosageEndTime)) {
            to = dosageEndTime;
        }

        if (fromDateTime.isEqual(to)) {
            return 1;
        } else if (to.isBefore(fromDateTime)) {
            return 0;
        } else {
            return Days.daysBetween(fromDateTime.toLocalDate(), to.toLocalDate()).getDays() + numberOfDosesOnLastDay(to);
        }
    }

    private int numberOfDosesOnLastDay(DateTime dateTime) {
        DateTime doseTime = dateTime.withTime(getHour(), getMinute(), 0, 0);
        return !dateTime.isBefore(doseTime) ? 1 : 0;
    }

    public DateTime firstDose() {
        return DateUtil.newDateTime(dosageResponse.getStartDate(), dosageResponse.getDosageHour(), dosageResponse.getDosageMinute(), 0);
    }
}
