package org.motechproject.tamacallflow.domain;

import org.joda.time.DateTime;
import org.junit.Test;
import org.motechproject.model.Time;
import org.motechproject.server.pillreminder.contract.DosageResponse;
import org.motechproject.server.pillreminder.contract.PillRegimenResponse;

import java.util.ArrayList;

import static junit.framework.Assert.assertEquals;

public class DosageTest {

    @Test
    public void numberOfDosagesShouldBe2() {
        DateTime today = new DateTime(2011, 10, 3, 10, 0, 0, 0);
        final DateTime dosageStartDateTime = today.minusDays(2);
        Dosage dosage = new Dosage(new DosageResponse("dosage_id", new Time(dosageStartDateTime.getHourOfDay(), dosageStartDateTime.getMinuteOfHour()), dosageStartDateTime.toLocalDate(), null, null, null));

        assertEquals(2, dosage.getNumberOfDosagesBetween(dosageStartDateTime, today));
    }

    @Test
    public void numberOfDosagesShouldBe3() {
        DateTime today = new DateTime(2011, 10, 3, 10, 0, 0, 0);
        final DateTime dosageStartDateTime = today.minusDays(3);
        Dosage dosage = new Dosage(new DosageResponse("dosage_id", new Time(dosageStartDateTime.getHourOfDay(), dosageStartDateTime.getMinuteOfHour()), dosageStartDateTime.toLocalDate(), null, null, null));

        assertEquals(3, dosage.getNumberOfDosagesBetween(dosageStartDateTime, today));
    }

    @Test
    public void numberOfdosagesSinceDosageStartShouldBe28() {
        DateTime today = new DateTime(2011, 10, 29, 10, 0, 0, 0);
        final DateTime dosageStartDateTime = today.minusWeeks(4);
        Dosage dosage = new Dosage(new DosageResponse("dosage_id", new Time(dosageStartDateTime.getHourOfDay(), dosageStartDateTime.getMinuteOfHour()), dosageStartDateTime.toLocalDate(), null, null, null));

        assertEquals(28, dosage.getNumberOfDosagesBetween(dosageStartDateTime, today));
    }

    @Test
    public void numberOfdosagesADayAfterDosageStartShouldBe27() {
        DateTime today = new DateTime(2011, 10, 2, 10, 0, 0, 0);
        final DateTime dosageStartDateTime = today.minusWeeks(4);
        Dosage dosage = new Dosage(new DosageResponse("dosage_id", new Time(dosageStartDateTime.getHourOfDay(), dosageStartDateTime.getMinuteOfHour()), dosageStartDateTime.toLocalDate(), null, null, null));

        assertEquals(27, dosage.getNumberOfDosagesBetween(dosageStartDateTime.plusDays(1), today));
    }

    @Test
    public void shouldExcludeDoseBeforeDoseStartTime() {
        DateTime today = new DateTime(2011, 10, 2, 10, 0, 0, 0);
        final DateTime dosageStartDateTime = today.minusWeeks(4);
        Dosage dosage = new Dosage(new DosageResponse("dosage_id", new Time(dosageStartDateTime.getHourOfDay(), dosageStartDateTime.getMinuteOfHour()), dosageStartDateTime.toLocalDate(), null, null, null));

        assertEquals(28, dosage.getNumberOfDosagesBetween(dosageStartDateTime.minusDays(1), today));
    }

    @Test
    public void shouldExcludeDoseAfterDoseEndTime() {
        DateTime today = new DateTime(2011, 10, 29, 10, 0, 0, 0);
        final DateTime dosageStartDateTime = today.minusWeeks(4);
        Dosage dosage = new Dosage(new DosageResponse("dosage_id", new Time(dosageStartDateTime.getHourOfDay(), dosageStartDateTime.getMinuteOfHour()), dosageStartDateTime.toLocalDate(), today.toLocalDate(), null, null));

        assertEquals(28, dosage.getNumberOfDosagesBetween(dosageStartDateTime, today.plusDays(1)));
    }

    @Test
    public void shouldNotFailWhenDoseTimeIsMidnight() {
        DateTime today = new DateTime(2011, 10, 29, 0, 0, 0, 0);
        final DateTime dosageStartDateTime = today.minusWeeks(4);
        Dosage dosage = new Dosage(new DosageResponse("dosage_id", new Time(dosageStartDateTime.getHourOfDay(), dosageStartDateTime.getMinuteOfHour()), dosageStartDateTime.toLocalDate(), null, null, null));

        assertEquals(28, dosage.getNumberOfDosagesBetween(dosageStartDateTime.minusDays(1), today));
    }
}
