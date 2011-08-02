package org.motechproject.tama.util;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import java.util.Calendar;
import java.util.Date;
import java.util.ResourceBundle;
import java.util.TimeZone;

public class DateUtility {

    private static DateTime dateTime;

    public static Date now() {
        Calendar calendar = getCalendar();
        return calendar.getTime();
    }

    public static Date today() {
        return getDateTime().withHourOfDay(0).withMinuteOfHour(0).withSecondOfMinute(0).toDate();
    }

    public static DateTime getDateTime() {
        String timeZone = ResourceBundle.getBundle("date").getString("timezone");
        return new DateTime(DateTimeZone.forTimeZone(TimeZone.getTimeZone(timeZone)));
    }

    public static Date newDate(int year, int month, int date) {
        return getDateTime().withYear(year).withMonthOfYear(month).withDayOfMonth(date).withHourOfDay(0).withMinuteOfHour(0).withSecondOfMinute(0).toDate();
    }

    private static Calendar getCalendar() {
        String timeZone = ResourceBundle.getBundle("date").getString("timezone");
        return Calendar.getInstance(TimeZone.getTimeZone(timeZone));
    }

    public static Date addDate(Date date, int days) {
        Calendar calendar = getCalendar();
        calendar.setTime(date);
        calendar.add(Calendar.DATE, days);
        return calendar.getTime();
    }
}