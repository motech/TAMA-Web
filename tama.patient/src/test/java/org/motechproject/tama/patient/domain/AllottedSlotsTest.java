package org.motechproject.tama.patient.domain;

import org.joda.time.DateTime;
import org.joda.time.LocalTime;
import org.junit.Test;

import java.util.Arrays;

import static junit.framework.Assert.assertEquals;

public class AllottedSlotsTest {

    @Test
    public void shouldCalculateTheTotalNumberOfSlotsAllotted_InAGivenSlotDuration() {

        AllottedSlot tenTenAMSlot = new AllottedSlot(DateTime.now().withTime(10, 10, 0, 0), 4);
        AllottedSlot tenTwentyAMSlot = new AllottedSlot(DateTime.now().withTime(10, 20, 0, 0), 10);
        AllottedSlot tenThirtyAMSlot = new AllottedSlot(DateTime.now().withTime(10, 30, 0, 0), 6);

        AllottedSlots allottedSlots = new AllottedSlots(Arrays.asList(tenTenAMSlot, tenTwentyAMSlot, tenThirtyAMSlot));

        assertEquals(4, allottedSlots.numberOfPatientsAllottedPerSlot(new LocalTime(10, 0), new LocalTime(10, 10)));
        assertEquals(4, allottedSlots.numberOfPatientsAllottedPerSlot(new LocalTime(10, 0), new LocalTime(10, 19)));
        assertEquals(14, allottedSlots.numberOfPatientsAllottedPerSlot(new LocalTime(10, 0), new LocalTime(10, 20)));
        assertEquals(14, allottedSlots.numberOfPatientsAllottedPerSlot(new LocalTime(10, 10), new LocalTime(10, 20)));
        assertEquals(16, allottedSlots.numberOfPatientsAllottedPerSlot(new LocalTime(10, 20), new LocalTime(10, 35)));
    }
}
