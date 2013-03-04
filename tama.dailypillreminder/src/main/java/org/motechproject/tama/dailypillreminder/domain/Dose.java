package org.motechproject.tama.dailypillreminder.domain;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.motechproject.server.pillreminder.api.contract.DosageResponse;
import org.motechproject.util.DateUtil;

public class Dose implements Comparable<Dose> {

    private final DosageResponse dosageResponse;
    private final LocalDate date;

    public Dose(DosageResponse dosageResponse, LocalDate date) {
        this.dosageResponse = dosageResponse;
        this.date = date;
    }

    public LocalDate getDate() {
        return date;
    }

    public DosageResponse getDosage() {
        return dosageResponse;
    }

    public String getDosageId() {
        return dosageResponse.getDosageId();
    }

    public int getDosageHour() {
        return dosageResponse.getDosageHour();
    }

    public int getDosageMinute() {
        return dosageResponse.getDosageMinute();
    }

    public LocalDate getStartDate() {
        return dosageResponse.getStartDate();
    }

    public LocalDate getResponseLastCapturedDate() {
        return dosageResponse.getResponseLastCapturedDate();
    }

    public DateTime getDoseTime() {
        return DateUtil.newDateTime(date, dosageResponse.getDosageHour(), dosageResponse.getDosageMinute(), 0);
    }

    public boolean isTaken() {
        return getResponseLastCapturedDate() != null && getResponseLastCapturedDate().equals(getDate());
    }

    public boolean isOnTime(DateTime specifiedDateTime, int dosageIntervalInMinutes) {
        return !isEarlyToTake(specifiedDateTime, dosageIntervalInMinutes) && !isLateToTake(specifiedDateTime, dosageIntervalInMinutes);
    }

    public boolean isEarlyToTake(DateTime specifiedDateTime, int dosageIntervalInMinutes) {
        DateTime dosageWindowStart = getDoseTime().minusMinutes(dosageIntervalInMinutes);
        return specifiedDateTime.isBefore(dosageWindowStart);
    }

    public boolean isLateToTake(DateTime specifiedDateTime, int dosageIntervalInMinutes) {
        DateTime dosageWindowEnd = getDoseTime().plusMinutes(dosageIntervalInMinutes);
        return specifiedDateTime.isAfter(dosageWindowEnd);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Dose that = (Dose) o;
        if (dosageResponse == null || that.dosageResponse == null) return false;
        if (!dosageResponse.equals(that.dosageResponse)) return false;
        return date.equals(that.date);
    }

    @Override
    public int hashCode() {
        return dosageResponse != null ? dosageResponse.hashCode() : 0;
    }

    @Override
    public int compareTo(Dose that) {
        return this.getDoseTime().compareTo(that.getDoseTime());
    }
}
