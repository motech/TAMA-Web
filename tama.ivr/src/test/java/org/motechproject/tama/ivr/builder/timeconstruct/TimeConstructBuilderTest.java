package org.motechproject.tama.ivr.builder.timeconstruct;

import org.junit.Test;
import org.motechproject.tama.refdata.domain.IVRLanguage;

import static junit.framework.Assert.assertEquals;

public class TimeConstructBuilderTest {

    @Test
    public void constructEnglishTimeConstructBuilder() {
        SlotBasedTimeConstructBuilder builder = new TimeConstructBuilder().builder(IVRLanguage.ENGLISH_CODE);
        assertEquals(EnglishTimeConstructBuilder.class, builder.getClass());
    }

    @Test
    public void constructMarathiTimeConstructBuilder() {
        SlotBasedTimeConstructBuilder builder = new TimeConstructBuilder().builder(IVRLanguage.MARATHI_CODE);
        assertEquals(MarathiTimeConstructBuilder.class, builder.getClass());
    }

    @Test
    public void constructHindiTimeConstructBuilder() {
        SlotBasedTimeConstructBuilder builder = new TimeConstructBuilder().builder(IVRLanguage.HINDI_CODE);
        assertEquals(HindiTimeConstructBuilder.class, builder.getClass());
    }

    @Test
    public void constructTamilTimeConstructBuilder() {
        SlotBasedTimeConstructBuilder builder = new TimeConstructBuilder().builder(IVRLanguage.TAMIL_CODE);
        assertEquals(TamilTimeConstructBuilder.class, builder.getClass());
    }

    @Test
    public void constructTeluguTimeConstructBuilder() {
        SlotBasedTimeConstructBuilder builder = new TimeConstructBuilder().builder(IVRLanguage.TELUGU_CODE);
        assertEquals(TeluguTimeConstructBuilder.class, builder.getClass());
    }

    @Test
    public void constructTimeConstructBuilder_ForNotSupportedLanguage() {
        try {
            new TimeConstructBuilder().builder("unknown");
        }
        catch(Exception e) {
            assertEquals("TimeConstruct for Preferred Language - unknown not supported", e.getMessage());
        }
    }
}
