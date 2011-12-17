package org.motechproject.tamacallflow.integration.repository;

import org.joda.time.LocalDate;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.motechproject.tama.common.integration.repository.SpringIntegrationTest;
import org.motechproject.tamacallflow.builder.DosageAdherenceLogBuilder;
import org.motechproject.tamacallflow.domain.DosageAdherenceLog;
import org.motechproject.tamacallflow.domain.DosageStatus;
import org.motechproject.tamacallflow.repository.AllDosageAdherenceLogs;
import org.motechproject.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import java.util.List;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;

@ContextConfiguration(locations = "classpath*:applicationContext-TAMACallFlow.xml", inheritLocations = false)
public class DosageAdherenceLogsTest extends SpringIntegrationTest {
    @Autowired
    private AllDosageAdherenceLogs allDosageAdherenceLogs;

    @Before
    public void before() {
        super.before();
    }

    @After
    public void after() {
        for (DosageAdherenceLog log : allDosageAdherenceLogs.getAll())
            markForDeletion(log);
        super.after();
    }

    @Test
    public void getEmptyLogCount() {
        int count = allDosageAdherenceLogs.findScheduledDosagesSuccessCount("random", DateUtil.today(), DateUtil.today().plusDays(10));
        assertEquals(0, count);
    }

    @Test
    public void shouldSaveDosageAdherenceLogRecord() {
        DosageAdherenceLog dosageAdherenceLog = new DosageAdherenceLogBuilder().withDefaults().build();
        allDosageAdherenceLogs.add(dosageAdherenceLog);

        assertNotNull(dosageAdherenceLog.getId());

        DosageAdherenceLog loadedDosageAdherenceLog = allDosageAdherenceLogs.get(dosageAdherenceLog.getId());
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
        allDosageAdherenceLogs.add(log1);
        allDosageAdherenceLogs.add(log2);
        allDosageAdherenceLogs.add(log3);

        List<DosageAdherenceLog> logs = allDosageAdherenceLogs.findByDosageId("123");
        assertEquals(2, logs.size());
    }

    @Test
    public void shouldGetDosageAdherenceLogsOfPatientForGivenDosageAndDate() {
        LocalDate dosageDate = DateUtil.newDate(2011, 12, 12);
        DosageAdherenceLog log1 = new DosageAdherenceLogBuilder().withDefaults().withDosageId("123").withDosageDate(dosageDate).build();
        DosageAdherenceLog log2 = new DosageAdherenceLogBuilder().withDefaults().withDosageId("123").withDosageDate(DateUtil.newDate(2011, 12, 13)).build();
        allDosageAdherenceLogs.add(log1);
        allDosageAdherenceLogs.add(log2);

        DosageAdherenceLog log = allDosageAdherenceLogs.findByDosageIdAndDate("123", dosageDate);
        assertNotNull(log);
    }

    @Test
    public void shouldGetSuccessCountOfScheduledDosagesForLastFourWeeks() {
        LocalDate today = DateUtil.today();
        DosageAdherenceLog log0 = new DosageAdherenceLogBuilder().withDefaults().withRegimenId("r1").withDosageId("123").withDosageDate(today).withDosageStatus(DosageStatus.TAKEN).build();
        DosageAdherenceLog log1 = new DosageAdherenceLogBuilder().withDefaults().withRegimenId("r1").withDosageId("234").withDosageDate(today).withDosageStatus(DosageStatus.NOT_TAKEN).build();
        DosageAdherenceLog log2 = new DosageAdherenceLogBuilder().withDefaults().withRegimenId("r1").withDosageId("123").withDosageDate(today.minusDays(1)).withDosageStatus(DosageStatus.TAKEN).build();
        DosageAdherenceLog log3 = new DosageAdherenceLogBuilder().withDefaults().withRegimenId("r1").withDosageId("234").withDosageDate(today.minusDays(1)).withDosageStatus(DosageStatus.WILL_TAKE_LATER).build();
        DosageAdherenceLog log4 = new DosageAdherenceLogBuilder().withDefaults().withRegimenId("r2").withDosageId("222").withDosageDate(today.minusDays(20)).withDosageStatus(DosageStatus.TAKEN).build();
        DosageAdherenceLog log5 = new DosageAdherenceLogBuilder().withDefaults().withRegimenId("r1").withDosageId("123").withDosageDate(today.minusDays(50)).withDosageStatus(DosageStatus.TAKEN).build();
        DosageAdherenceLog log6 = new DosageAdherenceLogBuilder().withDefaults().withRegimenId("r1").withDosageId("234").withDosageDate(today.minusDays(50)).withDosageStatus(DosageStatus.TAKEN).build();
        allDosageAdherenceLogs.add(log0);
        allDosageAdherenceLogs.add(log1);
        allDosageAdherenceLogs.add(log2);
        allDosageAdherenceLogs.add(log3);
        allDosageAdherenceLogs.add(log4);
        allDosageAdherenceLogs.add(log5);
        allDosageAdherenceLogs.add(log6);

        int count = allDosageAdherenceLogs.findScheduledDosagesSuccessCount("r1", today.minusDays(28), today);

        Assert.assertEquals(2, count);
    }

    @Test
    public void shouldReturnWhenCurrentDosageWillBeTakenLaterOrNot() {
        LocalDate today = DateUtil.today();
        DosageAdherenceLog log0 = new DosageAdherenceLogBuilder().withDefaults().withRegimenId("r1").withDosageId("123").withDosageDate(today).withDosageStatus(DosageStatus.WILL_TAKE_LATER).build();
        DosageAdherenceLog log1 = new DosageAdherenceLogBuilder().withDefaults().withRegimenId("r1").withDosageId("123").withDosageDate(today.minusDays(1)).withDosageStatus(DosageStatus.TAKEN).build();
        DosageAdherenceLog log2 = new DosageAdherenceLogBuilder().withDefaults().withRegimenId("r1").withDosageId("123").withDosageDate(today.minusDays(2)).withDosageStatus(DosageStatus.NOT_TAKEN).build();
        allDosageAdherenceLogs.add(log0);
        allDosageAdherenceLogs.add(log1);
        allDosageAdherenceLogs.add(log2);

        Assert.assertTrue(allDosageAdherenceLogs.willCurrentDosageBeTakenLater("r1"));
    }

    @Test
    public void shouldConsiderDosageAsTakenWhenCurrentDosageResponseIsWillTakeLater() {
        LocalDate today = DateUtil.today();
        DosageAdherenceLog log0 = new DosageAdherenceLogBuilder().withDefaults().withRegimenId("r1").withDosageId("123").withDosageDate(today).withDosageStatus(DosageStatus.WILL_TAKE_LATER).build();
        DosageAdherenceLog log1 = new DosageAdherenceLogBuilder().withDefaults().withRegimenId("r1").withDosageId("123").withDosageDate(today.minusDays(1)).withDosageStatus(DosageStatus.TAKEN).build();
        DosageAdherenceLog log2 = new DosageAdherenceLogBuilder().withDefaults().withRegimenId("r1").withDosageId("123").withDosageDate(today.minusDays(2)).withDosageStatus(DosageStatus.NOT_TAKEN).build();
        allDosageAdherenceLogs.add(log0);
        allDosageAdherenceLogs.add(log1);
        allDosageAdherenceLogs.add(log2);

        int count = allDosageAdherenceLogs.findScheduledDosagesSuccessCount("r1", today.minusDays(28), today);

        Assert.assertEquals(2, count);
    }

    @Test
    public void shouldGetDosageTakenCount() {
        LocalDate today = DateUtil.today();
        DosageAdherenceLog log0 = new DosageAdherenceLogBuilder().withDefaults().withRegimenId("r1").withDosageId("123").withDosageDate(today).withDosageStatus(DosageStatus.TAKEN).build();
        DosageAdherenceLog log1 = new DosageAdherenceLogBuilder().withDefaults().withRegimenId("r1").withDosageId("234").withDosageDate(today).withDosageStatus(DosageStatus.NOT_TAKEN).build();
        DosageAdherenceLog log2 = new DosageAdherenceLogBuilder().withDefaults().withRegimenId("r1").withDosageId("123").withDosageDate(today.minusDays(1)).withDosageStatus(DosageStatus.TAKEN).build();
        DosageAdherenceLog log3 = new DosageAdherenceLogBuilder().withDefaults().withRegimenId("r1").withDosageId("234").withDosageDate(today.minusDays(1)).withDosageStatus(DosageStatus.WILL_TAKE_LATER).build();
        DosageAdherenceLog log4 = new DosageAdherenceLogBuilder().withDefaults().withRegimenId("r2").withDosageId("222").withDosageDate(today.minusDays(20)).withDosageStatus(DosageStatus.TAKEN).build();
        DosageAdherenceLog log5 = new DosageAdherenceLogBuilder().withDefaults().withRegimenId("r1").withDosageId("123").withDosageDate(today.minusDays(50)).withDosageStatus(DosageStatus.NOT_TAKEN).build();
        DosageAdherenceLog log6 = new DosageAdherenceLogBuilder().withDefaults().withRegimenId("r1").withDosageId("234").withDosageDate(today.minusDays(50)).withDosageStatus(DosageStatus.TAKEN).build();
        allDosageAdherenceLogs.add(log0);
        allDosageAdherenceLogs.add(log1);
        allDosageAdherenceLogs.add(log2);
        allDosageAdherenceLogs.add(log3);
        allDosageAdherenceLogs.add(log4);
        allDosageAdherenceLogs.add(log5);
        allDosageAdherenceLogs.add(log6);

        int count = allDosageAdherenceLogs.getDosageTakenCount("r1");

        Assert.assertEquals(3, count);
    }
}
