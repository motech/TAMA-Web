package org.motechproject.tamadatasetup.domain;

import org.junit.Test;
import org.motechproject.model.Time;

import static junit.framework.Assert.assertEquals;

public class ExpectedDailyPillAdherenceTest {
    @Test
    public void onlyMorningDosage() {
        ExpectedDailyPillAdherence pillAdherence = new ExpectedDailyPillAdherence(10, 50, new Time(10, 10), null);
        assertEquals(5, pillAdherence.numberOfDosageTaken());
    }

    @Test
    public void twiceDailyDosage() {
        ExpectedDailyPillAdherence pillAdherence = new ExpectedDailyPillAdherence(10, 50, new Time(10, 10), new Time(20, 20));
        assertEquals(5, pillAdherence.numberOfDosageTaken());
    }
}
