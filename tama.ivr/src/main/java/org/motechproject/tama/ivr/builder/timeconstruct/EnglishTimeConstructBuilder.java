package org.motechproject.tama.ivr.builder.timeconstruct;

import org.joda.time.LocalTime;
import org.joda.time.format.DateTimeFormat;
import org.motechproject.tama.ivr.TamaIVRMessage;

import java.util.ArrayList;
import java.util.List;

public class EnglishTimeConstructBuilder extends SlotBasedTimeConstructBuilder {

    static final LocalTime AM_START = new LocalTime(0, 0, 0);
    static final LocalTime PM_START = new LocalTime(12, 0, 0);
    static final LocalTime PM_END = new LocalTime(23, 59, 59, 999);

    public EnglishTimeConstructBuilder() {
        addSlot(AM_START, PM_START, TamaIVRMessage.TIME_OF_DAY_AM);
        addSlot(PM_START, PM_END, TamaIVRMessage.TIME_OF_DAY_PM);
    }

    public List<String> build(LocalTime localTime) {
        ArrayList<String> messages = new ArrayList<String>();
        messages.add(getNumberFilename(DateTimeFormat.forPattern(TimeConstructBuilder.HOUR_OF_HALF_DAY).print(localTime)));
        if (localTime.getMinuteOfHour() != 0) {
            messages.add(getNumberFilename(DateTimeFormat.forPattern(TimeConstructBuilder.MINUTE_OF_THE_HOUR).print(localTime)));
        }
        messages.add(getTimePeriod(localTime));
        return messages;
    }
}
