package org.motechproject.tama.util;

import org.junit.Assert;
import org.junit.Test;

public class TimeUtilTest {
    @Test
    public void shouldExtractHourAndMinute() {
        String timeString = "01:23am";

        TimeUtil timeUtil = new TimeUtil(timeString);
        Assert.assertEquals(1, timeUtil.getHours());
        Assert.assertEquals(23, timeUtil.getMinutes());
    }

    @Test
    public void shouldReturnHoursIn24HourFormat() {
        String timeString = "05:00pm";

        TimeUtil timeUtil = new TimeUtil(timeString);
        Assert.assertEquals(17, timeUtil.getHours());
    }
}