package org.motechproject.tamahealthtip.domain;

import org.joda.time.LocalDate;
import org.joda.time.Period;
import org.joda.time.PeriodType;
import org.motechproject.tamadomain.domain.Patient;
import org.motechproject.util.DateUtil;

public class HealthTipParams {

    private Patient patient;
    private boolean dosageMissedLastWeek;
    private LocalDate treatmentStartDate;
    private LocalDate lastCD4TestDate;
    private int lastCD4Count;

    public HealthTipParams(Patient patient, boolean dosageMissedLastWeek) {
        this.patient = patient;
        this.dosageMissedLastWeek = dosageMissedLastWeek;
    }

    public int numberOfMonthsSinceTreatmentStarted() {
        return new Period(treatmentStartDate, DateUtil.today(), PeriodType.months()).getMonths();
    }

    public int numberOfWeeksSinceTreatmentStarted() {
        return new Period(treatmentStartDate, DateUtil.today(), PeriodType.weeks()).getWeeks();
    }

    public void treatmentAdviceStartDate(LocalDate treatmentAdviceStartDate){
        treatmentStartDate = treatmentAdviceStartDate;
    }

    public boolean isPatientOnDailyPillReminder() {
        return patient.isOnDailyPillReminder();
    }

    public boolean isDosageMissedLastWeek() {
        return dosageMissedLastWeek;
    }

    public void lastCD4TestDate(LocalDate lastCD4TestDate) {
        this.lastCD4TestDate = lastCD4TestDate;
    }

    public int numberOfWeeksSinceLastCD4LabTest(){
        return new Period(lastCD4TestDate, DateUtil.today(), PeriodType.weeks()).getWeeks();
    }

    public void lastCD4Count(int lastCD4Count) {
        this.lastCD4Count = lastCD4Count;
    }

    public int lastCD4Count() {
        return lastCD4Count;
    }
}