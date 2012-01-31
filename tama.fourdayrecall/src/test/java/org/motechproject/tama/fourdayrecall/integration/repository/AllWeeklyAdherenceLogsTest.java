package org.motechproject.tama.fourdayrecall.integration.repository;

import org.joda.time.LocalDate;
import org.junit.Test;
import org.motechproject.tama.common.integration.repository.SpringIntegrationTest;
import org.motechproject.tama.fourdayrecall.domain.WeeklyAdherenceLog;
import org.motechproject.tama.fourdayrecall.repository.AllWeeklyAdherenceLogs;
import org.motechproject.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import static junit.framework.Assert.*;

@ContextConfiguration(locations = "classpath*:applicationFourDayRecallContext.xml", inheritLocations = false)
public class AllWeeklyAdherenceLogsTest extends SpringIntegrationTest {

    @Autowired
    AllWeeklyAdherenceLogs allWeeklyAdherenceLogs;

    @Override
    public void before() {
        super.before();
        allWeeklyAdherenceLogs.add(new WeeklyAdherenceLog("Patient1", "TADocID1", DateUtil.newDate(2000, 9, 28), DateUtil.newDateTime(DateUtil.newDate(2000, 10, 5), 0, 0, 0), 0));
        allWeeklyAdherenceLogs.add(new WeeklyAdherenceLog("Patient1", "TADocID1", DateUtil.newDate(2000, 10, 5), DateUtil.newDateTime(DateUtil.newDate(2000, 10, 13),  0, 0, 0),0));
        allWeeklyAdherenceLogs.add(new WeeklyAdherenceLog("Patient1", "TADocID1", DateUtil.newDate(2000, 10, 13), DateUtil.newDateTime(DateUtil.newDate(2000, 10, 21),  0, 0, 0), 0));
        allWeeklyAdherenceLogs.add(new WeeklyAdherenceLog("Patient1", "TADocID2", DateUtil.newDate(2000, 10, 13), DateUtil.newDateTime(DateUtil.newDate(2000, 10, 22),  0, 0, 0), 0));
        allWeeklyAdherenceLogs.add(new WeeklyAdherenceLog("Patient2", "TADocID21", DateUtil.newDate(2000, 9, 30), DateUtil.newDateTime(DateUtil.newDate(2000, 10, 7),  0, 0, 0), 0));
        allWeeklyAdherenceLogs.add(new WeeklyAdherenceLog("Patient3", "TADocID31", DateUtil.newDate(2000, 10, 2), DateUtil.newDateTime(DateUtil.newDate(2000, 10, 9),  0, 0, 0), 0));
    }

    @Test
    public void shouldFetchLogIfItExists() {
        LocalDate weekStartDate = DateUtil.newDate(2000, 10, 5);
        WeeklyAdherenceLog log = allWeeklyAdherenceLogs.findLogByWeekStartDate("Patient1", "TADocID1", weekStartDate);
        assertNotNull(log);
        assertEquals(weekStartDate, log.getWeekStartDate());
    }

    @Test
    public void shouldReturnNullIfNoLogExists() {
        LocalDate weekStartDate = DateUtil.newDate(2000, 10, 11);
        WeeklyAdherenceLog log = allWeeklyAdherenceLogs.findLogByWeekStartDate("Patient1", "TADocID1", weekStartDate);
        assertNull(log);
    }

    @Override
    public void after() {
        for (WeeklyAdherenceLog log : allWeeklyAdherenceLogs.getAll())
            markForDeletion(log);
        super.after();
    }
}
