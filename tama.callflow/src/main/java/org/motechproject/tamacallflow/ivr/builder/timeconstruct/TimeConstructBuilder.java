package org.motechproject.tamacallflow.ivr.builder.timeconstruct;

import org.joda.time.LocalTime;
import org.motechproject.tamacommon.TamaException;
import org.motechproject.tamadomain.domain.IVRLanguage;

import java.util.List;

public class TimeConstructBuilder {
    public static final String HOUR_OF_HALF_DAY = "h";
    public static final String MINUTE_OF_THE_HOUR = "m";
    public static final String AM_PM = "a";

    private String preferredLanguage;

    public TimeConstructBuilder(String preferredLanguage) {
        this.preferredLanguage = preferredLanguage;
    }

    public List<String> build(LocalTime localTime) {
        if (IVRLanguage.ENGLISH_CODE.equals(preferredLanguage))
            return new EnglishTimeConstructBuilder(localTime).build();
        if (IVRLanguage.MARATHI_CODE.equals(preferredLanguage))
            return new MarathiTimeConstructBuilder(localTime).build();
        throw new TamaException(String.format("TimeConstruct for Preferred Language - %s not supported ", preferredLanguage));
    }
}
