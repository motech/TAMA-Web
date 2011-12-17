package org.motechproject.tamacallflow.domain;

import org.joda.time.DateTime;
import org.junit.Test;
import org.motechproject.model.Time;
import org.motechproject.server.pillreminder.contract.DosageResponse;

import static junit.framework.Assert.assertEquals;

public class DosageTest {

    @Test
    public void totalDosesInAWeek() {
        DateTime now = new DateTime(2011, 10, 3, 10, 0, 0, 0);
        final DateTime dosageStartTime = now.minusWeeks(1);
        final DateTime dosageEndTime = now.plusDays(1);
        Dosage dosage = new Dosage(new DosageResponse("dosage_id", new Time(dosageStartTime.getHourOfDay(), dosageStartTime.getMinuteOfHour()), dosageStartTime.toLocalDate(), dosageEndTime.toLocalDate(), null, null));

        assertEquals(7, dosage.getDosesIn(1, now));
    }

    @Test
    public void totalDosesForDosageStartingYesterday() {
        DateTime now = new DateTime(2011, 10, 3, 10, 0, 0, 0);
        final DateTime dosageStartTime = now.minusDays(1);
        final DateTime dosageEndTime = now.plusDays(1);
        Dosage dosage = new Dosage(new DosageResponse("dosage_id", new Time(dosageStartTime.getHourOfDay(), dosageStartTime.getMinuteOfHour()), dosageStartTime.toLocalDate(), dosageEndTime.toLocalDate(), null, null));

        assertEquals(2, dosage.getNumberOfDosesBetween(dosageStartTime, now));
    }

    @Test
    public void totalDosesWhenStartTimeIsBeforeDosageStartTime() {
        DateTime now = new DateTime(2011, 10, 3, 10, 0, 0, 0);
        final DateTime dosageStartTime = now.minusDays(1);
        final DateTime startTime = dosageStartTime.minusDays(1);
        final DateTime dosageEndTime = now.plusDays(1);
        Dosage dosage = new Dosage(new DosageResponse("dosage_id", new Time(dosageStartTime.getHourOfDay(), dosageStartTime.getMinuteOfHour()), dosageStartTime.toLocalDate(), dosageEndTime.toLocalDate(), null, null));

        assertEquals(2, dosage.getNumberOfDosesBetween(startTime, now));
    }

    @Test
    public void totalDosesWhenEndTimeIsGreaterThanDosageEndTime() {
        DateTime now = new DateTime(2011, 10, 3, 10, 0, 0, 0);
        final DateTime dosageStartTime = now;
        final DateTime dosageEndTime = now.plusDays(1);
        final DateTime endTime = dosageEndTime.plusDays(1);
        Dosage dosage = new Dosage(new DosageResponse("dosage_id", new Time(dosageStartTime.getHourOfDay(), dosageStartTime.getMinuteOfHour()), dosageStartTime.toLocalDate(), dosageEndTime.toLocalDate(), null, null));

        assertEquals(2, dosage.getNumberOfDosesBetween(dosageStartTime, endTime));
    }

    @Test
    public void totalDosesWhenEndTimeIsNull() {
        DateTime now = new DateTime(2011, 10, 3, 10, 0, 0, 0);
        final DateTime dosageStartTime = now.minusDays(1);
        Dosage dosage = new Dosage(new DosageResponse("dosage_id", new Time(dosageStartTime.getHourOfDay(), dosageStartTime.getMinuteOfHour()), dosageStartTime.toLocalDate(), null, null, null));

        assertEquals(2, dosage.getNumberOfDosesBetween(dosageStartTime, now));
    }

    @Test
    public void totalDosesWhenDosageStartDateEqualsDosageEndDate() {
        DateTime now = new DateTime(2011, 10, 2, 10, 0, 0, 0);
        final DateTime dosageStartTime = now;
        final DateTime dosageEndTime = dosageStartTime;
        Dosage dosage = new Dosage(new DosageResponse("dosage_id", new Time(dosageStartTime.getHourOfDay(), dosageStartTime.getMinuteOfHour()), dosageStartTime.toLocalDate(), dosageEndTime.toLocalDate(), null, null));

        assertEquals(1, dosage.getNumberOfDosesBetween(dosageStartTime, dosageEndTime));
    }
}
