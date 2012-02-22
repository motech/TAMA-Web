package org.motechproject.tama.dailypillreminder.domain;

import org.junit.Test;
import org.motechproject.model.Time;
import org.motechproject.util.DateUtil;

import java.util.ArrayList;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNull;

public class DailyPillReminderSummaryTest {

    @Test
    public void shouldSetMorningTimeAndMorningStatus_whenDosageAdherenceLogSummaryTimeIsLessThan_12_Noon(){
        ArrayList<DosageAdherenceLogSummary> dosageAdherenceLogSummaries = new ArrayList<DosageAdherenceLogSummary>();
        dosageAdherenceLogSummaries.add(new DosageAdherenceLogSummary().setDosageStatus(DosageStatus.TAKEN).setDosageTime(new Time(11, 45)));
        DosageAdherenceLogPerDay dosageAdherenceLogPerDay = new DosageAdherenceLogPerDay().setLogs(dosageAdherenceLogSummaries).setDate(DateUtil.newDate(2010, 9, 23));
        DailyPillReminderSummary dailyPillReminderSummary = new DailyPillReminderSummary(dosageAdherenceLogPerDay);

        assertEquals("2010-09-23", dailyPillReminderSummary.getDate());
        assertEquals("TAKEN", dailyPillReminderSummary.getMorningDoseStatus());
        assertEquals("11:45", dailyPillReminderSummary.getMorningDoseTime());
        assertNull(dailyPillReminderSummary.getEveningDoseTime());
    }

    @Test
    public void shouldSetEveningTimeAndStatus_whenDosageAdherenceLogSummaryTimeIsGreaterThan_12_Noon(){
        ArrayList<DosageAdherenceLogSummary> dosageAdherenceLogSummaries = new ArrayList<DosageAdherenceLogSummary>();
        dosageAdherenceLogSummaries.add(new DosageAdherenceLogSummary().setDosageStatus(DosageStatus.TAKEN).setDosageTime(new Time(15, 25)));
        DosageAdherenceLogPerDay dosageAdherenceLogPerDay = new DosageAdherenceLogPerDay().setLogs(dosageAdherenceLogSummaries);
        DailyPillReminderSummary dailyPillReminderSummary = new DailyPillReminderSummary(dosageAdherenceLogPerDay);

        assertEquals("TAKEN", dailyPillReminderSummary.getEveningDoseStatus());
        assertEquals("03:25", dailyPillReminderSummary.getEveningDoseTime());
        assertNull(dailyPillReminderSummary.getMorningDoseTime());
    }

    @Test
    public void shouldReturnDosageStatus() {
        ArrayList<DosageAdherenceLogSummary> dosageAdherenceLogSummaries = new ArrayList<DosageAdherenceLogSummary>();
        dosageAdherenceLogSummaries.add(new DosageAdherenceLogSummary().setDosageStatus(DosageStatus.TAKEN).setDosageTime(new Time(11, 45)));
        dosageAdherenceLogSummaries.add(new DosageAdherenceLogSummary().setDosageStatus(DosageStatus.NOT_TAKEN).setDosageTime(new Time(17, 45)));
        DosageAdherenceLogPerDay dosageAdherenceLogPerDay = new DosageAdherenceLogPerDay().setLogs(dosageAdherenceLogSummaries);
        DailyPillReminderSummary dailyPillReminderSummary = new DailyPillReminderSummary(dosageAdherenceLogPerDay);

        assertEquals("TAKEN", dailyPillReminderSummary.getMorningDoseStatus());
        assertEquals("NOT_TAKEN", dailyPillReminderSummary.getEveningDoseStatus());
    }

    @Test
    public void shouldReturnDosageStatus_NOT_REPORTED_whenStatusIs_NOT_RECORDED_Or_WILL_TAKE_LATER() {
        ArrayList<DosageAdherenceLogSummary> dosageAdherenceLogSummaries = new ArrayList<DosageAdherenceLogSummary>();
        dosageAdherenceLogSummaries.add(new DosageAdherenceLogSummary().setDosageStatus(DosageStatus.NOT_RECORDED).setDosageTime(new Time(11, 45)));
        dosageAdherenceLogSummaries.add(new DosageAdherenceLogSummary().setDosageStatus(DosageStatus.WILL_TAKE_LATER).setDosageTime(new Time(17, 45)));
        DosageAdherenceLogPerDay dosageAdherenceLogPerDay = new DosageAdherenceLogPerDay().setLogs(dosageAdherenceLogSummaries);
        DailyPillReminderSummary dailyPillReminderSummary = new DailyPillReminderSummary(dosageAdherenceLogPerDay);

        assertEquals("NOT_REPORTED", dailyPillReminderSummary.getMorningDoseStatus());
        assertEquals("NOT_REPORTED", dailyPillReminderSummary.getEveningDoseStatus());
    }
}
