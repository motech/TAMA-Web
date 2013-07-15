package org.motechproject.tama.dailypillreminder.service;

import org.joda.time.LocalDate;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.model.Time;
import org.motechproject.tama.dailypillreminder.contract.DailyPillReminderReport;
import org.motechproject.tama.dailypillreminder.domain.DailyPillReminderSummary;
import org.motechproject.tama.dailypillreminder.domain.DosageAdherenceLogPerDay;
import org.motechproject.tama.dailypillreminder.domain.DosageAdherenceLogSummary;
import org.motechproject.tama.dailypillreminder.domain.DosageStatus;
import org.motechproject.tama.dailypillreminder.repository.AllDosageAdherenceLogs;
import org.motechproject.tama.patient.domain.PatientReports;
import org.motechproject.tama.patient.service.PatientService;
import org.motechproject.util.DateUtil;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static java.util.Arrays.asList;
import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.motechproject.util.DateUtil.today;
import static org.powermock.api.mockito.PowerMockito.when;

public class DailyPillReminderReportServiceTest {

    @Mock
    private AllDosageAdherenceLogs allDosageAdherenceLogs;
    @Mock
    private PatientService patientService;

    private LocalDate startDate;
    private LocalDate endDate;

    private DailyPillReminderReportService dailyPillReminderReportService;

    @Before
    public void setUp() {
        initMocks(this);
        dailyPillReminderReportService = new DailyPillReminderReportService(allDosageAdherenceLogs, patientService);

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
    public void shouldReturnAListOfDailyPillReminderSummaries() {
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
        assertEquals("{\"eveningDoseStatus\":\"NOT_TAKEN\",\"regimenId\":null,\"eveningDoseTime\":\"05:50\",\"morningDoseStatus\":\"TAKEN\",\"patientDocId\":null,\"morningDoseTime\":\"11:45\",\"date\":\"2012-01-02\",\"treatmentAdviceId\":null}", report_1.toString());
        assertEquals("{\"eveningDoseStatus\":null,\"regimenId\":null,\"eveningDoseTime\":null,\"morningDoseStatus\":\"TAKEN\",\"patientDocId\":null,\"morningDoseTime\":\"10:45\",\"date\":\"2012-01-10\",\"treatmentAdviceId\":null}", report_2.toString());
    }

    @Test
    public void shouldReturnListOfDailyPillReminderReports() {
        String patientId = "patientId";
        String docId1 = "docId1";
        String docId2 = "docId2";

        PatientReports patientReports = mock(PatientReports.class);

        DosageAdherenceLogPerDay log1 = buildDosageAdherenceLogPerDay();
        DosageAdherenceLogPerDay log2 = buildDosageAdherenceLogPerDay();

        when(patientService.getPatientReports(patientId)).thenReturn(patientReports);
        when(patientReports.getPatientDocIds()).thenReturn(asList(docId1, docId2));

        when(allDosageAdherenceLogs.getLogsPerDay(docId1, startDate, endDate)).thenReturn(asList(log1));
        when(allDosageAdherenceLogs.getLogsPerDay(docId2, startDate, endDate)).thenReturn(asList(log2));

        DailyPillReminderReport reports = dailyPillReminderReportService.reports(patientId, startDate, endDate);
        assertEquals(patientReports, reports.getPatientReports());
        assertEquals(new DailyPillReminderSummary(log1), reports.getDailyPillReminderSummaries().get(0));
        assertEquals(new DailyPillReminderSummary(log2), reports.getDailyPillReminderSummaries().get(1));
    }

    private void assertReportSummary(DailyPillReminderSummary day1_report, String morningTime, String morningDosageStatus, String eveningTime, String eveningDosageStatus) {
        assertEquals(morningTime, day1_report.getMorningDoseTime());
        assertEquals(morningDosageStatus, day1_report.getMorningDoseStatus());
        assertEquals(eveningTime, day1_report.getEveningDoseTime());
        assertEquals(eveningDosageStatus, day1_report.getEveningDoseStatus());
    }

    private DosageAdherenceLogPerDay buildDosageAdherenceLogPerDay(){
        DosageAdherenceLogPerDay log = new DosageAdherenceLogPerDay();
        log.setLogs(Collections.<DosageAdherenceLogSummary>emptyList());
        log.setDate(today());
        return log;
    }
}
