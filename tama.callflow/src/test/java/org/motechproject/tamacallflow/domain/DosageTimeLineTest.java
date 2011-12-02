package org.motechproject.tamacallflow.domain;

import org.joda.time.LocalDate;
import org.junit.Ignore;
import org.junit.Test;
import org.motechproject.model.Time;
import org.motechproject.server.pillreminder.contract.DosageResponse;
import org.motechproject.tamacallflow.ivr.Dose;
import org.motechproject.util.DateUtil;

import java.util.Arrays;

import static junit.framework.Assert.*;
import static org.junit.Assert.assertFalse;

public class DosageTimeLineTest {

    @Test
    public void shouldFetchTheUpcomingDosage() {
        LocalDate fromDate = DateUtil.newDate(2011, 11, 11);
        DosageResponse dosageResponse1 = new DosageResponse("dosageId", new Time(13, 10), fromDate, null, null, null);
        DosageResponse dosageResponse2 = new DosageResponse("dosageId1", new Time(07, 00), fromDate, null, null, null);

        DosageTimeLine dosageTimeLine = new DosageTimeLine(Arrays.asList(dosageResponse1, dosageResponse2), DateUtil.newDateTime(fromDate, 06, 00, 00));

        assertDosageResponse(dosageResponse2, fromDate, dosageTimeLine.next());
    }

    @Test
    public void shouldFetchTheFourthUpcomingDosage() {
        LocalDate fromDate = DateUtil.newDate(2011, 11, 11);
        DosageResponse dosageResponse1 = new DosageResponse("dosageId", new Time(13, 10), fromDate, null, null, null);
        DosageResponse dosageResponse2 = new DosageResponse("dosageId1", new Time(07, 00), fromDate, null, null, null);

        DosageTimeLine dosageTimeLine = new DosageTimeLine(Arrays.asList(dosageResponse1, dosageResponse2), DateUtil.newDateTime(fromDate, 8, 00, 00));
        dosageTimeLine.next();
        dosageTimeLine.next();
        dosageTimeLine.next();
        Dose actualDoseResponse = dosageTimeLine.next();

        assertDosageResponse(dosageResponse2, fromDate.plusDays(2), actualDoseResponse);
    }

    @Test
    public void shouldNotFetchDosageBeyondStartDate() {
        LocalDate fromDate = DateUtil.newDate(2011, 11, 11);
        DosageResponse dosageResponse = new DosageResponse("dosageId", new Time(13, 10), fromDate, null, null, null);

        DosageTimeLine dosageTimeLine = new DosageTimeLine(Arrays.asList(dosageResponse), DateUtil.newDateTime(fromDate.minusDays(1), 6, 0, 0), DateUtil.newDateTime(fromDate.minusDays(1), 17, 0, 0));

        try {
            dosageTimeLine.next();
            fail("No dosage is valid yet");
        } catch (ArrayIndexOutOfBoundsException ex) {
        }
    }

    @Test
    public void shouldNotFetchDosageBeyondStartDate1() {
        LocalDate fromDate = DateUtil.newDate(2011, 10, 22);
        DosageResponse dosageResponse1 = new DosageResponse("dosageId", new Time(5, 0), fromDate, fromDate.plusWeeks(4), null, null);

        DosageTimeLine dosageTimeLine = new DosageTimeLine(Arrays.asList(dosageResponse1), DateUtil.newDateTime(fromDate.minusDays(5), 6, 0, 0), DateUtil.newDateTime(fromDate.minusDays(4), 17, 0, 0));

        try {
            dosageTimeLine.next();
            fail("No dosage is valid yet");
        } catch (ArrayIndexOutOfBoundsException ex) {
        }
    }

    @Test
    public void shouldReturnOnlyTheDosageThatFallsInInterimPeriod(){
        LocalDate fromDate = DateUtil.newDate(2011, 11, 11);
        DosageResponse dosageResponse1 = new DosageResponse("dosageId", new Time(13, 10), fromDate, null, null, null);
        DosageResponse dosageResponse2 = new DosageResponse("dosageId1", new Time(07, 00), fromDate, null, null, null);
        LocalDate toDate = fromDate;

        DosageTimeLine dosageTimeLine = new DosageTimeLine(Arrays.asList(dosageResponse1, dosageResponse2), DateUtil.newDateTime(fromDate, 10, 00, 00), DateUtil.newDateTime(toDate, 17, 0, 0));
        assertDosageResponse(dosageResponse1, fromDate, dosageTimeLine.next());
        try {
            dosageTimeLine.next();
            fail();
        } catch (ArrayIndexOutOfBoundsException ex) {
        }
    }

    @Test
    public void shouldReturnTheUpcomingDosagesWithOnlyOneDosePerDay(){
        LocalDate fromDate = DateUtil.newDate(2011, 11, 11);
        DosageResponse dosageResponse1 = new DosageResponse("dosageId", new Time(13, 10), fromDate, null, null, null);

        DosageTimeLine dosageTimeLine = new DosageTimeLine(Arrays.asList(dosageResponse1), DateUtil.newDateTime(fromDate, 14, 00, 00));
        assertDosageResponse(dosageResponse1, fromDate.plusDays(1), dosageTimeLine.next());
        assertDosageResponse(dosageResponse1, fromDate.plusDays(2), dosageTimeLine.next());
    }

    @Test
    public void shouldReturnTrueIfThereIsAtLeastOneUpcomingDosage() {
        LocalDate fromDate = DateUtil.newDate(2011, 11, 11);
        DosageResponse dosageResponse1 = new DosageResponse("dosageId", new Time(13, 10), fromDate, null, null, null);
        DosageResponse dosageResponse2 = new DosageResponse("dosageId1", new Time(07, 00), fromDate, null, null, null);

        DosageTimeLine dosageTimeLine = new DosageTimeLine(Arrays.asList(dosageResponse1, dosageResponse2), DateUtil.newDateTime(fromDate, 8, 00, 00));
        assertTrue(dosageTimeLine.hasNext());
        assertTrue(dosageTimeLine.hasNext()); //Operation is Idempotent.
    }

    @Test
    public void shouldReturnFalseIfThereAreNoMoreUpcomingDosages() {
        LocalDate fromDate = DateUtil.newDate(2011, 11, 11);
        DosageResponse dosageResponse1 = new DosageResponse("dosageId", new Time(13, 10), fromDate, null, null, null);
        DosageResponse dosageResponse2 = new DosageResponse("dosageId1", new Time(07, 00), fromDate, null, null, null);

        LocalDate toDate = DateUtil.newDate(2011, 11, 12);
        DosageTimeLine dosageTimeLine = new DosageTimeLine(Arrays.asList(dosageResponse1, dosageResponse2), DateUtil.newDateTime(fromDate, 8, 00, 00), DateUtil.newDateTime(toDate, 10, 00, 00));
        assertTrue(dosageTimeLine.hasNext());
        dosageTimeLine.next();
        assertTrue(dosageTimeLine.hasNext());
        dosageTimeLine.next();
        assertFalse(dosageTimeLine.hasNext());
    }

    public void assertDosageResponse(DosageResponse expectedDosageResponse, LocalDate expectedDosageDate, Dose actualResponse) {
        assertEquals("Expected " + expectedDosageResponse.getDosageId() + " but was " + actualResponse.getDosageId(),expectedDosageResponse, actualResponse.getDosage());
        assertEquals(expectedDosageDate, actualResponse.getDosageDate());
    }

    @Test
    @Ignore //TODO: Make this an integration test
    public void shouldNotReturnDosagesCapturedBeforeSuspensionOnStartDate_AndNotReturnAnyDosagesThatCanBeCapturedAfterReactivation(){
        LocalDate fromDate = DateUtil.newDate(2011, 11, 11);
        DosageResponse dosageResponse1 = new DosageResponse("dosageId", new Time(13, 10), fromDate, null, null, null);
        DosageResponse dosageResponse2 = new DosageResponse("dosageId1", new Time(07, 00), fromDate, null, null, null);

        LocalDate toDate = DateUtil.newDate(2011, 11, 13);
        DosageTimeLine dosageTimeLine = new DosageTimeLine(Arrays.asList(dosageResponse1, dosageResponse2), DateUtil.newDateTime(fromDate, 14, 00, 00), DateUtil.newDateTime(toDate, 05, 0, 0));
        assertDosageResponse(dosageResponse2, fromDate.plusDays(1), dosageTimeLine.next());
        assertDosageResponse(dosageResponse1, fromDate.plusDays(1), dosageTimeLine.next());
        try {
            dosageTimeLine.next();
            fail();
        } catch (ArrayIndexOutOfBoundsException ex) {
        }
    }

    @Test
    @Ignore //TODO: Make this an integration test
    public void shouldFetchOnlyTheApplicableDosageForDayOfResume() {
        LocalDate fromDate = DateUtil.newDate(2011, 11, 11);
        DosageResponse dosageResponse1 = new DosageResponse("dosageId", new Time(13, 10), fromDate, null, null, null);
        DosageResponse dosageResponse2 = new DosageResponse("dosageId1", new Time(07, 00), fromDate, null, null, null);

        LocalDate toDate = DateUtil.newDate(2011, 11, 13);
        DosageTimeLine dosageTimeLine = new DosageTimeLine(Arrays.asList(dosageResponse1, dosageResponse2), DateUtil.newDateTime(fromDate, 8, 00, 00), DateUtil.newDateTime(toDate, 12, 00, 00));
        dosageTimeLine.next();
        dosageTimeLine.next();
        Dose actualDoseResponse = dosageTimeLine.next();
        assertDosageResponse(dosageResponse1, fromDate.plusDays(1), actualDoseResponse);
        actualDoseResponse = dosageTimeLine.next();
        assertDosageResponse(dosageResponse2, fromDate.plusDays(2), actualDoseResponse);
        try {
            dosageTimeLine.next();
            fail();
        } catch (ArrayIndexOutOfBoundsException ex) {
        }
    }

    @Test
    @Ignore //TODO: Make this an integration test
    public void shouldNotFetchAnyDosageIfPatientIsSuspendedAndActivatedBetweenDosageTimes() {
        LocalDate fromDate = DateUtil.newDate(2011, 11, 11);
        DosageResponse dosageResponse1 = new DosageResponse("dosageId", new Time(13, 10), fromDate, null, null, null);
        DosageResponse dosageResponse2 = new DosageResponse("dosageId1", new Time(07, 00), fromDate, null, null, null);

        LocalDate toDate = DateUtil.newDate(2011, 11, 11);
        DosageTimeLine dosageTimeLine = new DosageTimeLine(Arrays.asList(dosageResponse1, dosageResponse2), DateUtil.newDateTime(fromDate, 8, 00, 00), DateUtil.newDateTime(toDate, 14, 00, 00));
        try {
            dosageTimeLine.next();
            fail();
        } catch (ArrayIndexOutOfBoundsException ex) {
        }
    }

    @Test
    @Ignore //TODO: Make this an integration test
    public void shouldFetchOnlyDosagesInInterimPeriodIfPatientIsSuspendedAndActivatedOnSameDay() {
        LocalDate fromDate = DateUtil.newDate(2011, 11, 11);
        DosageResponse dosageResponse1 = new DosageResponse("dosageId", new Time(13, 10), fromDate, null, null, null);
        DosageResponse dosageResponse2 = new DosageResponse("dosageId1", new Time(07, 00), fromDate, null, null, null);

        LocalDate toDate = DateUtil.newDate(2011, 11, 11);
        DosageTimeLine dosageTimeLine = new DosageTimeLine(Arrays.asList(dosageResponse1, dosageResponse2), DateUtil.newDateTime(fromDate, 6, 00, 00), DateUtil.newDateTime(toDate, 12, 00, 00));
        assertDosageResponse(dosageResponse2, fromDate, dosageTimeLine.next());
        try {
            dosageTimeLine.next();
            fail();
        } catch (ArrayIndexOutOfBoundsException ex) {
        }
    }
}