package org.motechproject.tama.common.domain;

import org.junit.Test;
import org.motechproject.model.Time;

import static junit.framework.Assert.assertEquals;

public class TimeOfDayTest {

    @Test
    public void getTimeOfDayAsString_ShouldConstructDisplayTime() {
        TimeOfDay timeOfDay = new TimeOfDay(5, 9, TimeMeridiem.AM);
        assertEquals("05:09", timeOfDay.getTimeOfDayAsString());
    }

    @Test
    public void setTimeOfDayAsString_ShouldReadDisplayTime() {
        TimeOfDay timeOfDay = new TimeOfDay();
        timeOfDay.setTimeMeridiem(TimeMeridiem.AM);
        timeOfDay.setTimeOfDayAsString("12:05");

        assertEquals(new Integer(12), timeOfDay.getHour());
        assertEquals(new Integer(5), timeOfDay.getMinute());
    }

    @Test
    public void shouldReturnTimeObjectForTheGivenTime() {
        TimeOfDay timeOfDay = new TimeOfDay(5, 9, TimeMeridiem.AM);
        assertEquals(new Time(5, 9), timeOfDay.toTime());
        timeOfDay = new TimeOfDay(5, 9, TimeMeridiem.PM);
        assertEquals(new Time(17, 9), timeOfDay.toTime());
        timeOfDay = new TimeOfDay(12, 9, TimeMeridiem.AM);
        assertEquals(new Time(0, 9), timeOfDay.toTime());
        timeOfDay = new TimeOfDay(12, 9, TimeMeridiem.PM);
        assertEquals(new Time(12, 9), timeOfDay.toTime());
    }
}
