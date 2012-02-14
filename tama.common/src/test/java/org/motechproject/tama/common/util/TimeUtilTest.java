package org.motechproject.tama.common.util;

import org.junit.Assert;
import org.junit.Test;

public class TimeUtilTest {

    @Test
    public void shouldExtractHourAndMinute() {
        String timeString = "01:23am";

        TimeUtil timeUtil = new TimeUtil(timeString).withReminderLagTime(0);
        Assert.assertEquals(1, timeUtil.getHours());
        Assert.assertEquals(23, timeUtil.getMinutes());
    }

    @Test
    public void shouldReturnHoursIn24HourFormat() {
        String timeString = "05:00pm";

        TimeUtil timeUtil = new TimeUtil(timeString).withReminderLagTime(0);
        Assert.assertEquals(17, timeUtil.getHours());
    }

    @Test
    public void shouldReturnHoursWhenTimeIsNoon() {
        String timeString = "12:03pm";

        TimeUtil timeUtil = new TimeUtil(timeString).withReminderLagTime(0);
        Assert.assertEquals(12, timeUtil.getHours());
    }

    @Test
    public void shouldReturnHoursIn24HourFormatWithLagTime() {
        String timeString = "05:50pm";

        TimeUtil timeUtil = new TimeUtil(timeString).withReminderLagTime(12);
        Assert.assertEquals(18, timeUtil.getHours());
        Assert.assertEquals(2, timeUtil.getMinutes());
    }

    @Test
    public void shouldReturnHoursIn24HourFormatWithLagTimeWhenDayLimitCrosses() {
        String timeString = "11:54pm";

        TimeUtil timeUtil = new TimeUtil(timeString).withReminderLagTime(12);
        Assert.assertEquals(0, timeUtil.getHours());
        Assert.assertEquals(6, timeUtil.getMinutes());
    }
}