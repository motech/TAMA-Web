package org.motechproject.tama.util;

import java.util.*;

public class DateUtility {

    public static Date now() {
        String timeZone = ResourceBundle.getBundle("date").getString("timezone");
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone(timeZone));
        return calendar.getTime();
    }
}
