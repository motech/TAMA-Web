package org.motechproject.tama.ivr;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.Properties;

@Component
public class IVRMessage {
    public static final String WAV = ".wav";
    public static final String MINUTES = "minutes";
    public static final String SIGNATURE_MUSIC_URL = "signature_music";
    public static final String CONTENT_LOCATION_URL = "content.location.url";
    public static final String PILL_REMINDER_RESPONSE_MENU = "pill_reminder_menu";
    public static final String ITS_TIME_FOR_THE_PILL = "its_time_for_the_pill";
    public static final String PILL_FROM_THE_BOTTLE = "pill_from_the_bottle";
    public static final String PLEASE_TAKE_DOSE = "please_take_dose";
    public static final String DOSE_TAKEN = "dose_taken";
    public static final String PLEASE_CARRY_SMALL_BOX = "please_carry_small_box";
    public static final String DOSE_CANNOT_BE_TAKEN_MENU = "dose_cannot_be_taken_menu";
    public static final String LAST_REMINDER_WARNING = "last_reminder_warning";

    /* -------- PreviousDosage ----------- */
    public static final String YOUR = "your";
    public static final String YESTERDAYS = "yesterdays";
    public static final String MORNING = "morning";
    public static final String AFTERNOON = "afternoon";
    public static final String EVENING = "evening";
    public static final String DOSE_NOT_RECORDED = "dose_not_recorded";
    public static final String YESTERDAY = "yesterday";
    public static final String IN_THE_MORNING = "in_the_morning";
    public static final String IN_THE_AFTERNOON = "in_the_afternoon";
    public static final String IN_THE_EVENING = "in_the_evening";
    public static final String LAST_NIGHT = "last_night";
    public static final String YOU_WERE_SUPPOSED_TO_TAKE = "you_were_supposed_to_take";
    public static final String FROM_THE_BOTTLE = "from_the_bottle";
    public static final String PREVIOUS_DOSE_MENU = "previous_dose_menu";
    public static final String YOU_SAID_YOU_TOOK = "you_said_you_took";
    public static final String YOU_SAID_YOU_DID_NOT_TAKE = "you_said_you_did_not_take";
    public static final String DOSE = "dose";
    public static final String TRY_NOT_TO_MISS = "try_not_to_miss";
    /* -------- PreviousDosage ----------- */

    public static final String TODAY = "today";
    public static final String TOMORROW = "tomorrow";

    /* -------- Adherence Feedback ----------- */
    public static final String YOUR_ADHERENCE_IS_NOW = "yourAdherenceNow"; // 001_06_01_YourAdherenceNow.wav
    public static final String PERCENT = "hasBecomePercent"; // 001_06_03_HasBecomePercent.wav
    public static final String MISSED_PILL_FEEDBACK_FIRST_TIME = "missedPillFeedback_FirstTime"; // F01_01_doctorMissedPillFeedback.wav
    public static final String MISSED_PILL_FEEDBACK_SECOND_TO_FOURTH_TIME = "missedPillFeedback_2ndTo4thTime"; // F02_01_doctorMissedPillFeedback.wav
    public static final String MISSED_PILL_FEEDBACK_MORE_THAN_90 = "missedPillFeedback_MoreThan90"; // F03_01_doctorMissedPillFeedback.wav
    public static final String MISSED_PILL_FEEDBACK_BETWEEN_70_AND_90 = "missedPillFeedback_Between70And90"; // F04_01_doctorMissedPillFeedback.wav
    public static final String MISSED_PILL_FEEDBACK_LESS_THAN_70 = "missedPillFeedback_LessThan70"; // F05_01_doctorMissedPillFeedback.wav
    /* -------- Adherence Feedback ----------- */

    private Properties properties;

    @Autowired
    public IVRMessage(@Qualifier("ivrProperties") Properties properties) {
        this.properties = properties;
    }

    public String get(String key) {
        return (String) properties.get(key.toLowerCase());
    }

    public String getWav(String key) {
        String file = get(key) != null ? get(key) : key.toLowerCase();
        return properties.get(CONTENT_LOCATION_URL) + file + WAV;
    }
}
