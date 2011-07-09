package org.motechproject.tama;

import java.util.ArrayList;
import java.util.List;

public class TAMAConstants {
    public static final String MOBILE_NUMBER_REGEX = "^\\d{10}$";
    public static final String PASSCODE_REGEX = "^\\d{4,10}$";

    public enum Time {
        MAX_DAYS_IN_A_MONTH(31),
        MAX_HOURS_IN_A_DAY(24),
        MAX_MINUTES_IN_AN_HOUR(60);
        private Integer value;

        Time(Integer value) {
            this.value = value;
        }

        public List<Integer> list() {
            ArrayList<Integer> items = new ArrayList<Integer>();
            for (int i = 0; i <= value; i++) items.add(i);
            return items;
        }
    }

}
