package org.motechproject.tama.dailypillreminder.domain;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.motechproject.server.pillreminder.contract.DosageResponse;
import org.motechproject.server.pillreminder.contract.MedicineResponse;
import org.motechproject.util.DateUtil;

import java.util.ArrayList;
import java.util.List;

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

    public List<String> medicineNames() {
        List<String> medicineNames = new ArrayList<String>();
        for (MedicineResponse medicine : dosageResponse.getMedicines()) {
            if (!date.isBefore(medicine.getStartDate()) &&
                    (medicine.getEndDate() == null || !date.isAfter(medicine.getEndDate())))
                medicineNames.add(String.format("pill%s", medicine.getName()));
        }
        return medicineNames;
    }

    public boolean isTaken() {
        return getResponseLastCapturedDate() != null && getResponseLastCapturedDate().equals(getDate());
    }

    public boolean isWithinSpecifiedInterval(DateTime specifiedDateTime, int intervalInMinutes) {
        boolean nowAfterPillWindowStart = specifiedDateTime.isAfter(getDoseTime().minusMinutes(intervalInMinutes));
        boolean nowBeforePillWindowEnd = specifiedDateTime.isBefore(getDoseTime().plusMinutes(intervalInMinutes));
        return nowAfterPillWindowStart && nowBeforePillWindowEnd;
    }

    public boolean isEarlyToTake(DateTime specifiedDateTime, int reminderRepeatWindowInHours, int dosageIntervalInMinutes) {
        DateTime dosageWindowStart = getDoseTime().minusMinutes(dosageIntervalInMinutes);
        DateTime pillWindowStart = getDoseTime().minusHours(reminderRepeatWindowInHours);
        return specifiedDateTime.isAfter(pillWindowStart) && specifiedDateTime.isBefore(dosageWindowStart);
    }

    public boolean isLateToTake(DateTime specifiedDateTime, int dosageIntervalInMinutes) {
        DateTime scheduledDoseInterval = getDoseTime().plusMinutes(dosageIntervalInMinutes);
        return specifiedDateTime.isAfter(scheduledDoseInterval);
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
