package org.motechproject.tama.dailypillreminder.domain;

import org.joda.time.LocalDate;
import org.joda.time.LocalTime;
import org.motechproject.model.Time;

public class DailyPillReminderSummary {

    private LocalDate date;
    private String morningTime;
    private DosageStatus morningStatus;
    private String eveningTime;
    private DosageStatus eveningStatus;
    private final Time NOON_TIME = new Time(12, 0);

    public DailyPillReminderSummary(DosageAdherenceLogPerDay dosageAdherenceLogPerDay) {
        this.date = dosageAdherenceLogPerDay.getDate();
        for (DosageAdherenceLogSummary dosageAdherenceLogSummary : dosageAdherenceLogPerDay.getLogs()) {
            if (dosageAdherenceLogSummary.getDosageTime().le(NOON_TIME)) {
                setMorningTime(dosageAdherenceLogSummary.getDosageTime());
                setMorningStatus(dosageAdherenceLogSummary.getDosageStatus());
            }
            else {
                setEveningTime(dosageAdherenceLogSummary.getDosageTime());
                setEveningStatus(dosageAdherenceLogSummary.getDosageStatus());
            }
        }
    }

    public LocalDate getDate() {
        return date;
    }

    public String getMorningTime() {
        return morningTime;
    }

    public DailyPillReminderSummary setMorningTime(Time morningTime) {
        this.morningTime = getTimeAsString(morningTime);
        return this;
    }

    public DosageStatus getMorningStatus() {
        return morningStatus;
    }

    public DailyPillReminderSummary setMorningStatus(DosageStatus morningStatus) {
        this.morningStatus = morningStatus;
        return this;
    }

    public String getEveningTime() {
        return eveningTime;
    }

    public DailyPillReminderSummary setEveningTime(Time eveningTime) {
        this.eveningTime = getTimeAsString(eveningTime);
        return this;
    }

    public DosageStatus getEveningStatus() {
        return eveningStatus;
    }

    public DailyPillReminderSummary setEveningStatus(DosageStatus eveningStatus) {
        this.eveningStatus = eveningStatus;
        return this;
    }

    private String getTimeAsString(Time time) {
        return new LocalTime(time.getHour(), time.getMinute()).toString("hh:mm");
    }
}
