package org.motechproject.tama.dailypillreminder.mapper;

import org.joda.time.LocalDate;
import org.junit.Test;
import org.motechproject.model.Time;
import org.motechproject.tama.dailypillreminder.domain.DosageAdherenceLogPerDay;
import org.motechproject.tama.dailypillreminder.domain.DosageAdherenceLogSummary;
import org.motechproject.tama.dailypillreminder.domain.DosageStatus;
import org.motechproject.util.DateUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static junit.framework.Assert.assertEquals;

public class DosageAdherenceLogPerDayMapperTest {

    @Test
    public void convertAnEmptyList() {
        List<DosageAdherenceLogPerDay> list = new DosageAdherenceLogPerDayMapper().map(new ArrayList<DosageAdherenceLogSummary>());
        assertEquals(0, list.size());
    }

    @Test
    public void convertALogADay() {
        LocalDate today = DateUtil.today();
        DosageAdherenceLogSummary summary = new DosageAdherenceLogSummary().setDosageDate(today).setDosageStatus(DosageStatus.TAKEN).setDosageTime(new Time(10, 10));

        List<DosageAdherenceLogPerDay> list = new DosageAdherenceLogPerDayMapper().map(Arrays.asList(summary));
        assertEquals(1, list.size());

        DosageAdherenceLogPerDay summaryLogForToday = list.get(0);
        assertEquals(today, summaryLogForToday.getDate());

        List<DosageAdherenceLogSummary> logsForToday = summaryLogForToday.getLogs();
        assertEquals(1, logsForToday.size());
        assertEquals(DosageStatus.TAKEN, logsForToday.get(0).getDosageStatus());
        assertEquals(new Time(10, 10), logsForToday.get(0).getDosageTime());
    }

    @Test
    public void convert2LogsADay() {
        LocalDate today = DateUtil.today();
        DosageAdherenceLogSummary summary1 = new DosageAdherenceLogSummary().setDosageDate(today).setDosageStatus(DosageStatus.TAKEN).setDosageTime(new Time(10, 10));
        DosageAdherenceLogSummary summary2 = new DosageAdherenceLogSummary().setDosageDate(today).setDosageStatus(DosageStatus.NOT_TAKEN).setDosageTime(new Time(20, 10));

        List<DosageAdherenceLogPerDay> list = new DosageAdherenceLogPerDayMapper().map(Arrays.asList(summary1, summary2));
        assertEquals(1, list.size());

        DosageAdherenceLogPerDay summaryLogForToday = list.get(0);
        assertEquals(today, summaryLogForToday.getDate());

        List<DosageAdherenceLogSummary> logsForToday = summaryLogForToday.getLogs();
        assertEquals(2, logsForToday.size());
        assertEquals(DosageStatus.TAKEN, logsForToday.get(0).getDosageStatus());
        assertEquals(new Time(10, 10), logsForToday.get(0).getDosageTime());
        assertEquals(DosageStatus.NOT_TAKEN, logsForToday.get(1).getDosageStatus());
        assertEquals(new Time(20, 10), logsForToday.get(1).getDosageTime());
    }

    @Test
    public void convertALogADayAcross2Days() {
        LocalDate today = DateUtil.today();
        LocalDate yesterday = today.minusDays(1);
        DosageAdherenceLogSummary summary1 = new DosageAdherenceLogSummary().setDosageDate(yesterday).setDosageStatus(DosageStatus.NOT_RECORDED).setDosageTime(new Time(20, 10));
        DosageAdherenceLogSummary summary2 = new DosageAdherenceLogSummary().setDosageDate(today).setDosageStatus(DosageStatus.TAKEN).setDosageTime(new Time(10, 10));

        List<DosageAdherenceLogPerDay> list = new DosageAdherenceLogPerDayMapper().map(Arrays.asList(summary1, summary2));
        assertEquals(2, list.size());

        DosageAdherenceLogPerDay summaryLogForToday = list.get(0);
        assertEquals(today, summaryLogForToday.getDate());

        List<DosageAdherenceLogSummary> logsForToday = summaryLogForToday.getLogs();
        assertEquals(1, logsForToday.size());
        assertEquals(DosageStatus.TAKEN, logsForToday.get(0).getDosageStatus());
        assertEquals(new Time(10, 10), logsForToday.get(0).getDosageTime());

        DosageAdherenceLogPerDay summaryLogForYesterday = list.get(1);
        assertEquals(yesterday, summaryLogForYesterday.getDate());

        List<DosageAdherenceLogSummary> logsForYesterday = summaryLogForYesterday.getLogs();
        assertEquals(1, logsForYesterday.size());
        assertEquals(DosageStatus.NOT_RECORDED, logsForYesterday.get(0).getDosageStatus());
        assertEquals(new Time(20, 10), logsForYesterday.get(0).getDosageTime());
    }
}
