package org.motechproject.tama.dailypillreminder.integration.repository;

import org.joda.time.LocalDate;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.motechproject.tama.common.integration.repository.SpringIntegrationTest;
import org.motechproject.tama.dailypillreminder.builder.DosageAdherenceLogBuilder;
import org.motechproject.tama.dailypillreminder.domain.DosageAdherenceLog;
import org.motechproject.tama.dailypillreminder.domain.DosageStatus;
import org.motechproject.tama.dailypillreminder.repository.AllDosageAdherenceLogs;
import org.motechproject.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static org.mockito.MockitoAnnotations.initMocks;

@ContextConfiguration(locations = "classpath*:applicationDailyPillReminderContext.xml", inheritLocations = false)
public class AllDosageAdherenceLogsTest extends SpringIntegrationTest {

    @Autowired
    private AllDosageAdherenceLogs allDosageAdherenceLogs;

    @Before
    public void before() {
        super.before();
        initMocks(this);
    }

    @After
    public void after() {
        for (DosageAdherenceLog log : allDosageAdherenceLogs.getAll())
            markForDeletion(log);
        super.after();
    }

    @Test
    public void shouldLoadPatientByRegimenIdAndDosageStatus() {
        DosageAdherenceLog dosageAdherenceLog1 = adherenceLog("1", DosageStatus.NOT_TAKEN);
        DosageAdherenceLog dosageAdherenceLog2 = adherenceLog("1", DosageStatus.TAKEN);
        DosageAdherenceLog dosageAdherenceLog3 = adherenceLog("1", DosageStatus.WILL_TAKE_LATER);
        DosageAdherenceLog dosageAdherenceLog4 = adherenceLog("2", DosageStatus.TAKEN);
        allDosageAdherenceLogs.add(dosageAdherenceLog1);
        allDosageAdherenceLogs.add(dosageAdherenceLog2);
        allDosageAdherenceLogs.add(dosageAdherenceLog3);
        allDosageAdherenceLogs.add(dosageAdherenceLog4);

        assertEquals(1, allDosageAdherenceLogs.getDosageTakenCount("1"));
    }

    private DosageAdherenceLog adherenceLog(String regimenId, DosageStatus dosageStatus) {
        DosageAdherenceLog adherenceLog = new DosageAdherenceLog(null, null, null, null, null, null);
        adherenceLog.setRegimenId(regimenId);
        adherenceLog.setDosageStatus(dosageStatus);
        return adherenceLog;
    }

    @Test
    public void shouldGetCountOfDosagesTaken() {
        LocalDate someDay = new LocalDate(2011, 10, 22);
        DosageAdherenceLog dosageAdherenceLog1 = new DosageAdherenceLog("patient_id", "regimen_id", "dosage1_id", DosageStatus.NOT_TAKEN, someDay, DateUtil.newDateTime(someDay, 0, 0, 0));
        allDosageAdherenceLogs.add(dosageAdherenceLog1);
        DosageAdherenceLog dosageAdherenceLog2 = new DosageAdherenceLog("patient_id", "regimen_id", "dosage1_id", DosageStatus.TAKEN, someDay.minusDays(1), DateUtil.newDateTime(someDay.minusDays(1), 0, 0, 0));
        allDosageAdherenceLogs.add(dosageAdherenceLog2);
        DosageAdherenceLog dosageAdherenceLog3 = new DosageAdherenceLog("patient_id", "regimen_id", "dosage1_id", DosageStatus.WILL_TAKE_LATER, someDay.minusDays(2), DateUtil.newDateTime(someDay.minusDays(2), 0, 0, 0));
        allDosageAdherenceLogs.add(dosageAdherenceLog3);
        DosageAdherenceLog dosageAdherenceLog4 = new DosageAdherenceLog("patient_id", "regimen_id", "dosage1_id", DosageStatus.TAKEN, someDay.minusDays(3), DateUtil.newDateTime(someDay.minusDays(3), 0, 0, 0));
        allDosageAdherenceLogs.add(dosageAdherenceLog4);
        DosageAdherenceLog dosageAdherenceLog5 = new DosageAdherenceLog("patient_id", "regimen_id", "dosage1_id", DosageStatus.TAKEN, someDay.minusDays(4), DateUtil.newDateTime(someDay.minusDays(4), 0, 0, 0));
        allDosageAdherenceLogs.add(dosageAdherenceLog5);

        assertEquals(3, allDosageAdherenceLogs.getDosageTakenCount("regimen_id"));
    }

    @Test
    public void shouldFindByDosageStatusAndDateRange() {
        LocalDate someDay = new LocalDate(2011, 10, 22);
        DosageAdherenceLog dosageAdherenceLog1 = new DosageAdherenceLog("patient_id", "regimen_id", "dosage1_id", DosageStatus.NOT_TAKEN, someDay, DateUtil.newDateTime(someDay, 0, 0, 0));
        allDosageAdherenceLogs.add(dosageAdherenceLog1);
        DosageAdherenceLog dosageAdherenceLog2 = new DosageAdherenceLog("patient_id", "regimen_id", "dosage1_id", DosageStatus.TAKEN, someDay.minusDays(1), DateUtil.newDateTime(someDay.minusDays(1), 0, 0, 0));
        allDosageAdherenceLogs.add(dosageAdherenceLog2);
        DosageAdherenceLog dosageAdherenceLog3 = new DosageAdherenceLog("patient_id", "regimen_id", "dosage1_id", DosageStatus.WILL_TAKE_LATER, someDay.minusDays(2), DateUtil.newDateTime(someDay.minusDays(2), 0, 0, 0));
        allDosageAdherenceLogs.add(dosageAdherenceLog3);
        DosageAdherenceLog dosageAdherenceLog4 = new DosageAdherenceLog("patient_id", "regimen_id", "dosage1_id", DosageStatus.TAKEN, someDay.minusDays(3), DateUtil.newDateTime(someDay.minusDays(3), 0, 0, 0));
        allDosageAdherenceLogs.add(dosageAdherenceLog4);
        DosageAdherenceLog dosageAdherenceLog5 = new DosageAdherenceLog("patient_id", "regimen_id", "dosage1_id", DosageStatus.TAKEN, someDay.minusDays(4), DateUtil.newDateTime(someDay.minusDays(4), 0, 0, 0));
        allDosageAdherenceLogs.add(dosageAdherenceLog5);

        assertEquals(3, allDosageAdherenceLogs.countByDosageStatusAndDate("regimen_id", DosageStatus.TAKEN, someDay.minusDays(5), someDay));
        assertEquals(2, allDosageAdherenceLogs.countByDosageStatusAndDate("regimen_id", DosageStatus.TAKEN, someDay.minusDays(3), someDay));
        assertEquals(0, allDosageAdherenceLogs.countByDosageStatusAndDate("regimen_id", DosageStatus.TAKEN, someDay.minusDays(6), someDay.minusDays(5)));
    }

    @Test
    public void shouldCountAllLogs_ForARegimen_ForGivenDateRange() {
        LocalDate someDay = new LocalDate(2011, 10, 22);
        DosageAdherenceLog dosageAdherenceLog1 = new DosageAdherenceLog("patient_id", "regimen_id", "dosage1_id", DosageStatus.NOT_TAKEN, someDay, DateUtil.newDateTime(someDay, 0, 0, 0));
        allDosageAdherenceLogs.add(dosageAdherenceLog1);
        DosageAdherenceLog dosageAdherenceLog2 = new DosageAdherenceLog("patient_id", "regimen1_id", "dosage1_id", DosageStatus.TAKEN, someDay.minusDays(1), DateUtil.newDateTime(someDay.minusDays(1), 0, 0, 0));
        allDosageAdherenceLogs.add(dosageAdherenceLog2);
        DosageAdherenceLog dosageAdherenceLog3 = new DosageAdherenceLog("patient_id", "regimen_id", "dosage1_id", DosageStatus.WILL_TAKE_LATER, someDay.minusDays(2), DateUtil.newDateTime(someDay.minusDays(2), 0, 0, 0));
        allDosageAdherenceLogs.add(dosageAdherenceLog3);
        DosageAdherenceLog dosageAdherenceLog4 = new DosageAdherenceLog("patient_id", "regimen_id", "dosage1_id", DosageStatus.NOT_RECORDED, someDay.minusDays(4), DateUtil.newDateTime(someDay.minusDays(4), 0, 0, 0));
        allDosageAdherenceLogs.add(dosageAdherenceLog4);

        assertEquals(3, allDosageAdherenceLogs.countByDosageDate("regimen_id", someDay.minusDays(5), someDay));
    }

    @Test
    public void shouldGetCountOfDoseTakenSinceGivenDate() {
        LocalDate today = DateUtil.today();
        DosageAdherenceLog doseTakenLateBeforeGivenDate = new DosageAdherenceLog("patient_id", "regimen_id", "dosage1_id", DosageStatus.TAKEN, today.minusWeeks(1), DateUtil.newDateTime(today.minusWeeks(1), 0, 0, 0));
        doseTakenLateBeforeGivenDate.dosageIsTakenLate();
        DosageAdherenceLog doseTakenLate_1 = new DosageAdherenceLog("patient_id", "regimen_id", "dosage1_id", DosageStatus.TAKEN, today.minusDays(3), DateUtil.newDateTime(today.minusDays(3), 0, 0, 0));
        doseTakenLate_1.dosageIsTakenLate();
        DosageAdherenceLog doseTakenLate_2 = new DosageAdherenceLog("patient_id", "regimen_id", "dosage1_id", DosageStatus.TAKEN, today.minusDays(1), DateUtil.newDateTime(today.minusDays(1), 0, 0, 0));
        doseTakenLate_2.dosageIsTakenLate();
        DosageAdherenceLog doseTakenLate_3 = new DosageAdherenceLog("patient_id", "regimen_id", "dosage1_id", DosageStatus.TAKEN, today, DateUtil.newDateTime(today, 0, 0, 0));
        doseTakenLate_3.dosageIsTakenLate();
        DosageAdherenceLog doseTakenOnTime = new DosageAdherenceLog("patient_id", "regimen_id", "dosage1_id", DosageStatus.TAKEN, today, DateUtil.newDateTime(today, 0, 0, 0));

        allDosageAdherenceLogs.add(doseTakenLateBeforeGivenDate);
        allDosageAdherenceLogs.add(doseTakenLate_1);
        allDosageAdherenceLogs.add(doseTakenOnTime);
        allDosageAdherenceLogs.add(doseTakenLate_2);
        allDosageAdherenceLogs.add(doseTakenLate_3);

        assertEquals(3, allDosageAdherenceLogs.getDoseTakenLateCount("regimen_id", today.minusWeeks(1).plusDays(1), true));
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
