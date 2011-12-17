package org.motechproject.tama.healthtips.domain;

import org.joda.time.LocalDate;
import org.joda.time.Period;
import org.joda.time.PeriodType;
import org.motechproject.tama.patient.domain.Patient;
import org.motechproject.util.DateUtil;

public class HealthTipParams {

    private Patient patient;
    private boolean dosageMissedLastWeek;
    private boolean anyDoseTakenLateLastWeek;
    private LocalDate treatmentStartDate;
    private LocalDate lastCD4TestDate;
    private int lastCD4Count;

    public HealthTipParams(Patient patient, boolean dosageMissedLastWeek, boolean anyDoseTakenLateLastWeek) {
        this.patient = patient;
        this.dosageMissedLastWeek = dosageMissedLastWeek;
        this.anyDoseTakenLateLastWeek = anyDoseTakenLateLastWeek;
    }

    public int numberOfMonthsSinceTreatmentStarted() {
        return new Period(treatmentStartDate, DateUtil.today(), PeriodType.months()).getMonths();
    }

    public int numberOfWeeksSinceTreatmentStarted() {
        return new Period(treatmentStartDate, DateUtil.today(), PeriodType.weeks()).getWeeks();
    }

    public void treatmentAdviceStartDate(LocalDate treatmentAdviceStartDate) {
        treatmentStartDate = treatmentAdviceStartDate;
    }

    public boolean isPatientOnDailyPillReminder() {
        return patient.isOnDailyPillReminder();
    }

    public boolean isDosageMissedLastWeek() {
        return dosageMissedLastWeek;
    }

    public boolean isAnyDoseTakenLateLastWeek() {
        return anyDoseTakenLateLastWeek;
    }

    public void lastCD4TestDate(LocalDate lastCD4TestDate) {
        this.lastCD4TestDate = lastCD4TestDate;
    }

    public int numberOfWeeksSinceLastCD4LabTest() {
        return new Period(lastCD4TestDate, DateUtil.today(), PeriodType.weeks()).getWeeks();
    }

    public void lastCD4Count(int lastCD4Count) {
        this.lastCD4Count = lastCD4Count;
    }

    public int lastCD4Count() {
        return lastCD4Count;
    }
}