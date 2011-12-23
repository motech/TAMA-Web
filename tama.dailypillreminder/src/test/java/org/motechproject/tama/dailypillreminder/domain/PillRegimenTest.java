package org.motechproject.tama.dailypillreminder.domain;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.junit.Test;
import org.motechproject.model.Time;
import org.motechproject.server.pillreminder.contract.DosageResponse;
import org.motechproject.server.pillreminder.contract.PillRegimenResponse;

import java.util.ArrayList;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNull;

public class PillRegimenTest {

    @Test
    public void dosagesForLastFourWeeksShouldBe28ForSingleDoseRegimen() {
        final DateTime dosageStartDate = new DateTime(2011, 10, 2, 10, 0, 0, 0);
        PillRegimenResponse pillRegimenResponse = new PillRegimenResponse("regimen_id", "patient_id", 2, 15, new ArrayList<DosageResponse>() {{
            add(new DosageResponse("dosage_id", new Time(dosageStartDate.getHourOfDay(), dosageStartDate.getMinuteOfHour()), dosageStartDate.toLocalDate(), null, null, null));
        }});
        PillRegimen pillRegimen = new PillRegimen(pillRegimenResponse);

        DateTime today = dosageStartDate.plusWeeks(4).minusDays(1);
        assertEquals(28, pillRegimen.getDosesBetween(dosageStartDate.toLocalDate(), today));
    }

    @Test
    public void dosagesShouldBe28ForSingleDoseRegimenStarting4WeeksBack() {
        final DateTime dosageStartDate = new DateTime(2011, 10, 2, 10, 0, 0, 0);
        PillRegimenResponse pillRegimenResponse = new PillRegimenResponse("regimen_id", "patient_id", 2, 15, new ArrayList<DosageResponse>() {{
            add(new DosageResponse("dosage_id", new Time(dosageStartDate.getHourOfDay(), dosageStartDate.getMinuteOfHour()), dosageStartDate.toLocalDate(), null, null, null));
        }});
        PillRegimen pillRegimen = new PillRegimen(pillRegimenResponse);

        DateTime today = dosageStartDate.plusWeeks(4).minusDays(1);
        assertEquals(28, pillRegimen.getNumberOfDosesAsOf(today));
    }

    @Test
    public void dosagesForLastFourWeeksShouldBe51ForTwoDoseRegimen() {
        final DateTime dosage1StartDate = new DateTime(2011, 10, 2, 10, 0, 0, 0);
        final DateTime dosage2StartDate = new DateTime(2011, 10, 7, 13, 0, 0, 0);
        final DateTime today = new DateTime(2011, 10, 30, 13, 0, 0, 0);
        PillRegimenResponse pillRegimenResponse = new PillRegimenResponse("regimen_id", "patient_id", 2, 15, new ArrayList<DosageResponse>() {{
            add(new DosageResponse("dosage1_id", new Time(dosage1StartDate.getHourOfDay(), dosage1StartDate.getMinuteOfHour()), dosage1StartDate.toLocalDate(), null, null, null));
            add(new DosageResponse("dosage2_id", new Time(dosage2StartDate.getHourOfDay(), dosage2StartDate.getMinuteOfHour()), dosage2StartDate.toLocalDate(), null, null, null));
        }});
        PillRegimen pillRegimen = new PillRegimen(pillRegimenResponse);

        assertEquals(51, pillRegimen.getDosesBetween(dosage1StartDate.toLocalDate(), today.minusDays(1)));
    }

    @Test
    public void shouldReturnTodaysDoseWhenPatientIsOnSingleDosage_AndTimeSpecifiedIsAfterPillTime() {
        final DateTime dosage1StartDate = new DateTime(2011, 10, 2, 10, 30, 0, 0);
        PillRegimenResponse pillRegimenResponse = new PillRegimenResponse("regimen_id", "patient_id", 1, 0, new ArrayList<DosageResponse>() {{
            add(new DosageResponse("dosage1_id", new Time(dosage1StartDate.getHourOfDay(), dosage1StartDate.getMinuteOfHour()), dosage1StartDate.toLocalDate(), null, null, null));
        }});
        PillRegimen pillRegimen = new PillRegimen(pillRegimenResponse);

        DateTime givenDateTime = new DateTime(2011, 11, 2, 11, 0, 0, 0);
        Dose dose = pillRegimen.getDoseAt(givenDateTime);

        assertDose("dosage1_id", 10, 30, givenDateTime.toLocalDate(), dose);
    }

    @Test
    public void shouldReturnTodaysDoseWhenPatientIsOnSingleDosage_AndTimeSpecifiedIsBeforePillTime_ButWithinPillWindow() {
        final DateTime dosage1StartDate = new DateTime(2011, 10, 2, 10, 30, 0, 0);
        final int reminderRepeatWindowInHours = 1;
        final int reminderRepeatIntervalInMinutes = 0;
        PillRegimenResponse pillRegimenResponse = new PillRegimenResponse("regimen_id", "patient_id", reminderRepeatWindowInHours, reminderRepeatIntervalInMinutes, new ArrayList<DosageResponse>() {{
            add(new DosageResponse("dosage1_id", new Time(dosage1StartDate.getHourOfDay(), dosage1StartDate.getMinuteOfHour()), dosage1StartDate.toLocalDate(), null, null, null));
        }});
        PillRegimen pillRegimen = new PillRegimen(pillRegimenResponse);

        DateTime givenDateTime = new DateTime(2011, 11, 2, 10, 0, 0, 0);
        Dose dose = pillRegimen.getDoseAt(givenDateTime);

        assertDose("dosage1_id", 10, 30, givenDateTime.toLocalDate(), dose);
    }


    @Test
    public void shouldReturnPreviousDaysDoseWhenPatientIsOnSingleDosage_AndTimeSpecifiedIsBeforePillTime_AndBeforePillWindow() {
        final DateTime dosage1StartDate = new DateTime(2011, 10, 2, 10, 30, 0, 0);
        final int reminderRepeatWindowInHours = 1;
        final int reminderRepeatIntervalInMinutes = 0;
        PillRegimenResponse pillRegimenResponse = new PillRegimenResponse("regimen_id", "patient_id", reminderRepeatWindowInHours, reminderRepeatIntervalInMinutes, new ArrayList<DosageResponse>() {{
            add(new DosageResponse("dosage1_id", new Time(dosage1StartDate.getHourOfDay(), dosage1StartDate.getMinuteOfHour()), dosage1StartDate.toLocalDate(), null, null, null));
        }});
        PillRegimen pillRegimen = new PillRegimen(pillRegimenResponse);

        DateTime givenDateTime = new DateTime(2011, 11, 2, 9, 0, 0, 0);
        Dose dose = pillRegimen.getDoseAt(givenDateTime);

        assertDose("dosage1_id", 10, 30, givenDateTime.minusDays(1).toLocalDate(), dose);
    }

    @Test
    public void shouldReturnTodaysFirstDoseWhenPatientIsOnMultipleDosages_AndTimeSpecifiedIsAfterFirstDosePillTime_ButBeforeSecondDosagesPillWindow() {
        final DateTime dosage1StartDate = new DateTime(2011, 10, 2, 10, 30, 0, 0);
        final DateTime dosage2StartDate = new DateTime(2011, 10, 2, 13, 30, 0, 0);
        final int reminderRepeatWindowInHours = 1;
        final int reminderRepeatIntervalInMinutes = 0;
        PillRegimenResponse pillRegimenResponse = new PillRegimenResponse("regimen_id", "patient_id", reminderRepeatWindowInHours, reminderRepeatIntervalInMinutes, new ArrayList<DosageResponse>() {{
            add(new DosageResponse("dosage1_id", new Time(dosage1StartDate.getHourOfDay(), dosage1StartDate.getMinuteOfHour()), dosage1StartDate.toLocalDate(), null, null, null));
            add(new DosageResponse("dosage2_id", new Time(dosage2StartDate.getHourOfDay(), dosage2StartDate.getMinuteOfHour()), dosage2StartDate.toLocalDate(), null, null, null));
        }});
        PillRegimen pillRegimen = new PillRegimen(pillRegimenResponse);

        DateTime givenDateTime = new DateTime(2011, 11, 2, 12, 0, 0, 0);
        Dose dose = pillRegimen.getDoseAt(givenDateTime);

        assertDose("dosage1_id", 10, 30, givenDateTime.toLocalDate(), dose);
    }

    @Test
    public void shouldReturnTodaysSecondDoseWhenPatientIsOnMultipleDosages_AndTimeSpecifiedIsBeforeSecondDosePillTime_AndWithinSecondDosagesPillWindow() {
        final DateTime dosage1StartDate = new DateTime(2011, 10, 2, 10, 30, 0, 0);
        final DateTime dosage2StartDate = new DateTime(2011, 10, 2, 13, 30, 0, 0);
        final int reminderRepeatWindowInHours = 1;
        final int reminderRepeatIntervalInMinutes = 0;
        PillRegimenResponse pillRegimenResponse = new PillRegimenResponse("regimen_id", "patient_id", reminderRepeatWindowInHours, reminderRepeatIntervalInMinutes, new ArrayList<DosageResponse>() {{
            add(new DosageResponse("dosage2_id", new Time(dosage2StartDate.getHourOfDay(), dosage2StartDate.getMinuteOfHour()), dosage2StartDate.toLocalDate(), null, null, null));
            add(new DosageResponse("dosage1_id", new Time(dosage1StartDate.getHourOfDay(), dosage1StartDate.getMinuteOfHour()), dosage1StartDate.toLocalDate(), null, null, null));
        }});
        PillRegimen pillRegimen = new PillRegimen(pillRegimenResponse);

        DateTime givenDateTime = new DateTime(2011, 11, 2, 12, 45, 0, 0);
        Dose dose = pillRegimen.getDoseAt(givenDateTime);

        assertDose("dosage2_id", 13, 30, givenDateTime.toLocalDate(), dose);
    }

    @Test
    public void shouldReturnTodaysSecondDoseWhenPatientIsOnMultipleDosages_AndTimeSpecifiedIsAfterSecondPillTime() {
        final DateTime dosage1StartDate = new DateTime(2011, 10, 2, 10, 30, 0, 0);
        final DateTime dosage2StartDate = new DateTime(2011, 10, 2, 13, 30, 0, 0);
        final int reminderRepeatWindowInHours = 1;
        final int reminderRepeatIntervalInMinutes = 0;
        PillRegimenResponse pillRegimenResponse = new PillRegimenResponse("regimen_id", "patient_id", reminderRepeatWindowInHours, reminderRepeatIntervalInMinutes, new ArrayList<DosageResponse>() {{
            add(new DosageResponse("dosage2_id", new Time(dosage2StartDate.getHourOfDay(), dosage2StartDate.getMinuteOfHour()), dosage2StartDate.toLocalDate(), null, null, null));
            add(new DosageResponse("dosage1_id", new Time(dosage1StartDate.getHourOfDay(), dosage1StartDate.getMinuteOfHour()), dosage1StartDate.toLocalDate(), null, null, null));
        }});
        PillRegimen pillRegimen = new PillRegimen(pillRegimenResponse);

        DateTime givenDateTime = new DateTime(2011, 11, 2, 18, 30, 0, 0);
        Dose dose = pillRegimen.getDoseAt(givenDateTime);

        assertDose("dosage2_id", 13, 30, givenDateTime.toLocalDate(), dose);
    }

    @Test
    public void shouldReturnPreviousDaysLastDoseWhenPatientIsOnMultipleDosages_AndTimeSpecifiedIsBeforeFirstDosePillWindow() {
        final DateTime dosage1StartDate = new DateTime(2011, 10, 2, 10, 30, 0, 0);
        final DateTime dosage2StartDate = new DateTime(2011, 10, 2, 13, 30, 0, 0);
        final int reminderRepeatWindowInHours = 1;
        final int reminderRepeatIntervalInMinutes = 0;
        PillRegimenResponse pillRegimenResponse = new PillRegimenResponse("regimen_id", "patient_id", reminderRepeatWindowInHours, reminderRepeatIntervalInMinutes, new ArrayList<DosageResponse>() {{
            add(new DosageResponse("dosage2_id", new Time(dosage2StartDate.getHourOfDay(), dosage2StartDate.getMinuteOfHour()), dosage2StartDate.toLocalDate(), null, null, null));
            add(new DosageResponse("dosage1_id", new Time(dosage1StartDate.getHourOfDay(), dosage1StartDate.getMinuteOfHour()), dosage1StartDate.toLocalDate(), null, null, null));
        }});
        PillRegimen pillRegimen = new PillRegimen(pillRegimenResponse);

        DateTime givenDateTime = new DateTime(2011, 11, 2, 8, 30, 0, 0);
        Dose dose = pillRegimen.getDoseAt(givenDateTime);

        assertDose("dosage2_id", 13, 30, givenDateTime.minusDays(1).toLocalDate(), dose);
    }

    @Test
    public void shouldNotReturnDoseWhenPatientIsOnMultipleDosages_AndTimeSpecifiedIsBeforeStartOfEitherDosagesPillWindow() {
        final DateTime dosage1StartDate = new DateTime(2011, 10, 2, 22, 30, 0, 0);
        final DateTime dosage2StartDate = new DateTime(2011, 10, 3, 16, 30, 0, 0);
        final int reminderRepeatWindowInHours = 1;
        final int reminderRepeatIntervalInMinutes = 0;
        PillRegimenResponse pillRegimenResponse = new PillRegimenResponse("regimen_id", "patient_id", reminderRepeatWindowInHours, reminderRepeatIntervalInMinutes, new ArrayList<DosageResponse>() {{
            add(new DosageResponse("dosage2_id", new Time(dosage2StartDate.getHourOfDay(), dosage2StartDate.getMinuteOfHour()), dosage2StartDate.toLocalDate(), null, null, null));
            add(new DosageResponse("dosage1_id", new Time(dosage1StartDate.getHourOfDay(), dosage1StartDate.getMinuteOfHour()), dosage1StartDate.toLocalDate(), null, null, null));
        }});
        PillRegimen pillRegimen = new PillRegimen(pillRegimenResponse);

        DateTime givenDateTime = new DateTime(2011, 10, 2, 10, 0, 0, 0);
        Dose dose = pillRegimen.getDoseAt(givenDateTime);

        assertNull(dose);
    }

    private void assertDose(String expectedDosageId, int expectedDosageHour, int expectedDosageMinute, LocalDate expectedDoseDate, Dose actualDose) {
        assertEquals(expectedDosageId, actualDose.getDosage().getDosageId());
        assertEquals(expectedDosageHour, actualDose.getDosage().getDosageHour());
        assertEquals(expectedDosageMinute, actualDose.getDosage().getDosageMinute());
        assertEquals(expectedDoseDate, actualDose.getDate());
    }
}
