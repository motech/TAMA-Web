package org.motechproject.tamacallflow.ivr.builder;

import org.joda.time.DateTime;
import org.motechproject.tamacallflow.ivr.TamaIVRMessage;
import org.motechproject.tamacallflow.ivr.builder.timeconstruct.TimeConstructBuilder;
import org.motechproject.util.DateUtil;

import java.util.ArrayList;
import java.util.List;

public class IVRDayMessageBuilder {

    public List<String> getMessageForNextDosage(DateTime nextDosageDateTime, String preferredLanguage) {
        List<String> messages = new ArrayList<String>();
        messages.add(nextDosageDateTime.toLocalDate().equals(DateUtil.today()) ? TamaIVRMessage.TODAY : TamaIVRMessage.TOMORROW);
        messages.add(TamaIVRMessage.AT);
        messages.addAll(new TimeConstructBuilder().build(preferredLanguage, nextDosageDateTime.toLocalTime()));
        return messages;
    }

    public String getMessageForPreviousDosageQuestion_YESTERDAYS_MORNING(DateTime previousDosageDateTime) {
        return getMessageForPreviousDosage(previousDosageDateTime, TamaIVRMessage.YESTERDAYS, TamaIVRMessage.MORNING, TamaIVRMessage.AFTERNOON, TamaIVRMessage.EVENING);
    }

    public String getMessageForPreviousDosageQuestion_YESTERDAY_IN_THE_MORNING(DateTime previousDosageDateTime) {
        return getMessageForPreviousDosage(previousDosageDateTime, TamaIVRMessage.YESTERDAY, TamaIVRMessage.IN_THE_MORNING, TamaIVRMessage.IN_THE_AFTERNOON, TamaIVRMessage.IN_THE_EVENING);
    }

    public String getMessageForPreviousDosageConfirmation_YESTERDAYS_MORNING(DateTime previousDosageDateTime) {
        return getMessageForPreviousDosage(previousDosageDateTime, TamaIVRMessage.YESTERDAYS_CONFIRMATION, TamaIVRMessage.MORNING_CONFIRMATION, TamaIVRMessage.AFTERNOON_CONFIRMATION, TamaIVRMessage.EVENING_CONFIRMATION);
    }

    private String getMessageForPreviousDosage(DateTime previousDosageDateTime, String yesterday, String morning, String afternoon, String evening) {
        if (previousDosageDateTime.plusDays(1).toLocalDate().equals(DateUtil.today()))
           return yesterday;
        else if (previousDosageDateTime.getHourOfDay() < 12)
            return morning;
        else if (previousDosageDateTime.getHourOfDay() < 16)
            return afternoon;
        return evening;
    }
}
