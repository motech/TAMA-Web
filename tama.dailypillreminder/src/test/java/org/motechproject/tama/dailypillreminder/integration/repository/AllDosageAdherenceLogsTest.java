package org.motechproject.tama.dailypillreminder.integration.repository;

import org.joda.time.LocalDate;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.motechproject.model.Time;
import org.motechproject.tama.common.domain.AdherenceSummaryForAWeek;
import org.motechproject.tama.common.integration.repository.SpringIntegrationTest;
import org.motechproject.tama.dailypillreminder.builder.DosageAdherenceLogBuilder;
import org.motechproject.tama.dailypillreminder.domain.DosageAdherenceLog;
import org.motechproject.tama.dailypillreminder.domain.DosageAdherenceLogPerDay;
import org.motechproject.tama.dailypillreminder.domain.DosageStatus;
import org.motechproject.tama.dailypillreminder.repository.AllDosageAdherenceLogs;
import org.motechproject.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import java.util.List;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static org.mockito.MockitoAnnotations.initMocks;

@ContextConfiguration(locations = "classpath*:applicationDailyPillReminderContext.xml", inheritLocations = false)
public class AllDosageAdherenceLogsTest extends SpringIntegrationTest {

    public static final String PATIENT_ID = "1234";
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
        DosageAdherenceLog adherenceLog = new DosageAdherenceLog(null, null, null, null, null, null, null, null);
        adherenceLog.setRegimenId(regimenId);
        adherenceLog.setDosageStatus(dosageStatus);
        return adherenceLog;
    }

    @Test
    public void shouldGetCountOfDosagesTaken() {
        LocalDate someDay = new LocalDate(2011, 10, 22);
        DosageAdherenceLog dosageAdherenceLog1 = new DosageAdherenceLog("patient_id", "regimen_id", "dosage1_id", "treatment_advice_id", DosageStatus.NOT_TAKEN, someDay, new Time(10, 5), DateUtil.newDateTime(someDay, 0, 0, 0));
        allDosageAdherenceLogs.add(dosageAdherenceLog1);
        DosageAdherenceLog dosageAdherenceLog2 = new DosageAdherenceLog("patient_id", "regimen_id", "dosage1_id", "treatment_advice_id", DosageStatus.TAKEN, someDay.minusDays(1), new Time(10, 5), DateUtil.newDateTime(someDay.minusDays(1), 0, 0, 0));
        allDosageAdherenceLogs.add(dosageAdherenceLog2);
        DosageAdherenceLog dosageAdherenceLog3 = new DosageAdherenceLog("patient_id", "regimen_id", "dosage1_id", "treatment_advice_id", DosageStatus.WILL_TAKE_LATER, someDay.minusDays(2), new Time(10, 5), DateUtil.newDateTime(someDay.minusDays(2), 0, 0, 0));
        allDosageAdherenceLogs.add(dosageAdherenceLog3);
        DosageAdherenceLog dosageAdherenceLog4 = new DosageAdherenceLog("patient_id", "regimen_id", "dosage1_id", "treatment_advice_id", DosageStatus.TAKEN, someDay.minusDays(3), new Time(10, 5), DateUtil.newDateTime(someDay.minusDays(3), 0, 0, 0));
        allDosageAdherenceLogs.add(dosageAdherenceLog4);
        DosageAdherenceLog dosageAdherenceLog5 = new DosageAdherenceLog("patient_id", "regimen_id", "dosage1_id", "treatment_advice_id", DosageStatus.TAKEN, someDay.minusDays(4), new Time(10, 5), DateUtil.newDateTime(someDay.minusDays(4), 0, 0, 0));
        allDosageAdherenceLogs.add(dosageAdherenceLog5);

        assertEquals(3, allDosageAdherenceLogs.getDosageTakenCount("regimen_id"));
    }

    @Test
    public void shouldFindByDosageStatusAndDateRange() {
        LocalDate someDay = new LocalDate(2011, 10, 22);
        DosageAdherenceLog dosageAdherenceLog1 = new DosageAdherenceLog("patient_id", "regimen_id", "dosage1_id", "treatment_advice_id", DosageStatus.NOT_TAKEN, someDay, new Time(10, 5), DateUtil.newDateTime(someDay, 0, 0, 0));
        allDosageAdherenceLogs.add(dosageAdherenceLog1);
        DosageAdherenceLog dosageAdherenceLog2 = new DosageAdherenceLog("patient_id", "regimen_id", "dosage1_id", "treatment_advice_id", DosageStatus.TAKEN, someDay.minusDays(1), new Time(10, 5), DateUtil.newDateTime(someDay.minusDays(1), 0, 0, 0));
        allDosageAdherenceLogs.add(dosageAdherenceLog2);
        DosageAdherenceLog dosageAdherenceLog3 = new DosageAdherenceLog("patient_id", "regimen_id", "dosage1_id", "treatment_advice_id", DosageStatus.WILL_TAKE_LATER, someDay.minusDays(2), new Time(10, 5), DateUtil.newDateTime(someDay.minusDays(2), 0, 0, 0));
        allDosageAdherenceLogs.add(dosageAdherenceLog3);
        DosageAdherenceLog dosageAdherenceLog4 = new DosageAdherenceLog("patient_id", "regimen_id", "dosage1_id", "treatment_advice_id", DosageStatus.TAKEN, someDay.minusDays(3), new Time(10, 5), DateUtil.newDateTime(someDay.minusDays(3), 0, 0, 0));
        allDosageAdherenceLogs.add(dosageAdherenceLog4);
        DosageAdherenceLog dosageAdherenceLog5 = new DosageAdherenceLog("patient_id", "regimen_id", "dosage1_id", "treatment_advice_id", DosageStatus.TAKEN, someDay.minusDays(4), new Time(10, 5), DateUtil.newDateTime(someDay.minusDays(4), 0, 0, 0));
        allDosageAdherenceLogs.add(dosageAdherenceLog5);

        assertEquals(3, allDosageAdherenceLogs.countByDosageStatusAndDate("regimen_id", DosageStatus.TAKEN, someDay.minusDays(5), someDay));
        assertEquals(2, allDosageAdherenceLogs.countByDosageStatusAndDate("regimen_id", DosageStatus.TAKEN, someDay.minusDays(3), someDay));
        assertEquals(0, allDosageAdherenceLogs.countByDosageStatusAndDate("regimen_id", DosageStatus.TAKEN, someDay.minusDays(6), someDay.minusDays(5)));
    }

    @Test
    public void shouldCountAllLogs_ForARegimen_ForGivenDateRange() {
        LocalDate someDay = new LocalDate(2011, 10, 22);
        DosageAdherenceLog dosageAdherenceLog1 = new DosageAdherenceLog("patient_id", "regimen_id", "dosage1_id", "treatment_advice_id", DosageStatus.NOT_TAKEN, someDay, new Time(10, 5), DateUtil.newDateTime(someDay, 0, 0, 0));
        allDosageAdherenceLogs.add(dosageAdherenceLog1);
        DosageAdherenceLog dosageAdherenceLog2 = new DosageAdherenceLog("patient_id", "regimen1_id", "dosage1_id", "treatment_advice_id", DosageStatus.TAKEN, someDay.minusDays(1), new Time(10, 5), DateUtil.newDateTime(someDay.minusDays(1), 0, 0, 0));
        allDosageAdherenceLogs.add(dosageAdherenceLog2);
        DosageAdherenceLog dosageAdherenceLog3 = new DosageAdherenceLog("patient_id", "regimen_id", "dosage1_id", "treatment_advice_id", DosageStatus.WILL_TAKE_LATER, someDay.minusDays(2), new Time(10, 5), DateUtil.newDateTime(someDay.minusDays(2), 0, 0, 0));
        allDosageAdherenceLogs.add(dosageAdherenceLog3);
        DosageAdherenceLog dosageAdherenceLog4 = new DosageAdherenceLog("patient_id", "regimen_id", "dosage1_id", "treatment_advice_id", DosageStatus.NOT_RECORDED, someDay.minusDays(4), new Time(10, 5), DateUtil.newDateTime(someDay.minusDays(4), 0, 0, 0));
        allDosageAdherenceLogs.add(dosageAdherenceLog4);

        assertEquals(3, allDosageAdherenceLogs.countByDosageDate("regimen_id", someDay.minusDays(5), someDay));
    }

    @Test
    public void shouldGetCountOfDoseTakenSinceGivenDate() {
        LocalDate today = DateUtil.today();
        DosageAdherenceLog doseTakenLateBeforeGivenDate = new DosageAdherenceLog("patient_id", "regimen_id", "dosage1_id", "treatment_advice_id", DosageStatus.TAKEN, today.minusWeeks(1), new Time(10, 5), DateUtil.newDateTime(today.minusWeeks(1), 0, 0, 0));
        doseTakenLateBeforeGivenDate.dosageIsTakenLate();
        DosageAdherenceLog doseTakenLate_1 = new DosageAdherenceLog("patient_id", "regimen_id", "dosage1_id", "treatment_advice_id", DosageStatus.TAKEN, today.minusDays(3), new Time(10, 5), DateUtil.newDateTime(today.minusDays(3), 0, 0, 0));
        doseTakenLate_1.dosageIsTakenLate();
        DosageAdherenceLog doseTakenLate_2 = new DosageAdherenceLog("patient_id", "regimen_id", "dosage1_id", "treatment_advice_id", DosageStatus.TAKEN, today.minusDays(1), new Time(10, 5), DateUtil.newDateTime(today.minusDays(1), 0, 0, 0));
        doseTakenLate_2.dosageIsTakenLate();
        DosageAdherenceLog doseTakenLate_3 = new DosageAdherenceLog("patient_id", "regimen_id", "dosage1_id", "treatment_advice_id", DosageStatus.TAKEN, today, new Time(10, 5), DateUtil.newDateTime(today, 0, 0, 0));
        doseTakenLate_3.dosageIsTakenLate();
        DosageAdherenceLog doseTakenOnTime = new DosageAdherenceLog("patient_id", "regimen_id", "dosage1_id", "treatment_advice_id", DosageStatus.TAKEN, today, new Time(10, 5), DateUtil.newDateTime(today, 0, 0, 0));

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

    @Test
    public void shouldGetCountOfDoseTakenAndTotalDosePerWeek() throws Exception {
        LocalDate monday = DateUtil.newDate(2012, 1, 2);
        LocalDate next_monday = DateUtil.newDate(2012, 1, 9);
        DosageAdherenceLog log1_week1 = createLog(monday, DosageStatus.TAKEN);
        DosageAdherenceLog log2_week1 = createLog(monday.plusDays(2), DosageStatus.TAKEN);
        DosageAdherenceLog log3_week1 = createLog(monday.plusDays(4), DosageStatus.NOT_RECORDED);

        DosageAdherenceLog log1_week2 = createLog(next_monday, DosageStatus.NOT_RECORDED);
        DosageAdherenceLog log2_week2 = createLog(next_monday.plusDays(2), DosageStatus.TAKEN);

        DosageAdherenceLog log1_week3 = createLog(next_monday.plusDays(7), DosageStatus.TAKEN);

        List<AdherenceSummaryForAWeek> data = allDosageAdherenceLogs.getDoseTakenSummaryPerWeek(PATIENT_ID);
        
        assertEquals(monday, data.get(0).getWeekStartDate().toLocalDate());
        assertEquals(2, data.get(0).getTaken());
        assertEquals(3, data.get(0).getTotal());

        assertEquals(next_monday, data.get(1).getWeekStartDate().toLocalDate());
        assertEquals(1, data.get(1).getTaken());
        assertEquals(2, data.get(1).getTotal());

        assertEquals(next_monday.plusDays(7), data.get(2).getWeekStartDate().toLocalDate());
        assertEquals(1, data.get(2).getTaken());
        assertEquals(1, data.get(2).getTotal());
    }

    @Test
    // test - rereduce of getPillsTakenAndTotalCountPerWeek
    public void shouldGetCountOfDoseTakenAndTotalDosePerWeek_GivenThatThereAreMoreThan2LogsPerDay() throws Exception {
        LocalDate monday = DateUtil.newDate(2012, 1, 2);
        for (int i = 0; i < 30; i++) {
            DosageAdherenceLog log1_week1 = createLog(monday, DosageStatus.TAKEN);
            DosageAdherenceLog log2_week1 = createLog(monday.plusDays(2), DosageStatus.NOT_TAKEN);
            DosageAdherenceLog log3_week1 = createLog(monday.plusDays(4), DosageStatus.NOT_RECORDED);
        }

        List<AdherenceSummaryForAWeek> data = allDosageAdherenceLogs.getDoseTakenSummaryPerWeek(PATIENT_ID);

        assertEquals(monday, data.get(0).getWeekStartDate().toLocalDate());
        assertEquals(30, data.get(0).getTaken());
        assertEquals(90, data.get(0).getTotal());
    }

    @Test
    public void shouldGetDALGroupedByDate_ScopedByGivenDateRange(){
        LocalDate day1 = DateUtil.newDate(2012, 1, 2);
        LocalDate day2 = DateUtil.newDate(2012, 1, 9);
        LocalDate day3 = DateUtil.newDate(2012, 1, 10);
        DosageAdherenceLog log1_day1 = createLog(day1, DosageStatus.TAKEN);
        DosageAdherenceLog log2_day1 = createLog(day1, DosageStatus.NOT_TAKEN);

        DosageAdherenceLog log1_day2 = createLog(day2, DosageStatus.TAKEN);
        DosageAdherenceLog log1_day3 = createLog(day3, DosageStatus.TAKEN);

        DosageAdherenceLog otherPatient_log1 = new DosageAdherenceLogBuilder().withDefaults()
                .withPatientId("OtherPatientId").withDosageDate(day3).build();
        allDosageAdherenceLogs.add(otherPatient_log1);

        LocalDate startDate = day1;
        LocalDate endDate = day2;
        List<DosageAdherenceLogPerDay> dosageAdherenceLogsPerDay = allDosageAdherenceLogs.getLogsPerDay(PATIENT_ID, startDate, endDate);

        assertEquals(2, dosageAdherenceLogsPerDay.size());
        assertEquals(day2, dosageAdherenceLogsPerDay.get(0).getDate());
        assertEquals(day1, dosageAdherenceLogsPerDay.get(1).getDate());

        assertEquals(1, dosageAdherenceLogsPerDay.get(0).getLogs().size());
        assertEquals(2, dosageAdherenceLogsPerDay.get(1).getLogs().size());

        assertEquals(new Time(10, 45), dosageAdherenceLogsPerDay.get(0).getLogs().get(0).getDosageTime());
    }

    private DosageAdherenceLog createLog(LocalDate date, DosageStatus status) {
        DosageAdherenceLog log0 = new DosageAdherenceLogBuilder().
                withDefaults().
                withPatientId(PATIENT_ID).
                withRegimenId("r1").
                withDosageId("123").
                withDosageDate(date).
                withDosageStatus(status).
                withDosageTime(new Time(10, 45)).build();
        allDosageAdherenceLogs.add(log0);
        return log0;
    }
}
