package org.motechproject.tama.dailypillreminder.domain;

import org.joda.time.DateTime;
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

    @Test
    public void isLateToTakeDosageShouldReturnTrueIfNowAfterDosageInterval() {
        DosageResponse dosageResponse = new DosageResponse("currentDosageId", new Time(10, 5), new LocalDate(2010, 1, 1), null, null, new ArrayList<MedicineResponse>());
        Dose currentDose = new Dose(dosageResponse, new LocalDate(2010, 10, 10));

        DateTime testCallTime = DateUtil.newDateTime(new LocalDate(2010, 10, 10), 12, 6, 0);
        assertTrue(currentDose.isLateToTake(testCallTime, 15));
    }

    @Test
    public void isLateToTakeDosageShouldReturnFalseIfNowWithinDosageInterval() {
        DosageResponse dosageResponse = new DosageResponse("currentDosageId", new Time(10, 5), new LocalDate(2010, 1, 1), null, null, new ArrayList<MedicineResponse>());
        Dose currentDose = new Dose(dosageResponse, new LocalDate(2010, 10, 10));

        DateTime testCallTime = DateUtil.newDateTime(new LocalDate(2010, 10, 10), 10, 19, 0);
        assertFalse(currentDose.isLateToTake(testCallTime, 15));
    }

    @Test
    public void isEarlyToTakeDosageShouldReturnTrueIfNowBeforeDosageInterval() {
        DosageResponse dosageResponse = new DosageResponse("currentDosageId", new Time(10, 5), new LocalDate(2010, 1, 1), null, null, new ArrayList<MedicineResponse>());
        Dose currentDose = new Dose(dosageResponse, new LocalDate(2010, 10, 10));

        DateTime testCallTime = DateUtil.newDateTime(new LocalDate(2010, 10, 10), 9, 45, 0);
        assertTrue(currentDose.isEarlyToTake(testCallTime, 15));
    }

    @Test
    public void isEarlyToTakeDosageShouldReturnFalseIfWithinDosageInterval() {
        DosageResponse dosageResponse = new DosageResponse("currentDosageId", new Time(10, 5), new LocalDate(2010, 1, 1), null, null, new ArrayList<MedicineResponse>());
        Dose currentDose = new Dose(dosageResponse, new LocalDate(2010, 10, 10));

        DateTime testCallTime = DateUtil.newDateTime(new LocalDate(2010, 10, 10), 10, 10, 0);
        assertFalse(currentDose.isEarlyToTake(testCallTime, 15));
    }

    @Test
    public void shouldReturnTrueIfCallTimeIsWithinDosageInterval() {
        DosageResponse dosageResponse = new DosageResponse("currentDosageId", new Time(21, 0), new LocalDate(2010, 1, 1), null, null, new ArrayList<MedicineResponse>());
        Dose currentDose = new Dose(dosageResponse, new LocalDate(2010, 10, 10));

        DateTime testCallTime = DateUtil.newDateTime(new LocalDate(2010, 10, 10), 20, 50, 0);
        assertTrue(currentDose.isOnTime(testCallTime, 15));
    }

    @Test
    public void shouldReturnFalseIfCallTimeIsNotWithinDosageInterval() {
        DosageResponse dosageResponse = new DosageResponse("currentDosageId", new Time(21, 0), new LocalDate(2010, 1, 1), null, null, new ArrayList<MedicineResponse>());
        Dose currentDose = new Dose(dosageResponse, new LocalDate(2010, 10, 10));

        DateTime testCallTime = DateUtil.newDateTime(new LocalDate(2010, 10, 10), 20, 40, 0);
        assertFalse(currentDose.isOnTime(testCallTime, 15));
    }
}
