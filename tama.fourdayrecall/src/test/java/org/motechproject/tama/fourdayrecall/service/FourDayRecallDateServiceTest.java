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
import org.motechproject.testing.utils.BaseUnitTest;
import org.motechproject.util.DateUtil;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static org.junit.Assert.assertEquals;


public class FourDayRecallDateServiceTest extends BaseUnitTest {

    private TreatmentAdvice treatmentAdvice;
    private FourDayRecallDateService fourDayRecallDateService;

    @Before
    public void setUp() {
        fourDayRecallDateService = new FourDayRecallDateService();
    }

    @Test
    public void shouldGetTheStartDateForWeek_WhenBestCallDayIsMoreThan4DaysIntoFirstTreatmentWeek_AndOnFirstRecallDate() {
        LocalDate treatmentAdviceStartDate = new LocalDate(2011, 11, 6);
        treatmentAdvice = TreatmentAdviceBuilder.startRecording().withStartDate(treatmentAdviceStartDate).build();
        LocalDate startDateForWeek = fourDayRecallDateService.treatmentWeekStartDate(treatmentAdviceStartDate.plusDays(5), patient(DayOfWeek.Friday), treatmentAdvice);
        assertEquals(treatmentAdviceStartDate, startDateForWeek);
    }

    @Test
    public void shouldGetTheStartDateForWeek_WhenBestCallDayIsMoreThan4DaysIntoFirstTreatmentWeek_AndOnFirstRetryCallDate() {
        LocalDate treatmentAdviceStartDate = new LocalDate(2011, 11, 6);
        LocalDate bestCallDay = treatmentAdviceStartDate.plusDays(5);
        LocalDate firstRetryDate = bestCallDay.plusDays(1);

        treatmentAdvice = TreatmentAdviceBuilder.startRecording().withStartDate(treatmentAdviceStartDate).build();
        LocalDate startDateForWeek = fourDayRecallDateService.treatmentWeekStartDate(firstRetryDate, patient(DayOfWeek.Friday), treatmentAdvice);
        assertEquals(treatmentAdviceStartDate, startDateForWeek);
    }

    @Test
    public void shouldGetTheStartDateForWeek_WhenBestCallDayIsMoreThan4DaysIntoFirstTreatmentWeek_AndOnSecondRetryCallDate() {
        treatmentAdvice = TreatmentAdviceBuilder.startRecording().withStartDate(new LocalDate(2011, 11, 6)).build();
        LocalDate startDateForWeek = fourDayRecallDateService.treatmentWeekStartDate(new LocalDate(2011, 11, 13), patient(DayOfWeek.Friday), treatmentAdvice);
        assertEquals(new LocalDate(2011, 11, 6), startDateForWeek);
    }

    @Test
    public void shouldGetTheStartDateForWeek_WhenBestCallDayIsMoreThan4DaysIntoFirstTreatmentWeek_AndADayBeforeNextWeekRecallDate() {
        treatmentAdvice = TreatmentAdviceBuilder.startRecording().withStartDate(new LocalDate(2011, 11, 6)).build();
        LocalDate startDateForWeek = fourDayRecallDateService.treatmentWeekStartDate(new LocalDate(2011, 11, 17), patient(DayOfWeek.Friday), treatmentAdvice);
        assertEquals(new LocalDate(2011, 11, 6), startDateForWeek);
    }

    @Test
    public void shouldGetTheStartDateForWeek_WhenBestCallDayIsMoreThan4DaysIntoSecondTreatmentWeek_AndOnNextWeekRecallDate() {
        treatmentAdvice = TreatmentAdviceBuilder.startRecording().withStartDate(new LocalDate(2011, 11, 6)).build();
        LocalDate startDateForWeek = fourDayRecallDateService.treatmentWeekStartDate(new LocalDate(2011, 11, 18), patient(DayOfWeek.Friday), treatmentAdvice);
        assertEquals(new LocalDate(2011, 11, 13), startDateForWeek);
    }

    @Test
    public void shouldGetTheStartDateForWeek_WhenBestCallDayIsLessThan4DaysIntoFirstTreatmentWeek_AndOnFirstRecallDate() {
        treatmentAdvice = TreatmentAdviceBuilder.startRecording().withStartDate(new LocalDate(2011, 11, 6)).build();
        LocalDate startDateForWeek = fourDayRecallDateService.treatmentWeekStartDate(new LocalDate(2011, 11, 15), patient(DayOfWeek.Tuesday), treatmentAdvice);
        assertEquals(new LocalDate(2011, 11, 6), startDateForWeek);
    }

    @Test
    public void shouldGetTheStartDateForWeek_WhenBestCallDayIsLessThan4DaysIntoFirstTreatmentWeek_AndOnFirstRetryCallDate() {
        treatmentAdvice = TreatmentAdviceBuilder.startRecording().withStartDate(new LocalDate(2011, 11, 6)).build();
        LocalDate startDateForWeek = fourDayRecallDateService.treatmentWeekStartDate(new LocalDate(2011, 11, 16), patient(DayOfWeek.Tuesday), treatmentAdvice);
        assertEquals(new LocalDate(2011, 11, 6), startDateForWeek);
    }

    @Test
    public void shouldGetTheStartDateForWeek_WhenBestCallDayIsLessThan4DaysIntoFirstTreatmentWeek_AndOnSecondRetryCallDate() {
        treatmentAdvice = TreatmentAdviceBuilder.startRecording().withStartDate(new LocalDate(2011, 11, 6)).build();
        LocalDate startDateForWeek = fourDayRecallDateService.treatmentWeekStartDate(new LocalDate(2011, 11, 17), patient(DayOfWeek.Tuesday), treatmentAdvice);
        assertEquals(new LocalDate(2011, 11, 6), startDateForWeek);
    }

    @Test
    public void shouldGetTheStartDateForWeek_WhenBestCallDayIsLessThan4DaysIntoFirstTreatmentWeek_AndADayBeforeNextWeekRecallDate() {
        treatmentAdvice = TreatmentAdviceBuilder.startRecording().withStartDate(new LocalDate(2011, 11, 6)).build();
        LocalDate startDateForWeek = fourDayRecallDateService.treatmentWeekStartDate(new LocalDate(2011, 11, 21), patient(DayOfWeek.Tuesday), treatmentAdvice);
        assertEquals(new LocalDate(2011, 11, 6), startDateForWeek);
    }

    @Test
    public void shouldGetTheStartDateForWeek_WhenBestCallDayIsLessThan4DaysIntoSecondTreatmentWeek_AndOnNextWeekRecallDate() {
        treatmentAdvice = TreatmentAdviceBuilder.startRecording().withStartDate(new LocalDate(2011, 11, 6)).build();
        LocalDate startDateForWeek = fourDayRecallDateService.treatmentWeekStartDate(new LocalDate(2011, 11, 22), patient(DayOfWeek.Tuesday), treatmentAdvice);
        assertEquals(new LocalDate(2011, 11, 13), startDateForWeek);
    }

    @Test
    public void shouldGetTheStartDateForWeek_WhenBestCallDayIsLessThan4DaysIntoSecondTreatmentWeek_AndOnSecondRetryDate() {
        treatmentAdvice = TreatmentAdviceBuilder.startRecording().withStartDate(new LocalDate(2011, 11, 6)).build();
        LocalDate startDateForWeek = fourDayRecallDateService.treatmentWeekStartDate(new LocalDate(2011, 11, 24), patient(DayOfWeek.Tuesday), treatmentAdvice);
        assertEquals(new LocalDate(2011, 11, 13), startDateForWeek);
    }

    @Test
    public void shouldGetTheStartDateForWeek_WhenPreferredDayIsSameAsTreatmentStartDay() {
        treatmentAdvice = TreatmentAdviceBuilder.startRecording().withStartDate(new LocalDate(2011, 10, 2)).build();
        LocalDate startDateForCurrentWeek = fourDayRecallDateService.treatmentWeekStartDate(new LocalDate(2011, 10, 16), patient(DayOfWeek.Sunday), treatmentAdvice);
        assertEquals(new LocalDate(2011, 10, 9), startDateForCurrentWeek);
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
    public void shouldGetTheFirstFourDayRecallDate_WhenTransitionDateIsAfterIs5DaysBeforeBestCallDay() {
        Patient patient = PatientBuilder.startRecording().withWeeklyCallPreference(DayOfWeek.Friday, new TimeOfDay(10, 0, TimeMeridiem.AM)).
                withTransitionDate(new LocalDate(2011, 11, 6)).build();
        TreatmentAdvice treatmentAdvice = TreatmentAdviceBuilder.startRecording().withId("treatmentAdviceId").withStartDate(new LocalDate(2011, 11, 3)).build();
        assertEquals(new LocalDate(2011, 11, 11), fourDayRecallDateService.firstRecallDate(patient, treatmentAdvice));
    }

    @Test
    public void shouldGetFirstTreatmentWeekStartDate_WhenThereWasNoCallPlanTransition() {
        Patient patient = PatientBuilder.startRecording().withWeeklyCallPreference(DayOfWeek.Friday, new TimeOfDay(10, 0, TimeMeridiem.AM)).build();
        TreatmentAdvice treatmentAdvice = TreatmentAdviceBuilder.startRecording().withId("treatmentAdviceId").withStartDate(new LocalDate(2011, 11, 3)).build();
        assertEquals(new LocalDate(2011, 11, 3), fourDayRecallDateService.firstTreatmentWeekStartDate(patient, treatmentAdvice));
    }

    @Test
    public void shouldGetFirstTreatmentWeekStartDate_WhenThereWasCallPlanTransition_AndFourDaysBeforeNextBestCallDay() {
        Patient patient = PatientBuilder.startRecording().withWeeklyCallPreference(DayOfWeek.Friday, new TimeOfDay(10, 0, TimeMeridiem.AM)).
                withTransitionDate(new LocalDate(2011, 11, 6)).build();
        TreatmentAdvice treatmentAdvice = TreatmentAdviceBuilder.startRecording().withId("treatmentAdviceId").withStartDate(new LocalDate(2011, 11, 3)).build();
        assertEquals(new LocalDate(2011, 11, 3), fourDayRecallDateService.firstTreatmentWeekStartDate(patient, treatmentAdvice));
    }

    @Test
    public void shouldGetFirstTreatmentWeekStartDate_WhenThereWasCallPlanTransition_AndLessThanFourDaysBeforeNextBestCallDay() {
        Patient patient = PatientBuilder.startRecording().withWeeklyCallPreference(DayOfWeek.Friday, new TimeOfDay(10, 0, TimeMeridiem.AM)).
                withTransitionDate(new LocalDate(2011, 11, 8)).build();
        TreatmentAdvice treatmentAdvice = TreatmentAdviceBuilder.startRecording().withId("treatmentAdviceId").withStartDate(new LocalDate(2011, 11, 3)).build();
        assertEquals(new LocalDate(2011, 11, 10), fourDayRecallDateService.firstTreatmentWeekStartDate(patient, treatmentAdvice));
    }

    @Test
    public void shouldReturnTrueIfCurrentWeekIsFirstWeekOfTreatment_AndTodayIsBeforeFirstRecallDate() {
        mockCurrentDate(DateUtil.newDateTime(new LocalDate(2011, 11, 8), 0, 0, 0));
        Patient patient = PatientBuilder.startRecording().withWeeklyCallPreference(DayOfWeek.Wednesday, new TimeOfDay(10, 0, TimeMeridiem.AM)).build();
        TreatmentAdvice treatmentAdvice = TreatmentAdviceBuilder.startRecording().withId("treatmentAdviceId").withStartDate(new LocalDate(2011, 11, 7)).build();
        assertTrue(fourDayRecallDateService.isFirstTreatmentWeek(patient, treatmentAdvice));
    }

    @Test
    public void shouldReturnTrueIfCurrentWeekIsFirstWeekOfTreatment_AndTodayIsOnFirstRecallDate() {
        mockCurrentDate(DateUtil.newDateTime(new LocalDate(2011, 11, 16), 0, 0, 0));
        Patient patient = PatientBuilder.startRecording().withWeeklyCallPreference(DayOfWeek.Wednesday, new TimeOfDay(10, 0, TimeMeridiem.AM)).build();
        TreatmentAdvice treatmentAdvice = TreatmentAdviceBuilder.startRecording().withId("treatmentAdviceId").withStartDate(new LocalDate(2011, 11, 7)).build();
        assertTrue(fourDayRecallDateService.isFirstTreatmentWeek(patient, treatmentAdvice));
    }

    @Test
    public void shouldReturnTrueIfCurrentWeekIsFirstWeekOfTreatment_AndTodayIsOnSecondRetryDate() {
        mockCurrentDate(DateUtil.newDateTime(new LocalDate(2011, 11, 18), 0, 0, 0));
        Patient patient = PatientBuilder.startRecording().withWeeklyCallPreference(DayOfWeek.Wednesday, new TimeOfDay(10, 0, TimeMeridiem.AM)).build();
        TreatmentAdvice treatmentAdvice = TreatmentAdviceBuilder.startRecording().withId("treatmentAdviceId").withStartDate(new LocalDate(2011, 11, 7)).build();
        assertTrue(fourDayRecallDateService.isFirstTreatmentWeek(patient, treatmentAdvice));
    }

    @Test
    public void shouldReturnTrueIfCurrentWeekIsFirstWeekOfTreatment_AndTodayIsOneDayBeforeNextWeekRecallDate() {
        mockCurrentDate(DateUtil.newDateTime(new LocalDate(2011, 11, 22), 0, 0, 0));
        Patient patient = PatientBuilder.startRecording().withWeeklyCallPreference(DayOfWeek.Wednesday, new TimeOfDay(10, 0, TimeMeridiem.AM)).build();
        TreatmentAdvice treatmentAdvice = TreatmentAdviceBuilder.startRecording().withId("treatmentAdviceId").withStartDate(new LocalDate(2011, 11, 7)).build();
        assertTrue(fourDayRecallDateService.isFirstTreatmentWeek(patient, treatmentAdvice));
    }

    @Test
    public void shouldReturnFalseIfCurrentWeekIsSecondWeekOfTreatment_AndTodayIsSecondWeekRecallDate() {
        mockCurrentDate(DateUtil.newDateTime(new LocalDate(2011, 11, 23), 0, 0, 0));
        Patient patient = PatientBuilder.startRecording().withWeeklyCallPreference(DayOfWeek.Wednesday, new TimeOfDay(10, 0, TimeMeridiem.AM)).build();
        TreatmentAdvice treatmentAdvice = TreatmentAdviceBuilder.startRecording().withId("treatmentAdviceId").withStartDate(new LocalDate(2011, 11, 7)).build();
        assertFalse(fourDayRecallDateService.isFirstTreatmentWeek(patient, treatmentAdvice));
    }

    @Test
    public void shouldReturnTrueIfCurrentWeekIsFirstWeekOfTreatmentAfterTransition_AndTodayIsBeforeFirstRecallDate() {
        mockCurrentDate(DateUtil.newDateTime(new LocalDate(2011, 11, 22), 0, 0, 0));
        Patient patient = PatientBuilder.startRecording().withWeeklyCallPreference(DayOfWeek.Wednesday, new TimeOfDay(10, 0, TimeMeridiem.AM))
                .withTransitionDate(new LocalDate(2011, 11, 15)).build();
        TreatmentAdvice treatmentAdvice = TreatmentAdviceBuilder.startRecording().withId("treatmentAdviceId").withStartDate(new LocalDate(2011, 11, 7)).build();
        assertTrue(fourDayRecallDateService.isFirstTreatmentWeek(patient, treatmentAdvice));
    }

    @Test
    public void shouldReturnTrueIfCurrentWeekIsFirstWeekOfTreatmentAfterTransition_AndTodayIsOnFirstRecallDate() {
        mockCurrentDate(DateUtil.newDateTime(new LocalDate(2011, 11, 23), 0, 0, 0));
        Patient patient = PatientBuilder.startRecording().withWeeklyCallPreference(DayOfWeek.Wednesday, new TimeOfDay(10, 0, TimeMeridiem.AM))
                .withTransitionDate(new LocalDate(2011, 11, 15)).build();
        TreatmentAdvice treatmentAdvice = TreatmentAdviceBuilder.startRecording().withId("treatmentAdviceId").withStartDate(new LocalDate(2011, 11, 7)).build();
        assertTrue(fourDayRecallDateService.isFirstTreatmentWeek(patient, treatmentAdvice));
    }

    @Test
    public void shouldReturnTrueIfCurrentWeekIsFirstWeekOfTreatmentAfterTransition_AndTodayIsOneDayBeforeNextWeekRecallDate() {
        mockCurrentDate(DateUtil.newDateTime(new LocalDate(2011, 11, 29), 0, 0, 0));
        Patient patient = PatientBuilder.startRecording().withWeeklyCallPreference(DayOfWeek.Wednesday, new TimeOfDay(10, 0, TimeMeridiem.AM))
                .withTransitionDate(new LocalDate(2011, 11, 15)).build();
        TreatmentAdvice treatmentAdvice = TreatmentAdviceBuilder.startRecording().withId("treatmentAdviceId").withStartDate(new LocalDate(2011, 11, 7)).build();
        assertTrue(fourDayRecallDateService.isFirstTreatmentWeek(patient, treatmentAdvice));
    }

    @Test
    public void shouldReturnFalseIfCurrentWeekIsSecondWeekOfTreatmentAfterTransition_AndTodayIsSecondWeekRecallDate() {
        mockCurrentDate(DateUtil.newDateTime(new LocalDate(2011, 11, 30), 0, 0, 0));
        Patient patient = PatientBuilder.startRecording().withWeeklyCallPreference(DayOfWeek.Wednesday, new TimeOfDay(10, 0, TimeMeridiem.AM))
                .withTransitionDate(new LocalDate(2011, 11, 15)).build();
        TreatmentAdvice treatmentAdvice = TreatmentAdviceBuilder.startRecording().withId("treatmentAdviceId").withStartDate(new LocalDate(2011, 11, 7)).build();
        assertFalse(fourDayRecallDateService.isFirstTreatmentWeek(patient, treatmentAdvice));
    }
}
