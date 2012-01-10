package org.motechproject.tama.fourdayrecall.util;

import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;
import org.motechproject.model.DayOfWeek;
import org.motechproject.tama.patient.builder.PatientBuilder;
import org.motechproject.tama.patient.builder.TreatmentAdviceBuilder;
import org.motechproject.tama.patient.domain.Patient;
import org.motechproject.tama.patient.domain.TreatmentAdvice;

import static junit.framework.Assert.assertEquals;
import static org.motechproject.tama.fourdayrecall.util.FourDayRecallUtil.getStartDateForWeek;

public class FourDayRecallUtilTest {

    private TreatmentAdvice treatmentAdvice;

    @Before
    public void setUp() {
        treatmentAdvice = TreatmentAdvice.newDefault();
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
}
