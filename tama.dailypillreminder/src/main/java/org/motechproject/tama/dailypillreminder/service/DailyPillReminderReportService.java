package org.motechproject.tama.dailypillreminder.service;

import org.joda.time.LocalDate;
import org.json.JSONException;
import org.json.JSONObject;
import org.motechproject.tama.dailypillreminder.domain.DailyPillReminderSummary;
import org.motechproject.tama.dailypillreminder.domain.DosageAdherenceLogPerDay;
import org.motechproject.tama.dailypillreminder.repository.AllDosageAdherenceLogs;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class DailyPillReminderReportService {

    private AllDosageAdherenceLogs allDosageAdherenceLogs;

    @Autowired
    public DailyPillReminderReportService(AllDosageAdherenceLogs allDosageAdherenceLogs) {
        this.allDosageAdherenceLogs = allDosageAdherenceLogs;
    }

    public JSONObject createReport(String patientId, LocalDate startDate, LocalDate endDate) throws JSONException {
        JSONObject result = new JSONObject();
        result.put(startDate.toString(), endDate.toString());
        return result;
    }

    public List<DailyPillReminderSummary> create(String patientDocId, LocalDate startDate, LocalDate endDate) {
        List<DosageAdherenceLogPerDay> logsPerDay = allDosageAdherenceLogs.getLogsPerDay(patientDocId, startDate, endDate);
        List<DailyPillReminderSummary> dailyPillReminderSummaries = new ArrayList<DailyPillReminderSummary>();
        for (DosageAdherenceLogPerDay dosageAdherenceLogPerDay : logsPerDay) {
            dailyPillReminderSummaries.add(new DailyPillReminderSummary(dosageAdherenceLogPerDay));
        }
        return dailyPillReminderSummaries;
    }
}
