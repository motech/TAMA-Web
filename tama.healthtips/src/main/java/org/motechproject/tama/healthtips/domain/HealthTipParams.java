package org.motechproject.tama.healthtips.domain;

import org.joda.time.LocalDate;
import org.joda.time.Period;
import org.joda.time.PeriodType;
import org.motechproject.tama.ivr.domain.AdherenceComplianceReport;
import org.motechproject.tama.patient.domain.LabResults;
import org.motechproject.tama.patient.domain.Patient;
import org.motechproject.util.DateUtil;

public class HealthTipParams {

    private Patient patient;
    private AdherenceComplianceReport adherenceComplianceReport;
    private LabResults labResults;
    private LocalDate treatmentStartDate;

    public HealthTipParams(Patient patient, AdherenceComplianceReport adherenceComplianceReport, LabResults labResults, LocalDate treatmentStartDate) {
        this.patient = patient;
        this.adherenceComplianceReport = adherenceComplianceReport;
        this.labResults = labResults;
        this.treatmentStartDate = treatmentStartDate;
    }

    public int numberOfMonthsSinceTreatmentStarted() {
        return new Period(treatmentStartDate, DateUtil.today(), PeriodType.months()).getMonths();
    }

    public int numberOfWeeksSinceTreatmentStarted() {
        return new Period(treatmentStartDate, DateUtil.today(), PeriodType.weeks()).getWeeks();
    }

    public boolean isPatientOnDailyPillReminder() {
        return patient.isOnDailyPillReminder();
    }

    public boolean isDosageMissedLastWeek() {
        return adherenceComplianceReport.missed();
    }

    public boolean isAnyDoseTakenLateLastWeek() {
        return adherenceComplianceReport.late();
    }

    public int numberOfWeeksSinceLastCD4LabTest() {
        return new Period(labResults.latestLabTestDate(), DateUtil.today(), PeriodType.weeks()).getWeeks();
    }

    public int lastCD4Count() {
        return labResults.latestCD4Count();
    }

    public int baselineCD4Count() {
        return labResults.getBaseLineCD4Count();
    }
}