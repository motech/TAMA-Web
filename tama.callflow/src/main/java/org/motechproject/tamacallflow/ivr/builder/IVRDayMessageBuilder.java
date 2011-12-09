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
        messages.addAll(new TimeConstructBuilder(preferredLanguage).build(nextDosageDateTime.toLocalTime()));
        return messages;
    }

    public List<String> getMessageForPreviousDosage_YESTERDAYS_MORNING(DateTime previousDosageDateTime) {
        return getMessageForPreviousDosage(previousDosageDateTime, TamaIVRMessage.YESTERDAYS, TamaIVRMessage.MORNING, TamaIVRMessage.AFTERNOON, TamaIVRMessage.EVENING, TamaIVRMessage.LAST_NIGHT);
    }

    public List<String> getMessageForPreviousDosage_YESTERDAY_IN_THE_MORNING(DateTime previousDosageDateTime) {
        return getMessageForPreviousDosage(previousDosageDateTime, TamaIVRMessage.YESTERDAY, TamaIVRMessage.IN_THE_MORNING, TamaIVRMessage.IN_THE_AFTERNOON, TamaIVRMessage.IN_THE_EVENING, TamaIVRMessage.IN_THE_LAST_NIGHT);
    }

    private List<String> getMessageForPreviousDosage(DateTime previousDosageDateTime, String yesterday, String morning, String afternoon, String evening, String lastNight) {
        List<String> messages = new ArrayList<String>();

        if (previousDosageDateTime.plusDays(1).toLocalDate().equals(DateUtil.today()) && previousDosageDateTime.getHourOfDay() < 20)
            messages.add(yesterday);

        if (previousDosageDateTime.getHourOfDay() < 12)
            messages.add(morning);
        else if (previousDosageDateTime.getHourOfDay() < 16)
            messages.add(afternoon);
        else if (previousDosageDateTime.getHourOfDay() < 20)
            messages.add(evening);
        else if (previousDosageDateTime.getHourOfDay() >= 20)
            messages.add(lastNight);

        return messages;
    }
}
