package org.motechproject.tama.dailypillreminder.domain;

import org.joda.time.DateTime;
import org.junit.Test;
import org.motechproject.model.Time;
import org.motechproject.server.pillreminder.contract.DosageResponse;
import org.motechproject.util.DateUtil;

import java.util.NoSuchElementException;

import static junit.framework.Assert.*;

public class DosageTimeLineTest {

    @Test
    public void shouldReturnNextDose_WhenDosageTimeIsAfterFromTime() {
        DateTime dosageStartTime = DateUtil.newDateTime(DateUtil.newDate(2011, 11, 11), 0, 0, 0);
        DateTime fromDate = dosageStartTime.plusDays(1);
        DateTime toDate = dosageStartTime.plusDays(3);
        final Time dosageTime = new Time(13, 10);
        DosageResponse dosageResponse = new DosageResponse("dosageId", dosageTime, dosageStartTime.toLocalDate(), null, null, null);

        DosageTimeLine dosageTimeLine = new DosageTimeLine(dosageResponse, fromDate, toDate);
        assertTrue(dosageTimeLine.hasNext());
        assertEquals(DateUtil.newDateTime(fromDate.toLocalDate(), dosageTime.getHour(), dosageTime.getMinute(), 0), dosageTimeLine.next().getDoseTime());
    }

    @Test
    public void shouldReturnNextDose_WhenDosageTimeIsBeforeFromTime() {
        DateTime dosageStartTime = DateUtil.newDateTime(DateUtil.newDate(2011, 11, 11), 12, 0, 0);
        DateTime fromDate = dosageStartTime.plusDays(1);
        DateTime toDate = dosageStartTime.plusDays(3);
        Time dosageTime = new Time(9, 30);
        DosageResponse dosageResponse = new DosageResponse("dosageId", dosageTime, dosageStartTime.toLocalDate(), null, null, null);

        DosageTimeLine dosageTimeLine = new DosageTimeLine(dosageResponse, fromDate, toDate);
        assertTrue(dosageTimeLine.hasNext());
        assertEquals(DateUtil.newDateTime(fromDate.toLocalDate().plusDays(1), dosageTime.getHour(), dosageTime.getMinute(), 0), dosageTimeLine.next().getDoseTime());
    }

    @Test
    public void shouldNotReturnNextDose_WhenToDateIsBeforeFromDate() {
        DateTime dosageStartTime = DateUtil.newDateTime(DateUtil.newDate(2011, 11, 11), 12, 0, 0);
        DateTime fromDate = dosageStartTime.plusDays(2);
        DateTime toDate = dosageStartTime.plusDays(1);
        Time dosageTime = new Time(9, 30);
        DosageResponse dosageResponse = new DosageResponse("dosageId", dosageTime, dosageStartTime.toLocalDate(), null, null, null);

        DosageTimeLine dosageTimeLine = new DosageTimeLine(dosageResponse, fromDate, toDate);
        assertFalse(dosageTimeLine.hasNext());
    }

    @Test
    public void shouldReturnNextDose_WhenDosageDateAndToDateAndFromDateAreTheSame() {
        DateTime dosageStartTime = DateUtil.newDateTime(DateUtil.newDate(2011, 11, 11), 12, 0, 0);
        DateTime fromDate = dosageStartTime.plusDays(1);
        DateTime toDate = dosageStartTime.plusDays(1);
        Time dosageTime = new Time(12, 0);
        DosageResponse dosageResponse = new DosageResponse("dosageId", dosageTime, dosageStartTime.toLocalDate(), null, null, null);

        DosageTimeLine dosageTimeLine = new DosageTimeLine(dosageResponse, fromDate, toDate);
        assertTrue(dosageTimeLine.hasNext());
        assertEquals(DateUtil.newDateTime(fromDate.toLocalDate(), dosageTime.getHour(), dosageTime.getMinute(), 0), dosageTimeLine.next().getDoseTime());
    }

    @Test
    public void shouldReturnNextDose_WhenFromDateIsBeforeDosageStartDate() {
        DateTime fromDate = DateUtil.newDateTime(DateUtil.newDate(2011, 11, 11), 12, 0, 0).plusDays(1);
        DateTime dosageStartTime = fromDate.plusWeeks(1);
        DateTime toDate = dosageStartTime.plusWeeks(2);
        Time dosageTime = new Time(12, 0);
        DosageResponse dosageResponse = new DosageResponse("dosageId", dosageTime, dosageStartTime.toLocalDate(), null, null, null);

        DosageTimeLine dosageTimeLine = new DosageTimeLine(dosageResponse, fromDate, toDate);
        assertTrue(dosageTimeLine.hasNext());
        assertEquals(DateUtil.newDateTime(dosageStartTime.toLocalDate(), dosageTime.getHour(), dosageTime.getMinute(), 0), dosageTimeLine.next().getDoseTime());
    }

    @Test
    public void shouldReturnFalse_WhenNextDosageIsOutsideTheTimeLine() {
        DateTime dosageStartTime = DateUtil.newDateTime(DateUtil.newDate(2011, 11, 11), 10, 0, 0);
        DateTime fromDate = dosageStartTime.plusDays(1).minusHours(1);
        DateTime toDate = dosageStartTime.plusDays(3).minusHours(1);
        DosageResponse dosageResponse = new DosageResponse("dosageId", new Time(13, 10), dosageStartTime.toLocalDate(), null, null, null);

        DosageTimeLine dosageTimeLine = new DosageTimeLine(dosageResponse, fromDate, toDate);
        dosageTimeLine.next();
        dosageTimeLine.next();
        assertFalse(dosageTimeLine.hasNext());
    }

    @Test(expected = NoSuchElementException.class)
    public void shouldThrowException_WhenNextDoseIsNotAvailable() {
        DateTime dosageStartTime = DateUtil.newDateTime(DateUtil.newDate(2011, 11, 11), 10, 0, 0);
        DateTime fromDate = dosageStartTime.plusDays(1).minusHours(1);
        DateTime toDate = dosageStartTime.plusDays(1).minusHours(1);
        DosageResponse dosageResponse = new DosageResponse("dosageId", new Time(13, 10), dosageStartTime.toLocalDate(), null, null, null);

        DosageTimeLine dosageTimeLine = new DosageTimeLine(dosageResponse, fromDate, toDate);
        dosageTimeLine.next();
        dosageTimeLine.next();
    }
}