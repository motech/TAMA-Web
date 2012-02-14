package org.motechproject.tama.common.util;

import org.joda.time.DateTime;
import org.joda.time.LocalTime;
import org.motechproject.util.DateUtil;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TimeUtil {
    public static final String TIME_STRING_FORMAT = "([0][0-9]|[1][0-2]):([0-5][0-9])([a,p,A,P][m,M])";
    private int hours;
    private int minutes;

    public TimeUtil(String timeString) {
        Pattern pattern = Pattern.compile(TIME_STRING_FORMAT);
        Matcher matcher = pattern.matcher(timeString);
        boolean found = matcher.find();
        if (found) {
            String ampm = matcher.group(3);
            int parsedHour = Integer.parseInt(matcher.group(1));
            hours = ampm.equals("am") || parsedHour == 12 ? parsedHour : parsedHour + 12;
            minutes = Integer.parseInt(matcher.group(2));
        }
    }

    public TimeUtil withReminderLagTime(int reminderLag) {
        DateTime dateTime = DateUtil.now().
                withHourOfDay(hours)
                .withMinuteOfHour(minutes)
                .plusMinutes(reminderLag);
        this.hours = dateTime.getHourOfDay();
        this.minutes = dateTime.getMinuteOfHour();
        return this;
    }

    public int getHours() {
        return hours;
    }

    public int getMinutes() {
        return minutes;
    }

    public LocalTime toLocalTime() {
        return new LocalTime(getHours(), getMinutes());
    }

}