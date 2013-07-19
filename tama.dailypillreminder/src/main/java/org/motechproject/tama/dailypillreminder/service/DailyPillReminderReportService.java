package org.motechproject.tama.dailypillreminder.service;

import org.joda.time.LocalDate;
import org.json.JSONException;
import org.json.JSONObject;
import org.motechproject.tama.dailypillreminder.contract.DailyPillReminderReport;
import org.motechproject.tama.dailypillreminder.domain.DailyPillReminderSummary;
import org.motechproject.tama.dailypillreminder.domain.DosageAdherenceLogPerDay;
import org.motechproject.tama.dailypillreminder.repository.AllDosageAdherenceLogs;
import org.motechproject.tama.patient.domain.PatientReports;
import org.motechproject.tama.patient.service.PatientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class DailyPillReminderReportService {

    private AllDosageAdherenceLogs allDosageAdherenceLogs;
    private PatientService patientService;

    @Autowired
    public DailyPillReminderReportService(AllDosageAdherenceLogs allDosageAdherenceLogs, PatientService patientService) {
        this.allDosageAdherenceLogs = allDosageAdherenceLogs;
        this.patientService = patientService;
    }

    public JSONObject JSONReport(String patientDocId, LocalDate startDate, LocalDate endDate) throws JSONException {
        JSONObject result = new JSONObject();
        List<DailyPillReminderSummary> dailyPillReminderSummaries = create(patientDocId, startDate, endDate);
        List<JSONObject> reportLogs = new ArrayList<JSONObject>();
        for (DailyPillReminderSummary dailyPillReminderSummary : dailyPillReminderSummaries) {
            reportLogs.add(new JSONObject(dailyPillReminderSummary));
        }
        result.put("logs", reportLogs);
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

    public DailyPillReminderReport reports(String patientId, LocalDate startDate, LocalDate endDate) {
        PatientReports patientReports = patientService.getPatientReports(patientId);
        List<DailyPillReminderSummary> dailyPillReminderSummaries = new ArrayList<>();
        for (String patientDocId : patientReports.getPatientDocIds()) {
            dailyPillReminderSummaries.addAll(create(patientDocId, startDate, endDate));
        }
        return new DailyPillReminderReport(patientReports, dailyPillReminderSummaries);
    }
    public DailyPillReminderReport reports(String patientId, String clinicId, LocalDate startDate, LocalDate endDate) {
        PatientReports patientReports = patientService.getPatientReports(patientId,clinicId);
        List<DailyPillReminderSummary> dailyPillReminderSummaries = new ArrayList<>();
        for (String patientDocId : patientReports.getPatientDocIds()) {
            dailyPillReminderSummaries.addAll(create(patientDocId, startDate, endDate));
        }
        return new DailyPillReminderReport(patientReports, dailyPillReminderSummaries);
    }


}
