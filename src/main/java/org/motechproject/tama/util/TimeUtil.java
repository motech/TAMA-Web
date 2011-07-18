package org.motechproject.tama.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TimeUtil {
    private int hours;
    private int minutes;
    private String ampm;

    public TimeUtil(String timeString) {

        Pattern pattern = Pattern.compile("([0][0-9]|[1][0-2]):([0-5][0-9])([a,p]m)");
        Matcher matcher = pattern.matcher(timeString);
        boolean found = matcher.find();
        if (found) {
            hours = Integer.parseInt(matcher.group(1));
            minutes = Integer.parseInt(matcher.group(2));
            ampm = matcher.group(3);
        }
    }

    public int getHours() {
        return ampm.equals("am") ? hours : hours + 12;
    }

    public int getMinutes() {
        return minutes;
    }
}