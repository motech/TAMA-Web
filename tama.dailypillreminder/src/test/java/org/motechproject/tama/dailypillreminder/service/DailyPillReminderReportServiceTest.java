package org.motechproject.tama.dailypillreminder.service;

import org.joda.time.LocalDate;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.model.Time;
import org.motechproject.tama.dailypillreminder.domain.DailyPillReminderSummary;
import org.motechproject.tama.dailypillreminder.domain.DosageAdherenceLogPerDay;
import org.motechproject.tama.dailypillreminder.domain.DosageAdherenceLogSummary;
import org.motechproject.tama.dailypillreminder.domain.DosageStatus;
import org.motechproject.tama.dailypillreminder.repository.AllDosageAdherenceLogs;
import org.motechproject.util.DateUtil;

import java.util.Arrays;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.powermock.api.mockito.PowerMockito.when;

public class DailyPillReminderReportServiceTest {

    @Mock
    private AllDosageAdherenceLogs allDosageAdherenceLogs;

    private LocalDate startDate;
    private LocalDate endDate;

    private DailyPillReminderReportService dailyPillReminderReportService;

    @Before
    public void setUp() {
        initMocks(this);
        dailyPillReminderReportService = new DailyPillReminderReportService(allDosageAdherenceLogs);

        startDate = DateUtil.newDate(2012, 1, 2);
        endDate = DateUtil.newDate(2012, 1, 10);

        DosageAdherenceLogSummary day_1_summary_1 = new DosageAdherenceLogSummary().setDosageTime(new Time(11, 45)).setDosageStatus(DosageStatus.TAKEN);
        DosageAdherenceLogSummary day_1_summary_2 = new DosageAdherenceLogSummary().setDosageTime(new Time(17, 50)).setDosageStatus(DosageStatus.NOT_TAKEN);
        DosageAdherenceLogSummary day_2_summary_1 = new DosageAdherenceLogSummary().setDosageTime(new Time(10, 45)).setDosageStatus(DosageStatus.TAKEN);

        DosageAdherenceLogPerDay logDay1 = new DosageAdherenceLogPerDay().setLogs(Arrays.asList(day_1_summary_1, day_1_summary_2)).setDate(startDate);
        DosageAdherenceLogPerDay logDay2 = new DosageAdherenceLogPerDay().setLogs(Arrays.asList(day_2_summary_1)).setDate(endDate);
        when(allDosageAdherenceLogs.getLogsPerDay("patientDocId", startDate, endDate)).thenReturn(Arrays.asList(logDay1, logDay2));
    }

    @Test
    public void shouldReturnAListOfDailyPillReminderReports(){
        List<DailyPillReminderSummary> reports = dailyPillReminderReportService.create("patientDocId", startDate, endDate);

        assertEquals(2, reports.size());
        DailyPillReminderSummary day1_report = reports.get(0);
        DailyPillReminderSummary day2_report = reports.get(1);
        assertReportSummary(day1_report, "11:45", DosageStatus.TAKEN.toString(), "05:50", DosageStatus.NOT_TAKEN.toString());
        assertReportSummary(day2_report, "10:45", DosageStatus.TAKEN.toString(), null, null);
    }

    @Test
    public void shouldReturnReportSummariesAsJSON() throws JSONException {
        JSONObject result = dailyPillReminderReportService.JSONReport("patientDocId", startDate, endDate);

        JSONArray logs = (JSONArray) result.get("logs");
        assertEquals(2, logs.length());
        JSONObject report_1 = (JSONObject) logs.get(0);
        JSONObject report_2 = (JSONObject) logs.get(1);
        assertEquals("{\"eveningStatus\":\"NOT_TAKEN\",\"morningStatus\":\"TAKEN\",\"morningTime\":\"11:45\",\"eveningTime\":\"05:50\",\"date\":\"2012-01-02\"}", report_1.toString());
        assertEquals("{\"eveningStatus\":null,\"morningStatus\":\"TAKEN\",\"morningTime\":\"10:45\",\"eveningTime\":null,\"date\":\"2012-01-10\"}", report_2.toString());
    }

    private void assertReportSummary(DailyPillReminderSummary day1_report, String morningTime, String morningDosageStatus, String eveningTime, String eveningDosageStatus) {
        assertEquals(morningTime, day1_report.getMorningTime());
        assertEquals(morningDosageStatus, day1_report.getMorningStatus());
        assertEquals(eveningTime, day1_report.getEveningTime());
        assertEquals(eveningDosageStatus, day1_report.getEveningStatus());
    }
}
