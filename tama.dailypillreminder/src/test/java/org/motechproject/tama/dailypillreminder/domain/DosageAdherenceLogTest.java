package org.motechproject.tama.dailypillreminder.domain;

import org.joda.time.DateTime;
import org.junit.Test;
import org.motechproject.tama.dailypillreminder.builder.DosageAdherenceLogBuilder;
import org.motechproject.util.DateUtil;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static org.mockito.Mockito.mock;

public class DosageAdherenceLogTest {

    @Test
    public void dosageTakenLate_isFalseByDefault() {
        assertFalse(new DosageAdherenceLog().isDosageTakenLate());
    }

    @Test
    public void dosageTakenLate() {
        DosageAdherenceLog dosageAdherenceLog = new DosageAdherenceLog();
        dosageAdherenceLog.setDosageStatus(DosageStatus.TAKEN);
        dosageAdherenceLog.dosageIsTakenLate();
        assertTrue(dosageAdherenceLog.isDosageTakenLate());
    }

    @Test
    public void dosageTakenLateIsFalseIfDosageNotTaken() {
        DosageAdherenceLog notTakenDose = new DosageAdherenceLog();
        notTakenDose.setDosageStatus(DosageStatus.NOT_TAKEN);

        notTakenDose.dosageIsTakenLate();
        assertFalse(notTakenDose.isDosageTakenLate());
    }

    @Test
    public void shouldUpdate_DosageStatusUpdatedOnDate_AsDoseDate_WhenStatusIsUpdated(){
        DosageAdherenceLog dosageAdherenceLog = DosageAdherenceLogBuilder.startRecording().withDefaults().build();
        Dose dose = mock(Dose.class);
        DateTime yesterday = DateUtil.now().minusDays(1);

        assertFalse(dosageAdherenceLog.getDosageStatusUpdatedAt().equals(yesterday));

        dosageAdherenceLog.updateStatus(DosageStatus.TAKEN, yesterday, 15, dose);

        assertEquals(yesterday, dosageAdherenceLog.getDosageStatusUpdatedAt());
    }
}
