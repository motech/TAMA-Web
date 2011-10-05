package org.motechproject.tama.integration.repository;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.motechproject.tama.domain.WeeklyAdherenceLog;
import org.motechproject.tama.repository.AllWeeklyAdherenceLogs;
import org.motechproject.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;

import static junit.framework.Assert.assertEquals;

public class AllWeeklyAdherenceLogsTest extends SpringIntegrationTest {

    @Autowired
    AllWeeklyAdherenceLogs allWeeklyAdherenceLogs;

    @Before
    public void setUp() {
        super.before();
        allWeeklyAdherenceLogs.add(new WeeklyAdherenceLog("Patient1", DateUtil.newDate(2000, 10, 5), 0, "TADocID1"));
        allWeeklyAdherenceLogs.add(new WeeklyAdherenceLog("Patient1", DateUtil.newDate(2000, 10, 13), 0, "TADocID1"));
        allWeeklyAdherenceLogs.add(new WeeklyAdherenceLog("Patient1", DateUtil.newDate(2000, 10, 21), 0, "TADocID1"));
        allWeeklyAdherenceLogs.add(new WeeklyAdherenceLog("Patient1", DateUtil.newDate(2000, 10, 21), 0, "TADocID2"));
        allWeeklyAdherenceLogs.add(new WeeklyAdherenceLog("Patient2", DateUtil.newDate(2000, 10, 7), 0, "TADocID21"));
        allWeeklyAdherenceLogs.add(new WeeklyAdherenceLog("Patient3", DateUtil.newDate(2000, 10, 9), 0, "TADocID31"));
    }

    @Test
    public void shouldFindLogCountForGivenDateRange() {
        int numLogs = allWeeklyAdherenceLogs.findLogCountByPatientIDAndTreatmentAdviceIdAndDateRange("Patient1", "TADocID1", DateUtil.newDate(2000, 10, 5), DateUtil.newDate(2000, 10, 15));
        assertEquals(2, numLogs);
    }

    @After
    public void tearDown() {
        for (WeeklyAdherenceLog log : allWeeklyAdherenceLogs.getAll())
            markForDeletion(log);
        super.after();
    }
}
