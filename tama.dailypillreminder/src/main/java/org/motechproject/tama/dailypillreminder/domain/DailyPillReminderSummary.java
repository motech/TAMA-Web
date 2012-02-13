package org.motechproject.tama.dailypillreminder.domain;

import org.joda.time.LocalDate;

public class DailyPillReminderSummary {

    private LocalDate date;
    private String morningTime;
    private String morningStatus;
    private String eveningTime;
    private String eveningStatus;

    public LocalDate getDate() {
        return date;
    }

    public DailyPillReminderSummary setDate(LocalDate date) {
        this.date = date;
        return this;
    }

    public String getMorningTime() {
        return morningTime;
    }

    public DailyPillReminderSummary setMorningTime(String morningTime) {
        this.morningTime = morningTime;
        return this;
    }

    public String getMorningStatus() {
        return morningStatus;
    }

    public DailyPillReminderSummary setMorningStatus(String morningStatus) {
        this.morningStatus = morningStatus;
        return this;
    }

    public String getEveningTime() {
        return eveningTime;
    }

    public DailyPillReminderSummary setEveningTime(String eveningTime) {
        this.eveningTime = eveningTime;
        return this;
    }

    public String getEveningStatus() {
        return eveningStatus;
    }

    public DailyPillReminderSummary setEveningStatus(String eveningStatus) {
        this.eveningStatus = eveningStatus;
        return this;
    }
}
