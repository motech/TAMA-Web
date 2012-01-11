package org.motechproject.tama.fourdayrecall.service;

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

import static org.junit.Assert.assertEquals;


public class FourDayRecallDateServiceTest {

    private TreatmentAdvice treatmentAdvice;
    private FourDayRecallDateService fourDayRecallDateService;

    @Before
    public void setUp() {
        fourDayRecallDateService = new FourDayRecallDateService();
    }

    @Test
    public void shouldGetTheStartDateForAnySpecifiedDay() {
        treatmentAdvice = TreatmentAdviceBuilder.startRecording().withStartDate(new LocalDate(2011, 11, 6)).build();
        LocalDate startDateForWeek = fourDayRecallDateService.treatmentWeekStartDate(new LocalDate(2011, 11, 25), patient(DayOfWeek.Friday), treatmentAdvice);
        assertEquals(new LocalDate(2011, 11, 20), startDateForWeek);
    }

    @Test
    public void shouldGetTheStartDateForWeek_WhenPreferredDayIsSameAsTreatmentStartDay() {
        treatmentAdvice = TreatmentAdviceBuilder.startRecording().withStartDate(new LocalDate(2011, 10, 2)).build();
        LocalDate startDateForCurrentWeek = fourDayRecallDateService.treatmentWeekStartDate(new LocalDate(2011, 10, 16), patient(DayOfWeek.Sunday), treatmentAdvice);
        assertEquals(new LocalDate(2011, 10, 9), startDateForCurrentWeek);
    }

    @Test
    public void shouldGetTheStartDateFor_WeekWhenFiveDaysIntoCurrentWeek() {
        treatmentAdvice = TreatmentAdviceBuilder.startRecording().withStartDate(new LocalDate(2011, 10, 2)).build();
        LocalDate startDateForCurrentWeek = fourDayRecallDateService.treatmentWeekStartDate(new LocalDate(2011, 10, 13), patient(DayOfWeek.Thursday), treatmentAdvice);
        assertEquals(new LocalDate(2011, 10, 9), startDateForCurrentWeek);
    }

    @Test
    public void shouldGetTheStartDateForWeek_WhenLessThanFiveDaysIntoCurrentWeek() {
        treatmentAdvice = TreatmentAdviceBuilder.startRecording().withStartDate(new LocalDate(2011, 10, 2)).build();
        LocalDate startDateForCurrentWeek = fourDayRecallDateService.treatmentWeekStartDate(new LocalDate(2011, 10, 11), patient(DayOfWeek.Thursday), treatmentAdvice);
        assertEquals(new LocalDate(2011, 10, 2), startDateForCurrentWeek);
    }

    @Test
    public void shouldGetTheStartDateForWeek_WhenMoreThanFiveDaysIntoCurrentWeek() {
        treatmentAdvice = TreatmentAdviceBuilder.startRecording().withStartDate(new LocalDate(2011, 10, 2)).build();
        LocalDate startDateForCurrentWeek = fourDayRecallDateService.treatmentWeekStartDate(new LocalDate(2011, 10, 14), patient(DayOfWeek.Thursday), treatmentAdvice);
        assertEquals(new LocalDate(2011, 10, 9), startDateForCurrentWeek);
    }

    @Test
    public void shouldGetTheStartDateForWeek_OnFirstRetryDay_AndFiveDaysIntoTheNextWeek() {
        treatmentAdvice = TreatmentAdviceBuilder.startRecording().withStartDate(new LocalDate(2011, 10, 2)).build();
        LocalDate startDateForCurrentWeek = fourDayRecallDateService.treatmentWeekStartDate(new LocalDate(2011, 10, 13), patient(DayOfWeek.Wednesday), treatmentAdvice);
        assertEquals(new LocalDate(2011, 10, 2), startDateForCurrentWeek);
    }

    @Test
    public void shouldGetTheStartDateForCurrentWeek_OnSecondRetryDay_AndFiveDaysIntoTheNextWeek() {
        treatmentAdvice = TreatmentAdviceBuilder.startRecording().withStartDate(new LocalDate(2011, 10, 2)).build();
        LocalDate startDateForCurrentWeek = fourDayRecallDateService.treatmentWeekStartDate(new LocalDate(2011, 10, 14), patient(DayOfWeek.Wednesday), treatmentAdvice);
        assertEquals(new LocalDate(2011, 10, 2), startDateForCurrentWeek);
    }

    private Patient patient(DayOfWeek dayOfWeek) {
        return PatientBuilder.startRecording().withWeeklyCallPreference(dayOfWeek, new TimeOfDay(10, 0, TimeMeridiem.AM)).build();
    }

    @Test
    public void shouldGetNextRecallDateTimeWhenPreferredDayIs4DaysAfterWeekStartDate() {
        LocalDate weekStartDate = new LocalDate(2011, 12, 11);
        TimeOfDay bestCallTime = new TimeOfDay(10, 10, TimeMeridiem.AM);
        Patient patient = PatientBuilder.startRecording().withWeeklyCallPreference(DayOfWeek.Friday, bestCallTime).build();

        DateTime nextRecall = fourDayRecallDateService.nextRecallOn(weekStartDate, patient);

        assertEquals(new LocalDate(2011, 12, 16), nextRecall.toLocalDate());
        assertEquals(10, nextRecall.getHourOfDay());
        assertEquals(10, nextRecall.getMinuteOfHour());
    }

    @Test
    public void shouldGetNextRecallDateTimeWhenPreferredDayIsWithin4DaysAfterWeekStartDate() {
        LocalDate weekStartDate = new LocalDate(2011, 12, 11);
        TimeOfDay bestCallTime = new TimeOfDay(10, 10, TimeMeridiem.AM);
        Patient patient = PatientBuilder.startRecording().withWeeklyCallPreference(DayOfWeek.Tuesday, bestCallTime).build();

        DateTime nextRecall = fourDayRecallDateService.nextRecallOn(weekStartDate, patient);

        assertEquals(new LocalDate(2011, 12, 20), nextRecall.toLocalDate());
        assertEquals(10, nextRecall.getHourOfDay());
        assertEquals(10, nextRecall.getMinuteOfHour());
    }

    @Test
    public void shouldGetRecallDateTimeWhenPreferredDayIsOnTheFourthDayFromWeekStartDate() {
        LocalDate weekStartDate = new LocalDate(2011, 12, 11);
        TimeOfDay bestCallTime = new TimeOfDay(10, 10, TimeMeridiem.PM);
        Patient patient = PatientBuilder.startRecording().withWeeklyCallPreference(DayOfWeek.Wednesday, bestCallTime).build();

        DateTime nextRecall = fourDayRecallDateService.nextRecallOn(weekStartDate, patient);

        assertEquals(new LocalDate(2011, 12, 21), nextRecall.toLocalDate());
        assertEquals(22, nextRecall.getHourOfDay());
        assertEquals(10, nextRecall.getMinuteOfHour());
    }

    @Test
    public void shouldReturnTreatmentWeekStartDate_WhenItFallsInTheSameWeek() {
        final LocalDate treatmentStartDate = new LocalDate(2012, 1, 2);
        final LocalDate givenDate = new LocalDate(2012, 1, 10);
        final TreatmentAdvice treatmentAdvice = TreatmentAdviceBuilder.startRecording().withDefaults().withStartDate(treatmentStartDate).build();
        final LocalDate weekStartDate = fourDayRecallDateService.treatmentWeekStartDate(givenDate, treatmentAdvice);
        assertEquals(new LocalDate(2012, 1, 9), weekStartDate);
    }

    @Test
    public void shouldReturnTreatmentWeekStartDate_WhenItFallsInThePreviousWeek() {
        final LocalDate treatmentStartDate = new LocalDate(2012, 1, 6);
        final LocalDate givenDate = new LocalDate(2012, 1, 18);
        final TreatmentAdvice treatmentAdvice = TreatmentAdviceBuilder.startRecording().withDefaults().withStartDate(treatmentStartDate).build();
        final LocalDate weekStartDate = fourDayRecallDateService.treatmentWeekStartDate(givenDate, treatmentAdvice);
        assertEquals(new LocalDate(2012, 1, 13), weekStartDate);
    }

    @Test
    public void shouldReturnTreatmentWeekStartDate_WhenItFallsOnTheGivenDate() {
        final LocalDate treatmentStartDate = new LocalDate(2012, 1, 3);
        final LocalDate givenDate = new LocalDate(2012, 1, 10);
        final TreatmentAdvice treatmentAdvice = TreatmentAdviceBuilder.startRecording().withDefaults().withStartDate(treatmentStartDate).build();
        final LocalDate weekStartDate = fourDayRecallDateService.treatmentWeekStartDate(givenDate, treatmentAdvice);
        assertEquals(new LocalDate(2012, 1, 10), weekStartDate);
    }

    @Test
    public void shouldGetTheFirstFourDayRecallDateForTreatmentAdviceStarting2DaysBeforeBestCallDay() {
        Patient patient = PatientBuilder.startRecording().withWeeklyCallPreference(DayOfWeek.Friday, new TimeOfDay(10, 0, TimeMeridiem.AM)).build();
        TreatmentAdvice treatmentAdvice = TreatmentAdviceBuilder.startRecording().withId("treatmentAdviceId").withStartDate(new LocalDate(2011, 11, 9)).build();
        assertEquals(new LocalDate(2011, 11, 18), fourDayRecallDateService.firstRecallDate(patient, treatmentAdvice));
    }

    @Test
    public void shouldGetTheFirstFourDayRecallDateForAnyTreatmentAdviceStarting5DaysBeforeBestCallDay() {
        Patient patient = PatientBuilder.startRecording().withWeeklyCallPreference(DayOfWeek.Friday, new TimeOfDay(10, 0, TimeMeridiem.AM)).build();
        TreatmentAdvice treatmentAdvice = TreatmentAdviceBuilder.startRecording().withId("treatmentAdviceId").withStartDate(new LocalDate(2011, 11, 6)).build();
        assertEquals(new LocalDate(2011, 11, 11), fourDayRecallDateService.firstRecallDate(patient, treatmentAdvice));
    }

    @Test
    public void shouldGetTheFirstFourDayRecallDateForTreatmentAdviceStartsFourDaysBeforeTheBestCallDay() {
        Patient patient = PatientBuilder.startRecording().withWeeklyCallPreference(DayOfWeek.Thursday, new TimeOfDay(10, 0, TimeMeridiem.AM)).build();
        TreatmentAdvice treatmentAdvice = TreatmentAdviceBuilder.startRecording().withId("treatmentAdviceId").withStartDate(new LocalDate(2011, 11, 13)).build();
        assertEquals(new LocalDate(2011, 11, 17), fourDayRecallDateService.firstRecallDate(patient, treatmentAdvice));
    }

    @Test
    public void shouldGetTheFirstFourDayRecallDate_WhenTransitionDateIs2DaysBeforeBestCallDay() {
        Patient patient = PatientBuilder.startRecording().withWeeklyCallPreference(DayOfWeek.Friday, new TimeOfDay(10, 0, TimeMeridiem.AM))
                .withTransitionDate(new LocalDate(2011, 11, 9)).build();
        TreatmentAdvice treatmentAdvice = TreatmentAdviceBuilder.startRecording().withId("treatmentAdviceId").withStartDate(new LocalDate(2011, 11, 3)).build();
        assertEquals(new LocalDate(2011, 11, 18), fourDayRecallDateService.firstRecallDate(patient, treatmentAdvice));
    }

    @Test
    public void shouldGetTheFirstFourDayRecall_WhenTransitionDateIsAfterIs5DaysBeforeBestCallDay() {
        Patient patient = PatientBuilder.startRecording().withWeeklyCallPreference(DayOfWeek.Friday, new TimeOfDay(10, 0, TimeMeridiem.AM)).
                withTransitionDate(new LocalDate(2011, 11, 6)).build();
        TreatmentAdvice treatmentAdvice = TreatmentAdviceBuilder.startRecording().withId("treatmentAdviceId").withStartDate(new LocalDate(2011, 11, 3)).build();
        assertEquals(new LocalDate(2011, 11, 11), fourDayRecallDateService.firstRecallDate(patient, treatmentAdvice));
    }
}
