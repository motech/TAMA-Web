package org.motechproject.tama.dailypillreminder.domain;

import org.joda.time.LocalDate;
import org.junit.Test;
import org.motechproject.model.Time;
import org.motechproject.server.pillreminder.contract.DosageResponse;
import org.motechproject.server.pillreminder.contract.MedicineResponse;
import org.motechproject.util.DateUtil;

import java.util.ArrayList;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class DoseTest {
    @Test
    public void isTakenShouldReturnFalse_ForTheVeryFirstDose() {
        DosageResponse dosageResponse = new DosageResponse("currentDosageId", new Time(22, 5), DateUtil.today(), null, null, new ArrayList<MedicineResponse>());
        Dose dose = new Dose(dosageResponse, DateUtil.today());

        assertFalse(dose.isTaken());
    }

    @Test
    public void isTakenShouldReturnFalse_WhenDosageResponseWasLastCaptured_BeforeTheDoseDate() {
        DosageResponse dosageResponse = new DosageResponse("currentDosageId", new Time(19, 0), new LocalDate(2010, 10, 10), null, new LocalDate(2010, 10, 11), new ArrayList<MedicineResponse>());
        Dose dose = new Dose(dosageResponse, new LocalDate(2010, 10, 12));

        assertFalse(dose.isTaken());
    }

    @Test
    public void isTakenShouldReturnTrue_IfAResponseToTheDosageWasCaptured() {
        DosageResponse dosageResponse = new DosageResponse("currentDosageId", new Time(19, 0), new LocalDate(2010, 10, 10), null, new LocalDate(2010, 10, 10), new ArrayList<MedicineResponse>());
        Dose dose = new Dose(dosageResponse, new LocalDate(2010, 10, 10));

        assertTrue(dose.isTaken());
    }
}
