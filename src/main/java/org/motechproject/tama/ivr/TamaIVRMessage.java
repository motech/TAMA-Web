package org.motechproject.tama.ivr;

import org.motechproject.server.service.ivr.IVRMessage;
import org.motechproject.tama.util.FileUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.Properties;

@Component
public class TamaIVRMessage implements IVRMessage {
    public static final String WAV = ".wav";

    public static final String MINUTES = "timeOfDayMinutes";
    public static final String SIGNATURE_MUSIC = "signature_music";
    public static final String CONTENT_LOCATION_URL = "content.location.url";

    public static final String PILL_REMINDER_RESPONSE_MENU = "001_02_05_pillTimeMenu";

    public static final String PILL_CONFIRM_CALL_MENU = "010_03_02_MainMenu2";
    public static final String ITS_TIME_FOR_THE_PILL = "001_02_02_itsTimeForPill1";
    public static final String PILL_FROM_THE_BOTTLE = "001_07_07_fromTheBottle1";
    public static final String PLEASE_TAKE_DOSE = "003_03_01_TAMAPillDelayWarning1";
    public static final String CALL_AFTER_SOME_TIME = "003_03_03_TAMAPillDelayWarning2";
    public static final String DOSE_RECORDED = "001_05_01_doseRecorded";
    public static final String DOSE_TAKEN_ON_TIME = "001_04_01_tookOnTime";
    public static final String PLEASE_CARRY_SMALL_BOX = "004_05_01_doctorNoPillsAdvice";
    public static final String YOUR_NEXT_DOSE_IS = "010_04_01_nextDoseIs1";
    public static final String YOUR_NEXT_DOSE_IS_PADDING = "010_04_06_nextDoseIs2";
    public static final String AT = "timeOfDayAt";
    public static final String DOSE_CANNOT_BE_TAKEN_MENU = "004_04_04_MissedPillMenu";
    public static final String LAST_REMINDER_WARNING = "005_04_01_CannotDelayPillsNow";
    public static final String LAST_REMINDER_WARNING_PADDING = "005_04_03_WillCallAgain";

    /* -------- PreviousDosage ----------- */
    public static final String YOUR = "001_07_01_your";
    public static final String YESTERDAYS = "001_07_02_doseTimeOfYesterdays";
    public static final String MORNING = "001_07_02_doseTimeOfMorning";
    public static final String AFTERNOON = "001_07_02_doseTimeOfAfternoon";
    public static final String EVENING = "001_07_02_doseTimeOfEvening";

    public static final String LAST_NIGHT = "001_07_02_doseTimeOfLastnight";

    public static final String DOSE_NOT_RECORDED = "001_07_03_doseNotRecorded";
    public static final String YESTERDAY = "001_07_04_doseTimeAtYesterday";
    public static final String IN_THE_MORNING = "001_07_04_doseTimeAtMorning";
    public static final String IN_THE_AFTERNOON = "001_07_04_doseTimeAtAfternoon";
    public static final String IN_THE_EVENING = "001_07_04_doseTimeAtEvening";

    public static final String IN_THE_LAST_NIGHT = "001_07_04_doseTimeAtLastnight";
    public static final String YOU_WERE_SUPPOSED_TO_TAKE = "001_07_05_supposedToTake";
    public static final String FROM_THE_BOTTLE = "001_07_07_fromTheBottle1";
    public static final String PREVIOUS_DOSE_MENU = "001_07_08_lastDoseMenu";

    /* ------------------- */
    public static final String YOU_SAID_YOU_TOOK = "001_08_01_youSaidYouTook";
    public static final String YOU_SAID_YOU_DID_NOT_TAKE = "001_09_01_youSaidYouTookNot";

    public static final String DOSE = "001_08_03_doseTaken";
    public static final String TRY_NOT_TO_MISS = "001_09_05_tryNotToMiss";
    public static final String TOOK_DOSE_LATE = "010_10_01_tookDoseLate";
    public static final String TOOK_DOSE_BEFORE_TIME = "010_09_01_tookDoseBeforeTime";
    public static final String NOT_REPORTED_IF_TAKEN = "010_02_04_notReportedIfTaken";
    /* -------- PreviousDosage ----------- */
    public static final String TODAY = "timeOfDayToday";
    public static final String TOMORROW = "timeOfDayTomorrow";
    /* -------- Adherence Feedback ----------- */
    public static final String YOUR_ADHERENCE_IS_NOW = "001_06_01_YourAdherenceNow"; // 001_06_01_YourAdherenceNow.wav

    public static final String PERCENT = "001_06_03_HasBecomePercent"; // 001_06_03_HasBecomePercent.wav
    public static final String MISSED_PILL_FEEDBACK_FIRST_TIME = "F01_01_doctorMissedPillFeedback"; // F01_01_doctorMissedPillFeedback.wav
    public static final String MISSED_PILL_FEEDBACK_SECOND_TO_FOURTH_TIME = "F02_01_doctorMissedPillFeedback"; // F02_01_doctorMissedPillFeedback.wav
    public static final String MISSED_PILL_FEEDBACK_MORE_THAN_90 = "F03_01_doctorMissedPillFeedback"; // F03_01_doctorMissedPillFeedback.wav
    public static final String MISSED_PILL_FEEDBACK_BETWEEN_70_AND_90 = "F04_01_doctorMissedPillFeedback"; // F04_01_doctorMissedPillFeedback.wav

    public static final String MISSED_PILL_FEEDBACK_LESS_THAN_70 = "F05_01_doctorMissedPillFeedback"; // F05_01_doctorMissedPillFeedback.wav

    public static final String M02_04_ADHERENCE_COMMENT_GT95_FALLING = "m02_04_adherencecommentgt95falling";

    public static final String M02_05_ADHERENCE_COMMENT_70TO90_FALLING = "m02_05_adherencecomment70to90falling";

    public static final String M02_06_ADHERENCE_COMMENT_70TO90_RISING = "m02_06_adherencecomment70to90rising";

    public static final String M02_07_ADHERENCE_COMMENT_LT70_FALLING = "m02_07_adherencecommentlt70falling";

    public static final String M02_08_ADHERENCE_COMMENT_LT70_RISING = "m02_08_adherencecommentlt70rising";

    public static final String MENU_010_05_01_MAINMENU4 = "010_05_01_MainMenu4";

    public static final String HANGUP_OR_MAIN_MENU = "010_08_01_HangUpOrMainMenu";

    public static final String NO_MESSAGES = "010_07_03_NoOutboxMessages";

    /* -------- Adherence Feedback ----------- */
    /*----------Outbox -------------------------*/
    public static final String CONTINUE_TO_OUTBOX = "001_06_04_mayEndThisCallNow1";
    public static final String THOSE_WERE_YOUR_MESSAGES = "001_06_05_mayEndThisCallNow2";


    /* ---------- Four Day Recall ----------*/
    // Common
    public static final String FDR_GREETING = "025_02_02_4DayRecallGreeting";
    public static final String FDR_ALL_DOSAGES_TAKEN = "025_10_01_ haveBeenTakingWell";
    public static final String FDR_TAKE_DOSAGES_REGULARLY = "025_11_01_takeRegularly";
    public static final String FDR_YOUR_WEEKLY_ADHERENCE_IS = "M02_01_adherence1";
    public static final String FDR_PERCENT = "M02_03_adherenceLow2";

    // Single Dosage
    public static final String FDR_MENU_FOR_SINGLE_DOSAGE = "025_03_01_fourDoseRecallMenuA";
    public static final String FDR_MISSED_ONE_DOSAGE_ON_ONE_DAY = "025_04_01_MissedDose1";
    public static final String FDR_MISSED_ONE_DOSAGE_ON_MULTIPLE_DAYS_PART_1 = "025_05_01_MissedDose1";
    public static final String FDR_MISSED_ONE_DOSAGE_ON_MULTIPLE_DAYS_PART_2 = "025_05_03_MissedDose2";
    // Multiple Dosages
    public static final String FDR_MENU_FOR_MULTIPLE_DOSAGES = "025_06_01_fourDoseRecallMenuA";
    public static final String FDR_MISSED_MULTIPLE_DOSAGES_ON_ONE_DAY = "025_07_01_MissedDose1";
    public static final String FDR_MISSED_MULTIPLE_ON_MULTIPLE_DAYS_PART_1 = "025_08_01_MissedDose1";
    public static final String FDR_MISSED_MULTIPLE_ON_MULTIPLE_DAYS_PART_2 = "025_08_03_MissedDose2";
    /* ---------- Four Day Recall ----------*/

    private Properties properties;

    @Autowired
    public TamaIVRMessage(@Qualifier("ivrProperties") Properties properties) {
        this.properties = properties;
    }

    public String get(String key) {
        return (String) properties.get(key.toLowerCase());
    }


    /* (non-Javadoc)
      * @see org.motechproject.tama.ivr.IVRMessage#getText(java.lang.String)
      */
    @Override
    public String getText(String key) {
        String text = get(key);
        return text == null ? key : text;
    }

    /* (non-Javadoc)
      * @see org.motechproject.tama.ivr.IVRMessage#getWav(java.lang.String, java.lang.String)
      */
    @Override
	public String getWav(String key, String preferredLangCode) {
        String file = get(key) != null ? get(key) : FileUtil.sanitizeFilename(key);
        return properties.get(CONTENT_LOCATION_URL) + preferredLangCode + "/" + file + WAV;
    }

    public String getNumberFilename(int n) {
        return String.format("Num_%03d", n);
    }

    @Override
    public String getSignatureMusic() {
        return SIGNATURE_MUSIC;
    }
}
