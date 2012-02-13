package org.motechproject.tama.dailypillreminder.domain;

import org.joda.time.DateTime;
import org.junit.Test;
import org.motechproject.model.Time;
import org.motechproject.tama.dailypillreminder.builder.DosageAdherenceLogBuilder;
import org.motechproject.util.DateUtil;

import static junit.framework.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class DosageAdherenceLogTest {

    @Test
    public void dosageTakenLate_isFalseByDefault() {
        assertFalse(new DosageAdherenceLog(null, null, null, null, null, null, null, null).isDosageTakenLate());
    }

    @Test
    public void dosageTakenLate() {
        DosageAdherenceLog dosageAdherenceLog = new DosageAdherenceLog(null, null, null, null, null, null, null, null);
        dosageAdherenceLog.setDosageStatus(DosageStatus.TAKEN);
        dosageAdherenceLog.dosageIsTakenLate();
        assertTrue(dosageAdherenceLog.isDosageTakenLate());
    }

    @Test
    public void dosageTakenLateIsFalseIfDosageNotTaken() {
        DosageAdherenceLog notTakenDose = new DosageAdherenceLog(null, null, null, null, null, null, null, null);
        notTakenDose.setDosageStatus(DosageStatus.NOT_TAKEN);

        notTakenDose.dosageIsTakenLate();
        assertFalse(notTakenDose.isDosageTakenLate());
    }

    @Test
    public void shouldSet_DosageStatusUpdatedOnDate_AsDoseDate_WhenLogIsCreated(){
        Dose dose = mock(Dose.class);
        DateTime tenDaysBack = DateUtil.now().minusDays(10);

        DosageAdherenceLog dosageAdherenceLog = DosageAdherenceLog.create(null, null, null, DosageStatus.TAKEN, dose, tenDaysBack, 15);
        assertEquals(tenDaysBack, dosageAdherenceLog.getDosageStatusUpdatedAt());
    }

    @Test
    public void shouldUpdate_DosageStatusUpdatedOnDate_AsDoseDate_WhenStatusIsUpdated(){
        DosageAdherenceLog dosageAdherenceLog = DosageAdherenceLogBuilder.startRecording().withDefaults().build();
        Dose dose = mock(Dose.class);
        DateTime yesterday = DateUtil.now().minusDays(1);

        dosageAdherenceLog.updateStatus(DosageStatus.TAKEN, yesterday, 15, dose);

        assertEquals(yesterday, dosageAdherenceLog.getDosageStatusUpdatedAt());
    }

    @Test
    public void shouldLogDosageTime() {
        Dose dose = mock(Dose.class);
        when(dose.getDosageHour()).thenReturn(10);
        when(dose.getDosageMinute()).thenReturn(50);
        DosageAdherenceLog takenDose = DosageAdherenceLog.create("patientId", "regimeId", "treatmentAdviceId", DosageStatus.TAKEN, dose, DateUtil.now(), 15);

        assertEquals("treatmentAdviceId", takenDose.getTreatmentAdviceId());
        assertEquals(new Time(10, 50), takenDose.getDosageTime());
    }
}
