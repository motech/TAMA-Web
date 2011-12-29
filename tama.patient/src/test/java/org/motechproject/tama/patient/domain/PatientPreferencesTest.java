package org.motechproject.tama.patient.domain;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.junit.Test;
import org.motechproject.model.DayOfWeek;
import org.motechproject.tama.patient.builder.PatientBuilder;

import static junit.framework.Assert.assertEquals;

public class PatientPreferencesTest {

    @Test
    public void shouldGetNextRecallDateTimeWhenPreferredDayIs4DaysAfterWeekStartDate() {
        LocalDate weekStartDate = new LocalDate(2011, 12, 11);
        TimeOfDay bestCallTime = new TimeOfDay(10, 10, TimeMeridiem.AM);
        Patient newPatient = PatientBuilder.startRecording().withWeeklyCallPreference(DayOfWeek.Friday, bestCallTime).build();

        DateTime nextRecall = newPatient.getPatientPreferences().nextRecallOn(weekStartDate);

        assertEquals(new LocalDate(2011, 12, 16), nextRecall.toLocalDate());
        assertEquals(10, nextRecall.getHourOfDay());
        assertEquals(10, nextRecall.getMinuteOfHour());
    }

    @Test
    public void shouldGetNextRecallDateTimeWhenPreferredDayIsWithin4DaysAfterWeekStartDate() {
        LocalDate weekStartDate = new LocalDate(2011, 12, 11);
        TimeOfDay bestCallTime = new TimeOfDay(10, 10, TimeMeridiem.AM);
        Patient newPatient = PatientBuilder.startRecording().withWeeklyCallPreference(DayOfWeek.Tuesday, bestCallTime).build();

        DateTime nextRecall = newPatient.getPatientPreferences().nextRecallOn(weekStartDate);

        assertEquals(new LocalDate(2011, 12, 20), nextRecall.toLocalDate());
        assertEquals(10, nextRecall.getHourOfDay());
        assertEquals(10, nextRecall.getMinuteOfHour());
    }

    @Test
    public void shouldGetRecallDateTimeWhenPreferredDayIsOnTheFourthDayFromWeekStartDate() {
        LocalDate weekStartDate = new LocalDate(2011, 12, 11);
        TimeOfDay bestCallTime = new TimeOfDay(10, 10, TimeMeridiem.PM);
        Patient newPatient = PatientBuilder.startRecording().withWeeklyCallPreference(DayOfWeek.Wednesday, bestCallTime).build();

        DateTime nextRecall = newPatient.getPatientPreferences().nextRecallOn(weekStartDate);

        assertEquals(new LocalDate(2011, 12, 21), nextRecall.toLocalDate());
        assertEquals(22, nextRecall.getHourOfDay());
        assertEquals(10, nextRecall.getMinuteOfHour());
    }
}
