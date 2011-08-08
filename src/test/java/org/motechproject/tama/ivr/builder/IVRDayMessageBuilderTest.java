package org.motechproject.tama.ivr.builder;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.motechproject.util.DateUtil;

import java.util.List;

import static org.junit.Assert.assertEquals;

public class IVRDayMessageBuilderTest {

    IVRDayMessageBuilder ivrDayMessageBuilder;

    @Before
    public void setup() {
        ivrDayMessageBuilder = new IVRDayMessageBuilder();
    }

    @Test
    public void shouldReturnWavFilesForTomorrowMorning() {
        DateTime time = DateUtil.now().plusDays(1).withHourOfDay(10).withMinuteOfHour(20);

        List<String> messages = ivrDayMessageBuilder.getMessageForNextDosage(time);
        assertEquals("10", messages.get(0));
        assertEquals("20", messages.get(1));
        assertEquals("in_the_morning", messages.get(2));
        assertEquals("tomorrow", messages.get(3));
    }

    @Test
    public void shouldReturnWavFilesForTomorrowEvening() {
        DateTime time = DateUtil.now().plusDays(1).withHourOfDay(19).withMinuteOfHour(20);

        List<String> messages = ivrDayMessageBuilder.getMessageForNextDosage(time);
        assertEquals("7", messages.get(0));
        assertEquals("20", messages.get(1));
        assertEquals("in_the_evening", messages.get(2));
        assertEquals("tomorrow", messages.get(3));
    }

    @Test
    public void shouldReturnWavFilesForTodayEvening() {
        DateTime time = DateUtil.now().withHourOfDay(19).withMinuteOfHour(20);

        List<String> messages = ivrDayMessageBuilder.getMessageForNextDosage(time);
        assertEquals("7", messages.get(0));
        assertEquals("20", messages.get(1));
        assertEquals("in_the_evening", messages.get(2));
        assertEquals("today", messages.get(3));
    }

    @Test
    public void constructMessageWhenPreviousDosageIsForTodayMorning() {
        DateTime time = DateUtil.now().withHourOfDay(10).withMinuteOfHour(20);

        List<String> messages = ivrDayMessageBuilder.getMessageForPreviousDosage_YESTERDAYS_MORNING(time);
        assertEquals(1, messages.size());
        assertEquals("morning", messages.get(0));
    }

    @Test
    public void constructMessageWhenPreviousDosageIsForYesterdaysMorning() {
        DateTime time = DateUtil.now().minusDays(1).withHourOfDay(10).withMinuteOfHour(20);

        List<String> messages = ivrDayMessageBuilder.getMessageForPreviousDosage_YESTERDAYS_MORNING(time);
        assertEquals(2, messages.size());
        assertEquals("yesterdays", messages.get(0));
        assertEquals("morning", messages.get(1));
    }

    @Test
    public void constructMessageWhenPreviousDosageIsForYesterdayInTheMorning() {
        DateTime time = DateUtil.now().minusDays(1).withHourOfDay(10).withMinuteOfHour(20);

        List<String> messages = ivrDayMessageBuilder.getMessageForPreviousDosage_YESTERDAY_IN_THE_MORNING(time);
        assertEquals(2, messages.size());
        assertEquals("yesterday", messages.get(0));
        assertEquals("in_the_morning", messages.get(1));
    }

    @Test
    public void constructMessageWhenPreviousDosageIsForYesterdaysAfternoon() {
        DateTime time = DateUtil.now().minusDays(1).withHourOfDay(12).withMinuteOfHour(00);

        List<String> messages = ivrDayMessageBuilder.getMessageForPreviousDosage_YESTERDAYS_MORNING(time);
        assertEquals(2, messages.size());
        assertEquals("yesterdays", messages.get(0));
        assertEquals("afternoon", messages.get(1));
    }

    @Test
    public void constructMessageWhenPreviousDosageIsForYesterdaysEvening() {
        DateTime time = DateUtil.now().minusDays(1).withHourOfDay(16).withMinuteOfHour(00);

        List<String> messages = ivrDayMessageBuilder.getMessageForPreviousDosage_YESTERDAYS_MORNING(time);
        assertEquals(2, messages.size());
        assertEquals("yesterdays", messages.get(0));
        assertEquals("evening", messages.get(1));
    }

    @Test
    public void constructMessageWhenPreviousDosageIsForLastNight() {
        DateTime time = DateUtil.now().minusDays(1).withHourOfDay(20).withMinuteOfHour(00);

        List<String> messages = ivrDayMessageBuilder.getMessageForPreviousDosage_YESTERDAYS_MORNING(time);
        assertEquals(1, messages.size());
        assertEquals("last_night", messages.get(0));
    }
}
