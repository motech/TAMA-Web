package org.motechproject.tama.ivr.builder;

import org.joda.time.DateTime;
import org.motechproject.tama.ivr.IVRMessage;
import org.motechproject.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class IVRDayMessageBuilder {


    private IVRMessage iVRMessage;

    @Autowired
    public IVRDayMessageBuilder(IVRMessage iVRMessage) {
        this.iVRMessage = iVRMessage;
    }

    public List<String> getMessageForNextDosage(DateTime nextDosageDateTime) {
        List<String> messages = new ArrayList<String>();
        if (nextDosageDateTime.getHourOfDay() == 0 || nextDosageDateTime.getHourOfDay() == 12) {
            messages.add(iVRMessage.getNumberFilename(12));
        } else {
            messages.add(iVRMessage.getNumberFilename(nextDosageDateTime.getHourOfDay() % 12));
        }
        messages.add(iVRMessage.getNumberFilename(nextDosageDateTime.getMinuteOfHour()));
        messages.add(nextDosageDateTime.getHourOfDay() < 12 ? IVRMessage.IN_THE_MORNING : IVRMessage.IN_THE_EVENING);
        messages.add(nextDosageDateTime.toLocalDate().equals(DateUtil.today()) ? IVRMessage.TODAY : IVRMessage.TOMORROW);
        return messages;
    }

    public List<String> getMessageForPreviousDosage_YESTERDAYS_MORNING(DateTime previousDosageDateTime) {
        return getMessageForPreviousDosage(previousDosageDateTime, IVRMessage.YESTERDAYS, IVRMessage.MORNING, IVRMessage.AFTERNOON, IVRMessage.EVENING, IVRMessage.LAST_NIGHT);
    }

    public List<String> getMessageForPreviousDosage_YESTERDAY_IN_THE_MORNING(DateTime previousDosageDateTime) {
        return getMessageForPreviousDosage(previousDosageDateTime, IVRMessage.YESTERDAY, IVRMessage.IN_THE_MORNING, IVRMessage.IN_THE_AFTERNOON, IVRMessage.IN_THE_EVENING, IVRMessage.IN_THE_LAST_NIGHT);
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
