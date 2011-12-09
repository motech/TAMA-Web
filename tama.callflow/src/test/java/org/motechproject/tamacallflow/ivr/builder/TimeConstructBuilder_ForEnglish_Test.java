package org.motechproject.tamacallflow.ivr.builder;

import org.joda.time.LocalTime;
import org.junit.Test;
import org.motechproject.tamacallflow.ivr.builder.timeconstruct.TimeConstructBuilder;
import org.motechproject.tamadomain.domain.IVRLanguage;

import java.util.List;

import static junit.framework.Assert.assertEquals;

public class TimeConstructBuilder_ForEnglish_Test {

    @Test
    public void shouldCreateTimeConstruct_AtTheExactHour(){
        LocalTime tenAM = new LocalTime(10, 0, 0);
        List<String> timeConstructWAVs = new TimeConstructBuilder(IVRLanguage.ENGLISH_CODE).build(tenAM);
        assertEquals(2, timeConstructWAVs.size());
        assertEquals("Num_010", timeConstructWAVs.get(0));
        assertEquals("AM", timeConstructWAVs.get(1));
    }

    @Test
    public void shouldCreateTimeConstruct_AtSomeMinutesPastAnHour(){
        LocalTime ten30AM = new LocalTime(10, 30, 0);
        List<String> timeConstructWAVs = new TimeConstructBuilder(IVRLanguage.ENGLISH_CODE).build(ten30AM);
        assertEquals(3, timeConstructWAVs.size());
        assertEquals("Num_010", timeConstructWAVs.get(0));
        assertEquals("Num_030", timeConstructWAVs.get(1));
        assertEquals("AM", timeConstructWAVs.get(2));
    }

    @Test
    public void shouldCreateTimeConstruct_AtMidnight(){
        LocalTime midnight = new LocalTime(0, 0, 0);
        List<String> timeConstructWAVs = new TimeConstructBuilder(IVRLanguage.ENGLISH_CODE).build(midnight);
        assertEquals(2, timeConstructWAVs.size());
        assertEquals("Num_012", timeConstructWAVs.get(0));
        assertEquals("AM", timeConstructWAVs.get(1));
    }

    @Test
    public void shouldCreateTimeConstruct_AtNoon(){
        LocalTime noon = new LocalTime(12, 0, 0);
        List<String> timeConstructWAVs = new TimeConstructBuilder(IVRLanguage.ENGLISH_CODE).build(noon);
        assertEquals(2, timeConstructWAVs.size());
        assertEquals("Num_012", timeConstructWAVs.get(0));
        assertEquals("PM", timeConstructWAVs.get(1));
    }
}
