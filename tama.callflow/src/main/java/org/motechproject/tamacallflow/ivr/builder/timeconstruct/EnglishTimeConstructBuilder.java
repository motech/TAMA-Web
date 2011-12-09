package org.motechproject.tamacallflow.ivr.builder.timeconstruct;

import org.joda.time.LocalTime;
import org.joda.time.format.DateTimeFormat;
import org.motechproject.tamacallflow.ivr.TamaIVRMessage;

import java.util.ArrayList;
import java.util.List;

public class EnglishTimeConstructBuilder {

    private LocalTime localTime;

    public EnglishTimeConstructBuilder(LocalTime localTime) {
        this.localTime = localTime;
    }

    public List<String> build() {
        ArrayList<String> messages = new ArrayList<String>();
        messages.add(getNumberFilename(DateTimeFormat.forPattern(TimeConstructBuilder.HOUR_OF_HALF_DAY).print(localTime)));
        if (localTime.getMinuteOfHour() != 0) {
            messages.add(getNumberFilename(DateTimeFormat.forPattern(TimeConstructBuilder.MINUTE_OF_THE_HOUR).print(localTime)));
        }
        messages.add(DateTimeFormat.forPattern(TimeConstructBuilder.AM_PM).print(localTime));
        return messages;
    }

    private String getNumberFilename(String number) {
        return String.format(TamaIVRMessage.NUMBER_WAV_FORMAT, Integer.parseInt(number));
    }
}
