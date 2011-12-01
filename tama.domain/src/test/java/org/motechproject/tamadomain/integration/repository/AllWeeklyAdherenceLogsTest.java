package org.motechproject.tamadomain.integration.repository;

import org.joda.time.LocalDate;
import org.junit.Test;
import org.motechproject.tamacommon.integration.repository.SpringIntegrationTest;
import org.motechproject.tamadomain.domain.WeeklyAdherenceLog;
import org.motechproject.tamadomain.repository.AllWeeklyAdherenceLogs;
import org.motechproject.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static junit.framework.Assert.assertEquals;

public class AllWeeklyAdherenceLogsTest extends SpringIntegrationTest {

    @Autowired
    AllWeeklyAdherenceLogs allWeeklyAdherenceLogs;

    @Override
    public void before() {
        super.before();
        allWeeklyAdherenceLogs.add(new WeeklyAdherenceLog("Patient1", "TADocID1", DateUtil.newDate(2000, 9, 28), DateUtil.newDate(2000, 10, 5), 0));
        allWeeklyAdherenceLogs.add(new WeeklyAdherenceLog("Patient1", "TADocID1", DateUtil.newDate(2000, 10, 5), DateUtil.newDate(2000, 10, 13), 0));
        allWeeklyAdherenceLogs.add(new WeeklyAdherenceLog("Patient1", "TADocID1", DateUtil.newDate(2000, 10, 13), DateUtil.newDate(2000, 10, 21), 0));
        allWeeklyAdherenceLogs.add(new WeeklyAdherenceLog("Patient1", "TADocID2", DateUtil.newDate(2000, 10, 13), DateUtil.newDate(2000, 10, 21), 0));
        allWeeklyAdherenceLogs.add(new WeeklyAdherenceLog("Patient2", "TADocID21", DateUtil.newDate(2000, 9, 30), DateUtil.newDate(2000, 10, 7), 0));
        allWeeklyAdherenceLogs.add(new WeeklyAdherenceLog("Patient3", "TADocID31", DateUtil.newDate(2000, 10, 2), DateUtil.newDate(2000, 10, 9), 0));
    }

    @Test
    public void shouldReturnTrueIfLogExistsForSpecifiedWeek() {
        LocalDate weekStartDate = DateUtil.newDate(2000, 10, 5);
        List<WeeklyAdherenceLog> adherenceLogs = allWeeklyAdherenceLogs.findLogsByWeekStartDate("Patient1", "TADocID1", weekStartDate);
        assertEquals(1, adherenceLogs.size());
        WeeklyAdherenceLog log = adherenceLogs.get(0);
        assertEquals(weekStartDate, log.getWeekStartDate());
    }

    @Test
    public void shouldReturnFalseIfLogDoesNotExistForSpecifiedWeek() {
        LocalDate weekStartDate = DateUtil.newDate(2000, 10, 11);
        List<WeeklyAdherenceLog> adherenceLogs = allWeeklyAdherenceLogs.findLogsByWeekStartDate("Patient1", "TADocID1", weekStartDate);
        assertEquals(0, adherenceLogs.size());
    }

    @Override
    public void after() {
        for (WeeklyAdherenceLog log : allWeeklyAdherenceLogs.getAll())
            markForDeletion(log);
        super.after();
    }
}
