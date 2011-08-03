package org.motechproject.tama.integration.repository;

import org.joda.time.LocalDate;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.motechproject.tama.builder.DosageAdherenceLogBuilder;
import org.motechproject.tama.domain.DosageAdherenceLog;
import org.motechproject.tama.domain.DosageStatus;
import org.motechproject.tama.repository.DosageAdherenceLogs;
import org.motechproject.tama.util.DateUtility;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;

public class DosageAdherenceLogsTest extends SpringIntegrationTest {
    @Autowired
    private DosageAdherenceLogs dosageAdherenceLogs;

    @Before
    public void before() {
        super.before();
    }

    @After
    public void after() {
        for (DosageAdherenceLog log : dosageAdherenceLogs.getAll())
            markForDeletion(log);
        super.after();
    }

    @Test
    public void shouldSaveDosageAdherenceLogRecord() {

        DosageAdherenceLog dosageAdherenceLog = new DosageAdherenceLogBuilder().withDefaults().build();
        dosageAdherenceLogs.add(dosageAdherenceLog);

        assertNotNull(dosageAdherenceLog.getId());

        DosageAdherenceLog loadedDosageAdherenceLog = dosageAdherenceLogs.get(dosageAdherenceLog.getId());
        assertEquals(dosageAdherenceLog.getPatientId(), loadedDosageAdherenceLog.getPatientId());
        assertEquals(dosageAdherenceLog.getRegimenId(), loadedDosageAdherenceLog.getRegimenId());
        assertEquals(dosageAdherenceLog.getDosageId(), loadedDosageAdherenceLog.getDosageId());
        assertEquals(dosageAdherenceLog.getDosageStatus(), loadedDosageAdherenceLog.getDosageStatus());
    }

    @Test
    public void shouldGetDosageAdherenceLogsOfPatientForGivenDosage() {

        DosageAdherenceLog log1 = new DosageAdherenceLogBuilder().withDefaults().withDosageId("123").build();
        DosageAdherenceLog log2 = new DosageAdherenceLogBuilder().withDefaults().withDosageId("123").build();
        DosageAdherenceLog log3 = new DosageAdherenceLogBuilder().withDefaults().withDosageId("222").build();
        dosageAdherenceLogs.add(log1);
        dosageAdherenceLogs.add(log2);
        dosageAdherenceLogs.add(log3);

        List<DosageAdherenceLog> logs = dosageAdherenceLogs.findByDosageId("123");
        assertEquals(2, logs.size());
    }

    @Test
    public void shouldGetDosageAdherenceLogsOfPatientForGivenDosageAndDate() {

        LocalDate dosageDate = DateUtility.newLocalDate(2011, 12, 12);
        DosageAdherenceLog log1 = new DosageAdherenceLogBuilder().withDefaults().withDosageId("123").withDosageDate(dosageDate).build();
        DosageAdherenceLog log2 = new DosageAdherenceLogBuilder().withDefaults().withDosageId("123").withDosageDate(DateUtility.newLocalDate(2011, 12, 13)).build();
        dosageAdherenceLogs.add(log1);
        dosageAdherenceLogs.add(log2);

        DosageAdherenceLog log = dosageAdherenceLogs.findByDosageIdAndDate("123", dosageDate);
        assertNotNull(log);
    }

    @Test
    public void shouldGetTotalCountOfScheduledDosagesForLastFourWeeks() {
        LocalDate now = DateUtility.today();
        DosageAdherenceLog log0 = new DosageAdherenceLogBuilder().withDefaults().withDosageId("123").withDosageDate(now).build();
        DosageAdherenceLog log1 = new DosageAdherenceLogBuilder().withDefaults().withDosageId("123").withDosageDate(DateUtility.addDaysToLocalDate(now, -1)).build();
        DosageAdherenceLog log2 = new DosageAdherenceLogBuilder().withDefaults().withDosageId("123").withDosageDate(DateUtility.addDaysToLocalDate(now, -10)).build();
        DosageAdherenceLog log3 = new DosageAdherenceLogBuilder().withDefaults().withDosageId("222").withDosageDate(DateUtility.addDaysToLocalDate(now, -20)).build();
        DosageAdherenceLog log4 = new DosageAdherenceLogBuilder().withDefaults().withDosageId("123").withDosageDate(DateUtility.addDaysToLocalDate(now, -50)).build();
        DosageAdherenceLog log5 = new DosageAdherenceLogBuilder().withDefaults().withDosageId("123").withDosageDate(DateUtility.addDaysToLocalDate(now, -60)).build();
        dosageAdherenceLogs.add(log0);
        dosageAdherenceLogs.add(log1);
        dosageAdherenceLogs.add(log2);
        dosageAdherenceLogs.add(log3);
        dosageAdherenceLogs.add(log4);
        dosageAdherenceLogs.add(log5);

        int count = dosageAdherenceLogs.findScheduledDosagesTotalCount("123", DateUtility.addDaysToLocalDate(now, -28), now);

        Assert.assertEquals(3, count);
    }


    @Test
    public void shouldGetSuccessCountOfScheduledDosagesForLastFourWeeks() {
        LocalDate now = DateUtility.today();
        DosageAdherenceLog log0 = new DosageAdherenceLogBuilder().withDefaults().withDosageId("123").withDosageDate(now).withDosageStatus(DosageStatus.TAKEN).build();
        DosageAdherenceLog log1 = new DosageAdherenceLogBuilder().withDefaults().withDosageId("123").withDosageDate(DateUtility.addDaysToLocalDate(now, -1)).withDosageStatus(DosageStatus.TAKEN).build();
        DosageAdherenceLog log2 = new DosageAdherenceLogBuilder().withDefaults().withDosageId("123").withDosageDate(DateUtility.addDaysToLocalDate(now, -10)).withDosageStatus(DosageStatus.WILL_TAKE_LATER).build();
        DosageAdherenceLog log3 = new DosageAdherenceLogBuilder().withDefaults().withDosageId("222").withDosageDate(DateUtility.addDaysToLocalDate(now, -20)).withDosageStatus(DosageStatus.TAKEN).build();
        DosageAdherenceLog log4 = new DosageAdherenceLogBuilder().withDefaults().withDosageId("123").withDosageDate(DateUtility.addDaysToLocalDate(now, -50)).withDosageStatus(DosageStatus.NOT_TAKEN).build();
        DosageAdherenceLog log5 = new DosageAdherenceLogBuilder().withDefaults().withDosageId("123").withDosageDate(DateUtility.addDaysToLocalDate(now, -60)).withDosageStatus(DosageStatus.TAKEN).build();
        dosageAdherenceLogs.add(log0);
        dosageAdherenceLogs.add(log1);
        dosageAdherenceLogs.add(log2);
        dosageAdherenceLogs.add(log3);
        dosageAdherenceLogs.add(log4);
        dosageAdherenceLogs.add(log5);

        int count = dosageAdherenceLogs.findScheduledDosagesSuccessCount("123", DateUtility.addDaysToLocalDate(now, -28), now);

        Assert.assertEquals(2, count);
    }

    @Test
    public void shouldGetDosageNotTakenCount() {
        LocalDate now = DateUtility.today();
        DosageAdherenceLog log0 = new DosageAdherenceLogBuilder().withDefaults().withDosageId("123").withDosageDate(now).withDosageStatus(DosageStatus.NOT_TAKEN).build();
        DosageAdherenceLog log1 = new DosageAdherenceLogBuilder().withDefaults().withDosageId("123").withDosageDate(DateUtility.addDaysToLocalDate(now, -1)).withDosageStatus(DosageStatus.TAKEN).build();
        DosageAdherenceLog log2 = new DosageAdherenceLogBuilder().withDefaults().withDosageId("123").withDosageDate(DateUtility.addDaysToLocalDate(now, -10)).withDosageStatus(DosageStatus.WILL_TAKE_LATER).build();
        DosageAdherenceLog log3 = new DosageAdherenceLogBuilder().withDefaults().withDosageId("222").withDosageDate(DateUtility.addDaysToLocalDate(now, -20)).withDosageStatus(DosageStatus.NOT_TAKEN).build();
        DosageAdherenceLog log4 = new DosageAdherenceLogBuilder().withDefaults().withDosageId("123").withDosageDate(DateUtility.addDaysToLocalDate(now, -50)).withDosageStatus(DosageStatus.NOT_TAKEN).build();
        DosageAdherenceLog log5 = new DosageAdherenceLogBuilder().withDefaults().withDosageId("123").withDosageDate(DateUtility.addDaysToLocalDate(now, -60)).withDosageStatus(DosageStatus.TAKEN).build();
        dosageAdherenceLogs.add(log0);
        dosageAdherenceLogs.add(log1);
        dosageAdherenceLogs.add(log2);
        dosageAdherenceLogs.add(log3);
        dosageAdherenceLogs.add(log4);
        dosageAdherenceLogs.add(log5);

        int count = dosageAdherenceLogs.findScheduledDosagesFailureCount("123");

        Assert.assertEquals(2, count);
    }

}
