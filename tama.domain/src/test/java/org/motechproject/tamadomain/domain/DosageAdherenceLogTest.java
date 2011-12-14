package org.motechproject.tamadomain.domain;

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
        dosageAdherenceLog.dosageIsTakenLate();
        assertTrue(dosageAdherenceLog.isDosageTakenLate());
    }
}
