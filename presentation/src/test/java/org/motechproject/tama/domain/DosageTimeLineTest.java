package org.motechproject.tama.domain;

import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;
import org.motechproject.model.Time;
import org.motechproject.server.pillreminder.contract.DosageResponse;
import org.motechproject.tama.ivr.DosageResponseWithDate;
import org.motechproject.util.DateUtil;

import java.util.Arrays;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import static junit.framework.Assert.fail;
import static org.junit.Assert.assertFalse;

public class DosageTimeLineTest {

    private DosageResponse dosageResponse1;
    private DosageResponse dosageResponse2;
    private LocalDate fromDate;

    @Before
    public void setUp() {
        fromDate = DateUtil.newDate(2011, 11, 11);
        dosageResponse1 = new DosageResponse("dosageId", new Time(13, 10), fromDate, null, null, null);
        dosageResponse2 = new DosageResponse("dosageId", new Time(07, 00), fromDate, null, null, null);
    }

    @Test
    public void shouldFetchTheUpcomingDosage() {
        DosageTimeLine dosageTimeLine = new DosageTimeLine(Arrays.asList(dosageResponse1, dosageResponse2), DateUtil.newDateTime(fromDate, 06, 00, 00));
        assertDosageResponse(dosageResponse2, fromDate, dosageTimeLine.next());
    }

    @Test
    public void shouldFetchTheFourthUpcomingDosage() {
        DosageTimeLine dosageTimeLine = new DosageTimeLine(Arrays.asList(dosageResponse1, dosageResponse2), DateUtil.newDateTime(fromDate, 8, 00, 00));
        dosageTimeLine.next();
        dosageTimeLine.next();
        dosageTimeLine.next();
        DosageResponseWithDate actualDosageResponse = dosageTimeLine.next();
        assertDosageResponse(dosageResponse2, fromDate.plusDays(2), actualDosageResponse);
    }

    @Test
    public void shouldNotFetchAnyDosageIfPatientIsSuspendedAndActivatedBetweenDosageTimes() {
        LocalDate toDate = DateUtil.newDate(2011, 11, 11);
        DosageTimeLine dosageTimeLine = new DosageTimeLine(Arrays.asList(dosageResponse1, dosageResponse2), DateUtil.newDateTime(fromDate, 8, 00, 00), DateUtil.newDateTime(toDate, 12, 00, 00));
        try {
            dosageTimeLine.next();
            fail();
        } catch (ArrayIndexOutOfBoundsException ex) {
        }
    }

    @Test
    public void shouldFetchOnlyDosagesInInterimPeriodIfPatientIsSuspendedAndActivatedOnSameDay() {
        LocalDate toDate = DateUtil.newDate(2011, 11, 11);
        DosageTimeLine dosageTimeLine = new DosageTimeLine(Arrays.asList(dosageResponse1, dosageResponse2), DateUtil.newDateTime(fromDate, 6, 00, 00), DateUtil.newDateTime(toDate, 12, 00, 00));
        assertDosageResponse(dosageResponse2, fromDate, dosageTimeLine.next());
        try {
            dosageTimeLine.next();
            fail();
        } catch (ArrayIndexOutOfBoundsException ex) {
        }
    }

    @Test
    public void shouldNotReturnDosagesCapturedBeforeSuspensionOnStartDate_AndNotReturnAnyDosagesThatCanBeCapturedAfterReactivation(){
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
    public void shouldReturnOnlyTheDosageThatFallsInInterimPeriod(){
        LocalDate toDate = DateUtil.newDate(2011, 11, 11);
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
        DosageTimeLine dosageTimeLine = new DosageTimeLine(Arrays.asList(dosageResponse1), DateUtil.newDateTime(fromDate, 14, 00, 00));
        assertDosageResponse(dosageResponse1, fromDate.plusDays(1), dosageTimeLine.next());
        assertDosageResponse(dosageResponse1, fromDate.plusDays(2), dosageTimeLine.next());
    }

    @Test
    public void shouldFetchOnlyTheApplicableDosageForDayOfResume() {
        LocalDate toDate = DateUtil.newDate(2011, 11, 13);
        DosageTimeLine dosageTimeLine = new DosageTimeLine(Arrays.asList(dosageResponse1, dosageResponse2), DateUtil.newDateTime(fromDate, 8, 00, 00), DateUtil.newDateTime(toDate, 12, 00, 00));
        dosageTimeLine.next();
        dosageTimeLine.next();
        DosageResponseWithDate actualDosageResponse = dosageTimeLine.next();
        assertDosageResponse(dosageResponse1, fromDate.plusDays(1), actualDosageResponse);
        actualDosageResponse = dosageTimeLine.next();
        assertDosageResponse(dosageResponse2, fromDate.plusDays(2), actualDosageResponse);
        try {
            dosageTimeLine.next();
            fail();
        } catch (ArrayIndexOutOfBoundsException ex) {
        }
    }


    @Test
    public void shouldReturnTrueIfThereIsAtLeastOneUpcomingDosage() {
        DosageTimeLine dosageTimeLine = new DosageTimeLine(Arrays.asList(dosageResponse1, dosageResponse2), DateUtil.newDateTime(fromDate, 8, 00, 00));
        assertTrue(dosageTimeLine.hasNext());
        assertTrue(dosageTimeLine.hasNext()); //Operation is Idempotent.
    }

    @Test
    public void shouldReturnFalseIfThereAreNoMoreUpcomingDosages() {
        LocalDate toDate = DateUtil.newDate(2011, 11, 12);
        DosageTimeLine dosageTimeLine = new DosageTimeLine(Arrays.asList(dosageResponse1, dosageResponse2), DateUtil.newDateTime(fromDate, 8, 00, 00), DateUtil.newDateTime(toDate, 10, 00, 00));
        assertTrue(dosageTimeLine.hasNext());
        dosageTimeLine.next();
        assertTrue(dosageTimeLine.hasNext());
        dosageTimeLine.next();
        assertFalse(dosageTimeLine.hasNext());
    }
    
    @Test
    public void shouldReturnFalseWhenThereWereNotAnyDosagesInTheInterimPeriodAtAll() {
        LocalDate toDate = DateUtil.newDate(2011, 11, 11);
        DosageTimeLine dosageTimeLine = new DosageTimeLine(Arrays.asList(dosageResponse1, dosageResponse2), DateUtil.newDateTime(fromDate, 8, 00, 00), DateUtil.newDateTime(toDate, 10, 00, 00));
        assertFalse(dosageTimeLine.hasNext());
    }

    public void assertDosageResponse(DosageResponse expectedDosageResponse, LocalDate expectedDosageDate, DosageResponseWithDate actualResponse) {
        assertEquals(expectedDosageResponse, actualResponse.getDosage());
        assertEquals(expectedDosageDate, actualResponse.getDosageDate());
    }
}