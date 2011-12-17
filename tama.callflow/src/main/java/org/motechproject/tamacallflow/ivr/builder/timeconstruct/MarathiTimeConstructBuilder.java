package org.motechproject.tamacallflow.ivr.builder.timeconstruct;

import org.joda.time.LocalTime;
import org.motechproject.tamacallflow.ivr.TamaIVRMessage;

public class MarathiTimeConstructBuilder extends SlotBasedTimeConstructBuilder {
    private final LocalTime MIDNIGHT_START = new LocalTime(0, 0, 0);
    private final LocalTime EARLY_MORNING_START = new LocalTime(3, 45, 0);
    private final LocalTime MORNING_START = new LocalTime(6, 0, 0);
    private final LocalTime NOON_START = new LocalTime(12, 0, 0);
    private final LocalTime EVENING_START = new LocalTime(16, 45, 0);
    private final LocalTime NIGHT_START = new LocalTime(19, 45, 0);
    private final LocalTime NIGHT_END = new LocalTime(23, 59, 59, 999);

    public MarathiTimeConstructBuilder() {
        addSlot(MIDNIGHT_START, EARLY_MORNING_START, TamaIVRMessage.TIME_OF_DAY_MIDNIGHT);
        addSlot(EARLY_MORNING_START, MORNING_START, TamaIVRMessage.TIME_OF_DAY_EARLY_MORNING);
        addSlot(MORNING_START, NOON_START, TamaIVRMessage.TIME_OF_DAY_MORNING);
        addSlot(NOON_START, EVENING_START, TamaIVRMessage.TIME_OF_DAY_AFTERNOON);
        addSlot(EVENING_START, NIGHT_START, TamaIVRMessage.TIME_OF_DAY_EVENING);
        addSlot(NIGHT_START, NIGHT_END, TamaIVRMessage.TIME_OF_DAY_NIGHT);
    }
}
