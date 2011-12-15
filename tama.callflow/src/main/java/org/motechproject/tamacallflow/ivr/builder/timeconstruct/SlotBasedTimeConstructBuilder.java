package org.motechproject.tamacallflow.ivr.builder.timeconstruct;

import org.joda.time.LocalTime;
import org.joda.time.format.DateTimeFormat;
import org.motechproject.tamacallflow.ivr.TamaIVRMessage;

import java.util.ArrayList;
import java.util.List;

public class SlotBasedTimeConstructBuilder {

    class TimeSlot {
        LocalTime startTime;
        LocalTime endTime;
        String name;

        TimeSlot(LocalTime startTime, LocalTime endTime, String name) {
            this.startTime = startTime;
            this.endTime = endTime;
            this.name = name;
        }

        boolean includes(LocalTime time) {
            return time.isEqual(startTime) || (time.isAfter(startTime) && time.isBefore(endTime));
        }

        public String getName() {
            return name;
        }
    }

    private ArrayList<TimeSlot> slots = new ArrayList<TimeSlot>();

    public void addSlot(LocalTime startTime, LocalTime endTime, String name) {
        slots.add(new TimeSlot(startTime, endTime, name));
    }

    public List<String> build(LocalTime time) {
        ArrayList<String> messages = new ArrayList<String>();
        messages.add(getTimePeriod(time));
        messages.add(getNumberFilename(DateTimeFormat.forPattern(TimeConstructBuilder.HOUR_OF_HALF_DAY).print(time)));
        if (time.getMinuteOfHour() == 0) {
            messages.add(TamaIVRMessage.TIME_OF_DAY_HOURS);
        } else {
            messages.add(TamaIVRMessage.TIME_OF_DAY_HOURS_AND);
            messages.add(getNumberFilename(DateTimeFormat.forPattern(TimeConstructBuilder.MINUTE_OF_THE_HOUR).print(time)));
            messages.add(TamaIVRMessage.MINUTES);
        }
        return messages;
    }

    String getTimePeriod(LocalTime time) {
        for (TimeSlot slot : slots) {
            if (slot.includes(time)) return slot.getName();
        }
        return null;
    }

    String getNumberFilename(String number) {
        return String.format(TamaIVRMessage.NUMBER_WAV_FORMAT, Integer.parseInt(number));
    }
}