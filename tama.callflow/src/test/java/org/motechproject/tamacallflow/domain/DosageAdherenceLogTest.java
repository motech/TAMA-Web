package org.motechproject.tamacallflow.domain;

import org.junit.Test;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

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
}
