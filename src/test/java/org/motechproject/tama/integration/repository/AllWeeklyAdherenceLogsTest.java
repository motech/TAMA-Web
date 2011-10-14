package org.motechproject.tama.integration.repository;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.motechproject.tama.domain.WeeklyAdherenceLog;
import org.motechproject.tama.repository.AllWeeklyAdherenceLogs;
import org.motechproject.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

public class AllWeeklyAdherenceLogsTest extends SpringIntegrationTest {

    @Autowired
    AllWeeklyAdherenceLogs allWeeklyAdherenceLogs;

    @Before
    public void setUp() {
        super.before();
        allWeeklyAdherenceLogs.add(new WeeklyAdherenceLog("Patient1", "TADocID1", DateUtil.newDate(2000, 9, 28), DateUtil.newDate(2000, 10, 5), 0));
        allWeeklyAdherenceLogs.add(new WeeklyAdherenceLog("Patient1", "TADocID1", DateUtil.newDate(2000, 10, 5), DateUtil.newDate(2000, 10, 13), 0));
        allWeeklyAdherenceLogs.add(new WeeklyAdherenceLog("Patient1", "TADocID1", DateUtil.newDate(2000, 10, 13), DateUtil.newDate(2000, 10, 21), 0));
        allWeeklyAdherenceLogs.add(new WeeklyAdherenceLog("Patient1", "TADocID2", DateUtil.newDate(2000, 10, 13), DateUtil.newDate(2000, 10, 21), 0));
        allWeeklyAdherenceLogs.add(new WeeklyAdherenceLog("Patient2", "TADocID21", DateUtil.newDate(2000, 9, 30), DateUtil.newDate(2000, 10, 7), 0));
        allWeeklyAdherenceLogs.add(new WeeklyAdherenceLog("Patient3", "TADocID31", DateUtil.newDate(2000, 10, 2), DateUtil.newDate(2000, 10, 9), 0));
    }

    @Test
    public void shouldFindLogsByDateRange() {
        List<WeeklyAdherenceLog> weeklyAdherenceLogs = allWeeklyAdherenceLogs.findByDateRange("Patient1", "TADocID1", DateUtil.newDate(2000, 10, 5), DateUtil.newDate(2000, 10, 13));
        assertEquals(2, weeklyAdherenceLogs.size());
        assertEquals("Patient1", weeklyAdherenceLogs.get(0).getPatientId());
        assertEquals("Patient1", weeklyAdherenceLogs.get(1).getPatientId());
        assertEquals("TADocID1", weeklyAdherenceLogs.get(0).getTreatmentAdviceId());
        assertEquals("TADocID1", weeklyAdherenceLogs.get(1).getTreatmentAdviceId());
    }

    @Test
    public void shouldReturnTrueIfLogExistsForSpecifiedWeek() {
        boolean logsExist = allWeeklyAdherenceLogs.logExistsFor("Patient1", "TADocID1", DateUtil.newDate(2000, 10, 5));
        assertTrue(logsExist);
    }

    @Test
    public void shouldReturnFalseIfLogDoesNotExistForSpecifiedWeek() {
        boolean logsExist = allWeeklyAdherenceLogs.logExistsFor("Patient1", "TADocID1", DateUtil.newDate(2000, 10, 11));
        assertFalse(logsExist);
    }

    @After
    public void tearDown() {
        for (WeeklyAdherenceLog log : allWeeklyAdherenceLogs.getAll())
            markForDeletion(log);
        super.after();
    }
}
