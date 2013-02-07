package org.motechproject.tama.dailypillreminder.contract;

import lombok.Data;
import org.motechproject.tama.dailypillreminder.domain.DailyPillReminderSummary;
import org.motechproject.tama.patient.domain.PatientReports;

import java.util.List;

@Data
public class DailyPillReminderReport {

    private PatientReports patientReports;
    private List<DailyPillReminderSummary> dailyPillReminderSummaries;

    public DailyPillReminderReport(PatientReports patientReports, List<DailyPillReminderSummary> dailyPillReminderSummaries) {
        this.patientReports = patientReports;
        this.dailyPillReminderSummaries = dailyPillReminderSummaries;
    }
}
