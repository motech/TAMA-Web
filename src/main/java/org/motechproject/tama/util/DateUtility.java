package org.motechproject.tama.util;

import java.util.Calendar;
import java.util.Date;
import java.util.ResourceBundle;
import java.util.TimeZone;

public class DateUtility {

    public static Date now() {
        Calendar calendar = getCalendar();
        return calendar.getTime();
    }

    public static Date newDate(int year, int month, int date) {
        Calendar calendar = getCalendar();
        calendar.set(year, month, date);
        return calendar.getTime();
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
