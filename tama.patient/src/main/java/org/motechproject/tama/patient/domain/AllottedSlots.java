package org.motechproject.tama.patient.domain;

import org.joda.time.LocalTime;

import java.util.ArrayList;
import java.util.Collection;

public class AllottedSlots extends ArrayList<AllottedSlot> {

    public AllottedSlots() {
    }

    public AllottedSlots(Collection<? extends AllottedSlot> c) {
        super(c);
    }

    public int numberOfPatientsAllottedPerSlot(LocalTime slotStartTime, LocalTime slotEndTime) {
        int numberOfPatientsAllottedPerSlot = 0;
        for (AllottedSlot allottedSlot : this) {
            LocalTime slotTime = allottedSlot.getSlotTimeAsLocalTime();
            if (slotTime == null) continue;
            if (slotTime.isBefore(slotStartTime) || slotTime.isAfter(slotEndTime)) continue;
            numberOfPatientsAllottedPerSlot += allottedSlot.getAllottedCount();
        }
        return numberOfPatientsAllottedPerSlot;
    }
}
