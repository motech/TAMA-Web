package org.motechproject.tama.dailypillreminder.builder.timeconstruct;

import org.motechproject.tama.common.TamaException;
import org.motechproject.tama.refdata.domain.IVRLanguage;

import java.util.HashMap;
import java.util.Map;

public class TimeConstructBuilder {
    public static final String HOUR_OF_HALF_DAY = "h";
    public static final String MINUTE_OF_THE_HOUR = "m";

    Map<String, SlotBasedTimeConstructBuilder> languageBasedTimeConstructMap = new HashMap<String, SlotBasedTimeConstructBuilder>();

    public TimeConstructBuilder() {
        languageBasedTimeConstructMap.put(IVRLanguage.ENGLISH_CODE, new EnglishTimeConstructBuilder());
        languageBasedTimeConstructMap.put(IVRLanguage.HINDI_CODE, new HindiTimeConstructBuilder());
        languageBasedTimeConstructMap.put(IVRLanguage.MARATHI_CODE, new MarathiTimeConstructBuilder());
        languageBasedTimeConstructMap.put(IVRLanguage.TAMIL_CODE, new TamilTimeConstructBuilder());
    }

    public SlotBasedTimeConstructBuilder builder(String preferredLanguage) {
        SlotBasedTimeConstructBuilder timeConstructBuilder = languageBasedTimeConstructMap.get(preferredLanguage);
        if (timeConstructBuilder == null) {
            throw new TamaException(String.format("TimeConstruct for Preferred Language - %s not supported", preferredLanguage));
        }
        return timeConstructBuilder;
    }
}
