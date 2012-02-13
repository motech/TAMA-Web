package org.motechproject.tama.dailypillreminder.service;

import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.tama.dailypillreminder.domain.DailyPillReminderSummary;
import org.motechproject.tama.dailypillreminder.domain.DosageAdherenceLogPerDay;
import org.motechproject.tama.dailypillreminder.domain.DosageAdherenceLogSummary;
import org.motechproject.tama.dailypillreminder.domain.DosageStatus;
import org.motechproject.tama.dailypillreminder.repository.AllDosageAdherenceLogs;
import org.motechproject.tama.patient.service.TreatmentAdviceService;
import org.motechproject.util.DateUtil;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static junit.framework.Assert.assertEquals;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.powermock.api.mockito.PowerMockito.when;

public class DailyPillReminderReportServiceTest {

    @Mock
    private TreatmentAdviceService treatmentAdviceService;
    @Mock
    private AllDosageAdherenceLogs allDosageAdherenceLogs;

    private DailyPillReminderReportService dailyPillReminderReportService;

    @Before
    public void setUp() {
        initMocks(this);
        dailyPillReminderReportService = new DailyPillReminderReportService(allDosageAdherenceLogs, treatmentAdviceService);
    }

    @Test
    @Ignore
    public void shouldReturnAListOfDailyPillReminderReports(){
        LocalDate startDate = DateUtil.newDate(2012, 1, 2);
        LocalDate endDate = DateUtil.newDate(2012, 1, 10);

        Map<String, List<String>> map = new HashMap<String, List<String>>();
        map.put("treatmentAdvice_1", Arrays.asList("11:45am", "05:50pm"));
        map.put("treatmentAdvice_2", Arrays.asList("10:45am"));
        when(treatmentAdviceService.getAllDrugTimeHistory("patientDocId")).thenReturn(map);

        DosageAdherenceLogSummary summary_1 = new DosageAdherenceLogSummary().setTreatmentAdviceId("treatmentAdvice_1").setDosageStatus(DosageStatus.TAKEN.toString());
        DosageAdherenceLogSummary summary_2 = new DosageAdherenceLogSummary().setTreatmentAdviceId("treatmentAdvice_1").setDosageStatus(DosageStatus.NOT_TAKEN.toString());
        DosageAdherenceLogSummary summary_3 = new DosageAdherenceLogSummary().setTreatmentAdviceId("treatmentAdvice_2").setDosageStatus(DosageStatus.TAKEN.toString());

        DosageAdherenceLogPerDay logDay1 = new DosageAdherenceLogPerDay().setLogs(Arrays.asList(summary_1, summary_2));
        DosageAdherenceLogPerDay logDay2 = new DosageAdherenceLogPerDay().setLogs(Arrays.asList(summary_3));
        when(allDosageAdherenceLogs.getLogsPerDay("patientDocId", startDate, endDate)).thenReturn(Arrays.asList(logDay1, logDay2));

        List<DailyPillReminderSummary> reports = dailyPillReminderReportService.create("patientDocId", startDate, endDate);

        assertEquals(2, reports.size());
        DailyPillReminderSummary day1_report = reports.get(0);
        DailyPillReminderSummary day2_report = reports.get(1);
        assertReportSummary(day1_report, "11:45am", DosageStatus.TAKEN.toString(), "05:50pm", DosageStatus.NOT_TAKEN.toString());
        assertReportSummary(day2_report, "10:45am", DosageStatus.TAKEN.toString(), null, null);
    }

    private void assertReportSummary(DailyPillReminderSummary day1_report, String morningTime, String morningDosageStatus, String eveningTime, String eveningDosageStatus) {
        assertEquals(morningTime, day1_report.getMorningTime());
        assertEquals(morningDosageStatus, day1_report.getMorningStatus());
        assertEquals(eveningTime, day1_report.getEveningTime());
        assertEquals(eveningDosageStatus, day1_report.getEveningStatus());
    }
}
