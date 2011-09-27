package org.motechproject.tama;

import java.util.ArrayList;
import java.util.List;

public class TAMAConstants {
    public static final String MOBILE_NUMBER_REGEX = "^\\d{10}$";
    public static final String PASSCODE_REGEX = "^\\d{4,10}$";
    public static final String RETRY_INTERVAL = "retry.interval.mins";
    public static final String PILL_WINDOW = "pill.window.hrs";
    public static final String REMINDER_LAG = "reminder.lag.mins";
    public static final String DOSAGE_INTERVAL = "dosage.interval";
    public static final String DATE_FORMAT = "dd/MM/yyyy";
    public static final int DAYS_IN_FOUR_WEEKS = 28;
    public static final String IS_OUTBOUND_CALL = "is_outbound";
    public static final String ADHERENCE_WEEKLY_TREND_SCHEDULER_SUBJECT = "org.motechproject.server" + ".adherence.weeklyTrendFeedback";

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

    public enum ReminderCall {
        Daily, Weekly
    }

    public enum DayOfWeek {
        Monday, Tuesday, Wednesday, Thursday, Friday, Saturday, Sunday
    }

    public enum TimeMeridiem {
        AM, PM
    }

    public enum AUTH_STATUS {
        AUTHENTICATED("Authenticated"), UNAUTHENTICATED("Unauthenticated");
        private String value;

        AUTH_STATUS(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }

    public enum DrugAllergy {
        Sulfonamide("Sulfonamide allergy"), ARV("ARV Allergy"), Other("Other");
        private String value;

        DrugAllergy(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }

    public enum NNRTIRash {
        DRD("Delavirdine / Rescriptor / DLV"),
        ESSE("Efavirenz / Sustiva / Stocrin / EFV"),
        NVN("Nevirapine / Viramune / NVP"),
        EIT("Etravirine / Intelence / TMC125");

        private String value;

        NNRTIRash(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }
}
