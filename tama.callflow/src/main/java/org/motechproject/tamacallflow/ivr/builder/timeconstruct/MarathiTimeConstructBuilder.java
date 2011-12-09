package org.motechproject.tamacallflow.ivr.builder.timeconstruct;

import org.joda.time.LocalTime;
import org.joda.time.format.DateTimeFormat;
import org.motechproject.tamacallflow.ivr.TamaIVRMessage;

import java.util.ArrayList;
import java.util.List;

public class MarathiTimeConstructBuilder {
    private final LocalTime MIDNIGHT_START = new LocalTime(0, 0, 0);
    private final LocalTime EARLY_MORNING_START = new LocalTime(3, 45, 0);
    private final LocalTime MORNING_START = new LocalTime(6, 0, 0);
    private final LocalTime NOON_START = new LocalTime(12, 0, 0);
    private final LocalTime EVENING_START = new LocalTime(16, 45, 0);
    private final LocalTime NIGHT_START = new LocalTime(19, 45, 0);
    private LocalTime localTime;

    public MarathiTimeConstructBuilder(LocalTime localTime) {
        this.localTime = localTime;
    }

    public List<String> build() {
        ArrayList<String> messages = new ArrayList<String>();
        messages.add(getTimePeriod());
        messages.add(getNumberFilename(DateTimeFormat.forPattern(TimeConstructBuilder.HOUR_OF_HALF_DAY).print(localTime)));
        if (localTime.getMinuteOfHour() == 0) {
            messages.add(TamaIVRMessage.TIME_OF_DAY_HOURS);
        }
        else {
            messages.add(TamaIVRMessage.TIME_OF_DAY_HOURS_AND);
            messages.add(getNumberFilename(DateTimeFormat.forPattern(TimeConstructBuilder.MINUTE_OF_THE_HOUR).print(localTime)));
            messages.add(TamaIVRMessage.MINUTES);
        }
        return messages;
    }

    private String getTimePeriod() {
        if (isBetween(MIDNIGHT_START, EARLY_MORNING_START))
            return TamaIVRMessage.TIME_OF_DAY_MIDNIGHT;
        else if (isBetween(EARLY_MORNING_START, MORNING_START))
            return TamaIVRMessage.TIME_OF_DAY_EARLY_MORNING;
        else if (isBetween(MORNING_START, NOON_START))
            return TamaIVRMessage.TIME_OF_DAY_MORNING;
        else if (isBetween(NOON_START, EVENING_START))
            return TamaIVRMessage.TIME_OF_DAY_AFTERNOON;
        else if (isBetween(EVENING_START, NIGHT_START))
            return TamaIVRMessage.TIME_OF_DAY_EVENING;
        else
            return TamaIVRMessage.TIME_OF_DAY_NIGHT;
    }

    private boolean isBetween(LocalTime startTime, LocalTime endTime) {
        return localTime.isEqual(startTime) || (localTime.isAfter(startTime) && localTime.isBefore(endTime));
    }

    private String getNumberFilename(String number) {
        return String.format(TamaIVRMessage.NUMBER_WAV_FORMAT, Integer.parseInt(number));
    }
}
