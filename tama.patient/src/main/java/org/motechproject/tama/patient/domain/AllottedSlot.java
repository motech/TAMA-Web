package org.motechproject.tama.patient.domain;

import org.joda.time.DateTime;
import org.joda.time.LocalTime;
import org.motechproject.util.DateUtil;

public class AllottedSlot {

    private DateTime slotTime;

    private int allottedCount;

    public AllottedSlot() {
    }

    public AllottedSlot(DateTime slotTime, int allottedCount) {
        this.slotTime = slotTime;
        this.allottedCount = allottedCount;
    }

    public LocalTime getSlotTimeAsLocalTime() {
        return slotTime == null ? null : slotTime.toLocalTime();
    }

    public DateTime getSlotTime() {
        return slotTime;
    }

    public void setSlotTime(DateTime slotTime) {
        this.slotTime = DateUtil.setTimeZone(slotTime);
    }

    public int getAllottedCount() {
        return allottedCount;
    }

    public void setAllottedCount(int allottedCount) {
        this.allottedCount = allottedCount;
    }
}
