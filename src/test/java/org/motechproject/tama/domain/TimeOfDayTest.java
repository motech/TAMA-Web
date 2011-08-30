package org.motechproject.tama.domain;

import org.junit.Test;
import org.motechproject.tama.TAMAConstants;

import static junit.framework.Assert.assertEquals;

public class TimeOfDayTest {

    @Test
    public void getTimeOfDayAsString_ShouldConstructDisplayTime() {
        TimeOfDay timeOfDay = new TimeOfDay();
        timeOfDay.setHour(5);
        timeOfDay.setMinute(9);
        timeOfDay.setTimeMeridiem(TAMAConstants.TimeMeridiem.AM);

        assertEquals("05:09", timeOfDay.getTimeOfDayAsString());
    }

    @Test
    public void setTimeOfDayAsString_ShouldReadDisplayTime() {
        TimeOfDay timeOfDay = new TimeOfDay();
        timeOfDay.setTimeMeridiem(TAMAConstants.TimeMeridiem.AM);
        timeOfDay.setTimeOfDayAsString("12:05");

        assertEquals(new Integer(12), timeOfDay.getHour());
        assertEquals(new Integer(5), timeOfDay.getMinute());
    }
}
