package org.motechproject.tama.dailypillreminder.domain;

import lombok.EqualsAndHashCode;
import org.joda.time.LocalDate;
import org.joda.time.LocalTime;
import org.motechproject.model.Time;

@EqualsAndHashCode
public class DailyPillReminderSummary {

    private LocalDate date;
    private String morningDoseTime;
    private DosageStatus morningDoseStatus;
    private String eveningDoseTime;
    private DosageStatus eveningDoseStatus;
    private Time NOON_TIME = new Time(12, 0);
    private String patientDocId;
    private String regimenId;
    private String treatmentAdviceId;

    public DailyPillReminderSummary(DosageAdherenceLogPerDay dosageAdherenceLogPerDay) {
        this.date = dosageAdherenceLogPerDay.getDate();
        this.patientDocId = dosageAdherenceLogPerDay.getPatientDocId();

        for (DosageAdherenceLogSummary dosageAdherenceLogSummary : dosageAdherenceLogPerDay.getLogs()) {
            if (dosageAdherenceLogSummary.getDosageTime().ge(NOON_TIME)) {
                setEveningDoseTime(dosageAdherenceLogSummary.getDosageTime());
                setEveningDoseStatus(dosageAdherenceLogSummary.getDosageStatus());
            } else {
                setMorningDoseTime(dosageAdherenceLogSummary.getDosageTime());
                setMorningDoseStatus(dosageAdherenceLogSummary.getDosageStatus());
            }
            this.regimenId = dosageAdherenceLogSummary.getRegimenId();
            this.treatmentAdviceId = dosageAdherenceLogSummary.getTreatmentAdviceId();
        }
    }

    public String getDate() {
        return date.toString("dd/MM/yyyy");
    }

    public String getMorningDoseTime() {
        return morningDoseTime;
    }

    public DailyPillReminderSummary setMorningDoseTime(Time morningTime) {
        this.morningDoseTime = getTimeAsString(morningTime);
        return this;
    }

    public String getMorningDoseStatus() {
        return morningDoseStatus == null ? null : getStatusString(morningDoseStatus);
    }

    private String getStatusString(DosageStatus status) {
        if (status.equals(DosageStatus.WILL_TAKE_LATER) || status.equals(DosageStatus.NOT_RECORDED))
            return "NOT_REPORTED";
        return status.toString();
    }

    public DailyPillReminderSummary setMorningDoseStatus(DosageStatus morningDoseStatus) {
        this.morningDoseStatus = morningDoseStatus;
        return this;
    }

    public String getEveningDoseTime() {
        return eveningDoseTime;
    }

    public DailyPillReminderSummary setEveningDoseTime(Time eveningTime) {
        this.eveningDoseTime = getTimeAsString(eveningTime);
        return this;
    }

    public String getEveningDoseStatus() {
        return eveningDoseStatus == null ? null : getStatusString(eveningDoseStatus);
    }

    public DailyPillReminderSummary setEveningDoseStatus(DosageStatus eveningDoseStatus) {
        this.eveningDoseStatus = eveningDoseStatus;
        return this;
    }

    private String getTimeAsString(Time time) {
        return new LocalTime(time.getHour(), time.getMinute()).toString("hh:mm");
    }

    public void setPatientDocId(String patientDocId) {
        this.patientDocId = patientDocId;
    }

    public String getPatientDocId() {
        return patientDocId;
    }

    public String getRegimenId() {
        return regimenId;
    }
    public String getTreatmentAdviceId()
    {
        return treatmentAdviceId;
    }
}
