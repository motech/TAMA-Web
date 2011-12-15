package org.motechproject.tamacallflow.ivr.builder.timeconstruct;

import org.joda.time.LocalTime;
import org.motechproject.tamacommon.TamaException;
import org.motechproject.tamadomain.domain.IVRLanguage;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.motechproject.tamadomain.domain.IVRLanguage.*;

public class TimeConstructBuilder {
    public static final String HOUR_OF_HALF_DAY = "h";
    public static final String MINUTE_OF_THE_HOUR = "m";
    public static final String AM_PM = "a";

    Map<String, SlotBasedTimeConstructBuilder> languageBasedTimeConstructMap = new HashMap<String, SlotBasedTimeConstructBuilder>();
    public TimeConstructBuilder(){
        Map<String, SlotBasedTimeConstructBuilder> map = languageBasedTimeConstructMap;
        map.put(ENGLISH_CODE, new EnglishTimeConstructBuilder());
        map.put(HINDI_CODE, new HindiTimeConstructBuilder());
        map.put(MARATHI_CODE, new MarathiTimeConstructBuilder());
    }
    
    public List<String> build(String preferredLanguage, LocalTime localTime) {
        SlotBasedTimeConstructBuilder timeConstructBuilder = languageBasedTimeConstructMap.get(preferredLanguage);
        if (timeConstructBuilder == null)
            throw new TamaException(String.format("TimeConstruct for Preferred Language - %s not supported ", preferredLanguage));
        return timeConstructBuilder.build(localTime);
    }
}
