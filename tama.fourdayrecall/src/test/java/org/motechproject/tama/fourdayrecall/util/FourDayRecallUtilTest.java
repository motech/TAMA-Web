package org.motechproject.tama.fourdayrecall.util;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;
import org.motechproject.model.DayOfWeek;
import org.motechproject.tama.patient.builder.PatientBuilder;
import org.motechproject.tama.patient.builder.TreatmentAdviceBuilder;
import org.motechproject.tama.patient.domain.Patient;
import org.motechproject.tama.patient.domain.TimeMeridiem;
import org.motechproject.tama.patient.domain.TimeOfDay;
import org.motechproject.tama.patient.domain.TreatmentAdvice;

import static junit.framework.Assert.assertEquals;
import static org.motechproject.tama.fourdayrecall.util.FourDayRecallUtil.getStartDateForWeek;
import static org.motechproject.tama.fourdayrecall.util.FourDayRecallUtil.nextRecallOn;

public class FourDayRecallUtilTest {

    private TreatmentAdvice treatmentAdvice;

    @Before
    public void setUp() {
    }

    @Test
    public void shouldGetTheStartDateForAnySpecifiedDay() {
        treatmentAdvice = TreatmentAdviceBuilder.startRecording().withStartDate(new LocalDate(2011, 11, 6)).build();
        LocalDate startDateForWeek = getStartDateForWeek(new LocalDate(2011, 11, 25), patient(DayOfWeek.Friday), treatmentAdvice);
        assertEquals(new LocalDate(2011, 11, 20), startDateForWeek);
    }

    @Test
    public void shouldGetTheStartDateForWeek_WhenPreferredDayIsSameAsTreatmentStartDay() {
        treatmentAdvice = TreatmentAdviceBuilder.startRecording().withStartDate(new LocalDate(2011, 10, 2)).build();
        LocalDate startDateForCurrentWeek = getStartDateForWeek(new LocalDate(2011, 10, 16), patient(DayOfWeek.Sunday), treatmentAdvice);
        assertEquals(new LocalDate(2011, 10, 9), startDateForCurrentWeek);
    }

    @Test
    public void shouldGetTheStartDateFor_WeekWhenFiveDaysIntoCurrentWeek() {
        treatmentAdvice = TreatmentAdviceBuilder.startRecording().withStartDate(new LocalDate(2011, 10, 2)).build();
        LocalDate startDateForCurrentWeek = getStartDateForWeek(new LocalDate(2011, 10, 13), patient(DayOfWeek.Thursday), treatmentAdvice);
        assertEquals(new LocalDate(2011, 10, 9), startDateForCurrentWeek);
    }

    @Test
    public void shouldGetTheStartDateForWeek_WhenLessThanFiveDaysIntoCurrentWeek() {
        treatmentAdvice = TreatmentAdviceBuilder.startRecording().withStartDate(new LocalDate(2011, 10, 2)).build();
        LocalDate startDateForCurrentWeek = getStartDateForWeek(new LocalDate(2011, 10, 11), patient(DayOfWeek.Thursday), treatmentAdvice);
        assertEquals(new LocalDate(2011, 10, 2), startDateForCurrentWeek);
    }

    @Test
    public void shouldGetTheStartDateForWeek_WhenMoreThanFiveDaysIntoCurrentWeek() {
        treatmentAdvice = TreatmentAdviceBuilder.startRecording().withStartDate(new LocalDate(2011, 10, 2)).build();
        LocalDate startDateForCurrentWeek = getStartDateForWeek(new LocalDate(2011, 10, 14), patient(DayOfWeek.Thursday), treatmentAdvice);
        assertEquals(new LocalDate(2011, 10, 9), startDateForCurrentWeek);
    }

    @Test
    public void shouldGetTheStartDateForWeek_OnFirstRetryDay_AndFiveDaysIntoTheNextWeek() {
        treatmentAdvice = TreatmentAdviceBuilder.startRecording().withStartDate(new LocalDate(2011, 10, 2)).build();
        LocalDate startDateForCurrentWeek = getStartDateForWeek(new LocalDate(2011, 10, 13), patient(DayOfWeek.Wednesday), treatmentAdvice);
        assertEquals(new LocalDate(2011, 10, 2), startDateForCurrentWeek);
    }

    @Test
    public void shouldGetTheStartDateForCurrentWeek_OnSecondRetryDay_AndFiveDaysIntoTheNextWeek() {
        treatmentAdvice = TreatmentAdviceBuilder.startRecording().withStartDate(new LocalDate(2011, 10, 2)).build();
        LocalDate startDateForCurrentWeek = getStartDateForWeek(new LocalDate(2011, 10, 14), patient(DayOfWeek.Wednesday), treatmentAdvice);
        assertEquals(new LocalDate(2011, 10, 2), startDateForCurrentWeek);
    }

    private Patient patient(DayOfWeek dayOfWeek) {
        return PatientBuilder.startRecording().withWeeklyCallPreference(dayOfWeek, null).build();
    }


    @Test
    public void shouldGetNextRecallDateTimeWhenPreferredDayIs4DaysAfterWeekStartDate() {
        LocalDate weekStartDate = new LocalDate(2011, 12, 11);
        TimeOfDay bestCallTime = new TimeOfDay(10, 10, TimeMeridiem.AM);
        Patient patient = PatientBuilder.startRecording().withWeeklyCallPreference(DayOfWeek.Friday, bestCallTime).build();

        DateTime nextRecall = nextRecallOn(weekStartDate, patient);

        assertEquals(new LocalDate(2011, 12, 16), nextRecall.toLocalDate());
        assertEquals(10, nextRecall.getHourOfDay());
        assertEquals(10, nextRecall.getMinuteOfHour());
    }

    @Test
    public void shouldGetNextRecallDateTimeWhenPreferredDayIsWithin4DaysAfterWeekStartDate() {
        LocalDate weekStartDate = new LocalDate(2011, 12, 11);
        TimeOfDay bestCallTime = new TimeOfDay(10, 10, TimeMeridiem.AM);
        Patient patient = PatientBuilder.startRecording().withWeeklyCallPreference(DayOfWeek.Tuesday, bestCallTime).build();

        DateTime nextRecall = nextRecallOn(weekStartDate, patient);

        assertEquals(new LocalDate(2011, 12, 20), nextRecall.toLocalDate());
        assertEquals(10, nextRecall.getHourOfDay());
        assertEquals(10, nextRecall.getMinuteOfHour());
    }

    @Test
    public void shouldGetRecallDateTimeWhenPreferredDayIsOnTheFourthDayFromWeekStartDate() {
        LocalDate weekStartDate = new LocalDate(2011, 12, 11);
        TimeOfDay bestCallTime = new TimeOfDay(10, 10, TimeMeridiem.PM);
        Patient patient = PatientBuilder.startRecording().withWeeklyCallPreference(DayOfWeek.Wednesday, bestCallTime).build();

        DateTime nextRecall = nextRecallOn(weekStartDate, patient);

        assertEquals(new LocalDate(2011, 12, 21), nextRecall.toLocalDate());
        assertEquals(22, nextRecall.getHourOfDay());
        assertEquals(10, nextRecall.getMinuteOfHour());
    }
}
