package org.motechproject.tama.ivr.builder;

import junit.framework.Assert;
import org.junit.Test;

import java.util.List;

public class IVRDayMessageBuilderTest {
    @Test
    public void constructMessageWhenPreviousDosageIsForYesterdayMorning() {
        IVRDayMessageBuilder ivrDayMessageBuilder = new IVRDayMessageBuilder("dosageId", "dosageId", 10);

        List<String> messages = ivrDayMessageBuilder.getMessages("yesterday", "morning", "evening");
        Assert.assertEquals(2, messages.size());
        Assert.assertEquals("yesterday", messages.get(0));
        Assert.assertEquals("morning", messages.get(1));
    }

    @Test
    public void constructMessageWhenPreviousDosageIsForYesterdayEvening() {
        IVRDayMessageBuilder ivrDayMessageBuilder = new IVRDayMessageBuilder("currentDosageId", "previousDosageId", 16);

        List<String> messages = ivrDayMessageBuilder.getMessages("yesterday", "morning", "evening");
        Assert.assertEquals(2, messages.size());
        Assert.assertEquals("yesterday", messages.get(0));
        Assert.assertEquals("evening", messages.get(1));
    }

    @Test
    public void constructMessageWhenPreviousDosageIsForTodayMorning() {
        IVRDayMessageBuilder ivrDayMessageBuilder = new IVRDayMessageBuilder("currentDosageId", "previousDosageId", 10);

        List<String> messages = ivrDayMessageBuilder.getMessages("yesterday", "morning", "evening");
        Assert.assertEquals(1, messages.size());
        Assert.assertEquals("morning", messages.get(0));
    }
}
