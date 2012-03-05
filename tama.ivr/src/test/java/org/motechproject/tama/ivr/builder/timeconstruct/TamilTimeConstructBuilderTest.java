package org.motechproject.tama.ivr.builder.timeconstruct;

import org.joda.time.LocalTime;
import org.junit.Test;

import java.util.List;

import static junit.framework.Assert.assertEquals;

public class TamilTimeConstructBuilderTest {

    @Test
    // 0:0:0 - Midnight Start
    public void shouldCreateTimeConstruct_AtMidnight_Start() {
        LocalTime midnight = new LocalTime(0, 0, 0);
        List<String> timeConstructWAVs = new TamilTimeConstructBuilder().build(midnight);
        assertEquals(3, timeConstructWAVs.size());
        assertEquals("timeOfDayMidnight", timeConstructWAVs.get(0));
        assertEquals("Num_012", timeConstructWAVs.get(1));
        assertEquals("timeOfDayHours", timeConstructWAVs.get(2));
    }

    @Test
    // 3:59:59 - Midnight End
    public void shouldCreateTimeConstruct_AtMidnight_End() {
        LocalTime midnight = new LocalTime(3, 59, 59);
        List<String> timeConstructWAVs = new TamilTimeConstructBuilder().build(midnight);
        assertEquals(5, timeConstructWAVs.size());
        assertEquals("timeOfDayMidnight", timeConstructWAVs.get(0));
        assertEquals("Num_003", timeConstructWAVs.get(1));
        assertEquals("timeOfDayHoursAfter", timeConstructWAVs.get(2));
        assertEquals("Num_059", timeConstructWAVs.get(3));
        assertEquals("timeOfDayMinutes", timeConstructWAVs.get(4));
    }

    @Test
    // 4:00:00 - Early Morning Start
    public void shouldCreateTimeConstruct_AtEarlyMorning_Start() {
        LocalTime earlyMorning = new LocalTime(4, 0, 0);
        List<String> timeConstructWAVs = new TamilTimeConstructBuilder().build(earlyMorning);
        assertEquals(3, timeConstructWAVs.size());
        assertEquals("timeofDayEarlyMorning", timeConstructWAVs.get(0));
        assertEquals("Num_004", timeConstructWAVs.get(1));
        assertEquals("timeOfDayHours", timeConstructWAVs.get(2));
    }

    @Test
    // 5:29:59 - Early Morning End
    public void shouldCreateTimeConstruct_AtEarlyMorning_End() {
        LocalTime earlyMorning = new LocalTime(5, 29, 59);
        List<String> timeConstructWAVs = new TamilTimeConstructBuilder().build(earlyMorning);
        assertEquals(5, timeConstructWAVs.size());
        assertEquals("timeofDayEarlyMorning", timeConstructWAVs.get(0));
        assertEquals("Num_005", timeConstructWAVs.get(1));
        assertEquals("timeOfDayHoursAfter", timeConstructWAVs.get(2));
        assertEquals("Num_029", timeConstructWAVs.get(3));
        assertEquals("timeOfDayMinutes", timeConstructWAVs.get(4));
    }

    @Test
    // 5:30:0 - Morning Start
    public void shouldCreateTimeConstruct_AtMorning_Start() {
        LocalTime morning = new LocalTime(5, 30, 0);
        List<String> timeConstructWAVs = new TamilTimeConstructBuilder().build(morning);
        assertEquals(5, timeConstructWAVs.size());
        assertEquals("timeofDayMorning", timeConstructWAVs.get(0));
        assertEquals("Num_005", timeConstructWAVs.get(1));
        assertEquals("timeOfDayHoursAfter", timeConstructWAVs.get(2));
        assertEquals("Num_030", timeConstructWAVs.get(3));
        assertEquals("timeOfDayMinutes", timeConstructWAVs.get(4));
    }

    @Test
    // 11:59:59 - Morning End
    public void shouldCreateTimeConstruct_AtMorning_End() {
        LocalTime morning = new LocalTime(11, 59, 59);
        List<String> timeConstructWAVs = new TamilTimeConstructBuilder().build(morning);
        assertEquals(5, timeConstructWAVs.size());
        assertEquals("timeofDayMorning", timeConstructWAVs.get(0));
        assertEquals("Num_011", timeConstructWAVs.get(1));
        assertEquals("timeOfDayHoursAfter", timeConstructWAVs.get(2));
        assertEquals("Num_059", timeConstructWAVs.get(3));
        assertEquals("timeOfDayMinutes", timeConstructWAVs.get(4));
    }

    @Test
    // 12:0:0 - Noon Start
    public void shouldCreateTimeConstruct_AtNoon_Start() {
        LocalTime noon = new LocalTime(12, 0, 0);
        List<String> timeConstructWAVs = new TamilTimeConstructBuilder().build(noon);
        assertEquals(3, timeConstructWAVs.size());
        assertEquals("timeOfDayAfternoon", timeConstructWAVs.get(0));
        assertEquals("Num_012", timeConstructWAVs.get(1));
        assertEquals("timeOfDayHours", timeConstructWAVs.get(2));
    }

    @Test
    // 15:59:59 - Noon End
    public void shouldCreateTimeConstruct_AtNoon_End() {
        LocalTime noon = new LocalTime(15, 59, 59);
        List<String> timeConstructWAVs = new TamilTimeConstructBuilder().build(noon);
        assertEquals(5, timeConstructWAVs.size());
        assertEquals("timeOfDayAfternoon", timeConstructWAVs.get(0));
        assertEquals("Num_003", timeConstructWAVs.get(1));
        assertEquals("timeOfDayHoursAfter", timeConstructWAVs.get(2));
        assertEquals("Num_059", timeConstructWAVs.get(3));
        assertEquals("timeOfDayMinutes", timeConstructWAVs.get(4));
    }

    @Test
    // 16:00:0 - Evening Start
    public void shouldCreateTimeConstruct_AtEvening_Start() {
        LocalTime evening = new LocalTime(16, 0, 0);
        List<String> timeConstructWAVs = new TamilTimeConstructBuilder().build(evening);
        assertEquals(3, timeConstructWAVs.size());
        assertEquals("timeOfDayEvening", timeConstructWAVs.get(0));
        assertEquals("Num_004", timeConstructWAVs.get(1));
        assertEquals("timeOfDayHours", timeConstructWAVs.get(2));
    }

    @Test
    // 19:59:59 - Evening End
    public void shouldCreateTimeConstruct_AtEvening_End() {
        LocalTime evening = new LocalTime(19, 59, 59);
        List<String> timeConstructWAVs = new TamilTimeConstructBuilder().build(evening);
        assertEquals(5, timeConstructWAVs.size());
        assertEquals("timeOfDayEvening", timeConstructWAVs.get(0));
        assertEquals("Num_007", timeConstructWAVs.get(1));
        assertEquals("timeOfDayHoursAfter", timeConstructWAVs.get(2));
        assertEquals("Num_059", timeConstructWAVs.get(3));
        assertEquals("timeOfDayMinutes", timeConstructWAVs.get(4));
    }

    @Test
    // 20:00:0 - Night Start
    public void shouldCreateTimeConstruct_AtNight_Start() {
        LocalTime night = new LocalTime(20, 0, 0);
        List<String> timeConstructWAVs = new TamilTimeConstructBuilder().build(night);
        assertEquals(3, timeConstructWAVs.size());
        assertEquals("timeOfDayNight", timeConstructWAVs.get(0));
        assertEquals("Num_008", timeConstructWAVs.get(1));
        assertEquals("timeOfDayHours", timeConstructWAVs.get(2));
    }

    @Test
    // 23:59:59 - Night End
    public void shouldCreateTimeConstruct_AtNight_End() {
        LocalTime night = new LocalTime(23, 59, 59);
        List<String> timeConstructWAVs = new TamilTimeConstructBuilder().build(night);
        assertEquals(5, timeConstructWAVs.size());
        assertEquals("timeOfDayNight", timeConstructWAVs.get(0));
        assertEquals("Num_011", timeConstructWAVs.get(1));
        assertEquals("timeOfDayHoursAfter", timeConstructWAVs.get(2));
        assertEquals("Num_059", timeConstructWAVs.get(3));
        assertEquals("timeOfDayMinutes", timeConstructWAVs.get(4));
    }
}
