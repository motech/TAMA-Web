package org.motechproject.tama.common.util;

import org.joda.time.DateTime;
import org.motechproject.util.DateUtil;

import java.util.Date;

public class DateFormat {

    public static String format(DateTime time, String pattern) {
        return time == null ? "" : time.toString(pattern);
    }

    public static String format(Date date, String pattern) {
        return format(DateUtil.newDateTime(date), pattern);
    }
}
