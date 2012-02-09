package org.motechproject.tama.common.util;

import org.joda.time.DateTime;
import org.motechproject.tama.common.domain.TimeMeridiem;
import org.motechproject.tama.common.domain.TimeOfDay;
import org.motechproject.util.DateUtil;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TimeUtil {
    public static final String TIME_STRING_FORMAT = "([0][0-9]|[1][0-2]):([0-5][0-9])([a,p,A,P][m,M])";
    private int hours;
    private int minutes;
    private String ampm;

    public TimeUtil(String timeString) {
        Pattern pattern = Pattern.compile(TIME_STRING_FORMAT);
        Matcher matcher = pattern.matcher(timeString);
        boolean found = matcher.find();
        if (found) {
            hours = Integer.parseInt(matcher.group(1));
            minutes = Integer.parseInt(matcher.group(2));
            ampm = matcher.group(3);
        }
    }

    public TimeUtil withReminderLagTime(int reminderLag) {
        DateTime dateTime = DateUtil.now().
                withHourOfDay(withHours())
                .withMinuteOfHour(getMinutes())
                .plusMinutes(reminderLag);
        this.hours = dateTime.getHourOfDay();
        this.minutes = dateTime.getMinuteOfHour();
        return this;
    }

    public int getHours() {
        return hours;
    }

    private int withHours() {
        return ampm.equals("am") || hours == 12 ? hours : hours + 12;
    }

    public int getMinutes() {
        return minutes;
    }

    public TimeOfDay getTimeOfDay() {
        if (ampm.equals("am")) {
            return new TimeOfDay(hours, minutes, TimeMeridiem.AM);
        } else {
            return new TimeOfDay(hours, minutes, TimeMeridiem.PM);
        }
    }
}