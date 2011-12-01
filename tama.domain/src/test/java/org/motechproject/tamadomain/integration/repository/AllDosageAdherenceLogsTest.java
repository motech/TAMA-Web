package org.motechproject.tamadomain.integration.repository;

import org.joda.time.LocalDate;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.motechproject.tamacommon.integration.repository.SpringIntegrationTest;
import org.motechproject.tamadomain.domain.DosageAdherenceLog;
import org.motechproject.tamadomain.domain.DosageStatus;
import org.motechproject.tamadomain.repository.AllDosageAdherenceLogs;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.mockito.MockitoAnnotations.initMocks;

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

        markForDeletion(dosageAdherenceLog1, dosageAdherenceLog2, dosageAdherenceLog3, dosageAdherenceLog4);
        assertEquals(1, allDosageAdherenceLogs.getDosageTakenCount("1"));
    }

    private DosageAdherenceLog adherenceLog(String regimenId, DosageStatus dosageStatus) {
        DosageAdherenceLog adherenceLog = new DosageAdherenceLog();
        adherenceLog.setRegimenId(regimenId);
        adherenceLog.setDosageStatus(dosageStatus);
        return adherenceLog;
    }

    @Test
    public void shouldGetCountOfDosagesTaken() {
        LocalDate someDay = new LocalDate(2011, 10, 22);
        DosageAdherenceLog dosageAdherenceLog1 = new DosageAdherenceLog("patient_id", "regimen_id", "dosage1_id", DosageStatus.NOT_TAKEN, someDay);
        allDosageAdherenceLogs.add(dosageAdherenceLog1);
        DosageAdherenceLog dosageAdherenceLog2 = new DosageAdherenceLog("patient_id", "regimen_id", "dosage1_id", DosageStatus.TAKEN, someDay.minusDays(1));
        allDosageAdherenceLogs.add(dosageAdherenceLog2);
        DosageAdherenceLog dosageAdherenceLog3 = new DosageAdherenceLog("patient_id", "regimen_id", "dosage1_id", DosageStatus.WILL_TAKE_LATER, someDay.minusDays(2));
        allDosageAdherenceLogs.add(dosageAdherenceLog3);
        DosageAdherenceLog dosageAdherenceLog4 = new DosageAdherenceLog("patient_id", "regimen_id", "dosage1_id", DosageStatus.TAKEN, someDay.minusDays(3));
        allDosageAdherenceLogs.add(dosageAdherenceLog4);
        DosageAdherenceLog dosageAdherenceLog5 = new DosageAdherenceLog("patient_id", "regimen_id", "dosage1_id", DosageStatus.TAKEN, someDay.minusDays(4));
        allDosageAdherenceLogs.add(dosageAdherenceLog5);
        markForDeletion(dosageAdherenceLog1, dosageAdherenceLog2, dosageAdherenceLog3, dosageAdherenceLog4, dosageAdherenceLog5);

        assertEquals(3, allDosageAdherenceLogs.getDosageTakenCount("regimen_id"));
    }

    @Test
    public void shouldFindByDosageStatusAndDateRange() {
        LocalDate someDay = new LocalDate(2011, 10, 22);
        DosageAdherenceLog dosageAdherenceLog1 = new DosageAdherenceLog("patient_id", "regimen_id", "dosage1_id", DosageStatus.NOT_TAKEN, someDay);
        allDosageAdherenceLogs.add(dosageAdherenceLog1);
        DosageAdherenceLog dosageAdherenceLog2 = new DosageAdherenceLog("patient_id", "regimen_id", "dosage1_id", DosageStatus.TAKEN, someDay.minusDays(1));
        allDosageAdherenceLogs.add(dosageAdherenceLog2);
        DosageAdherenceLog dosageAdherenceLog3 = new DosageAdherenceLog("patient_id", "regimen_id", "dosage1_id", DosageStatus.WILL_TAKE_LATER, someDay.minusDays(2));
        allDosageAdherenceLogs.add(dosageAdherenceLog3);
        DosageAdherenceLog dosageAdherenceLog4 = new DosageAdherenceLog("patient_id", "regimen_id", "dosage1_id", DosageStatus.TAKEN, someDay.minusDays(3));
        allDosageAdherenceLogs.add(dosageAdherenceLog4);
        DosageAdherenceLog dosageAdherenceLog5 = new DosageAdherenceLog("patient_id", "regimen_id", "dosage1_id", DosageStatus.TAKEN, someDay.minusDays(4));
        allDosageAdherenceLogs.add(dosageAdherenceLog5);
        markForDeletion(dosageAdherenceLog1, dosageAdherenceLog2, dosageAdherenceLog3, dosageAdherenceLog4, dosageAdherenceLog5);

        assertArrayEquals(new DosageAdherenceLog[]{ dosageAdherenceLog5, dosageAdherenceLog4, dosageAdherenceLog2 }, allDosageAdherenceLogs.findByStatusAndDateRange(DosageStatus.TAKEN, someDay.minusDays(5), someDay).toArray());

        assertArrayEquals(new DosageAdherenceLog[]{ dosageAdherenceLog4, dosageAdherenceLog2 }, allDosageAdherenceLogs.findByStatusAndDateRange(DosageStatus.TAKEN, someDay.minusDays(3), someDay).toArray());
    }
}
