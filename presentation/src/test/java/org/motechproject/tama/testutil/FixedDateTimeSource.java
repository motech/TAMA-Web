package org.motechproject.tama.testutil;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.LocalDate;
import org.motechproject.util.datetime.DateTimeSource;

import java.util.Calendar;
import java.util.TimeZone;

public class FixedDateTimeSource implements DateTimeSource {
    private DateTimeZone timeZone;
    private DateTime fixedTime;

    public FixedDateTimeSource(DateTime fixedTime) {
        this.fixedTime = fixedTime;
        TimeZone tz = Calendar.getInstance().getTimeZone();
        this.timeZone = DateTimeZone.forTimeZone(tz);
    }

    @Override
    public DateTimeZone timeZone() {
        return timeZone;
    }

    @Override
    public DateTime now() {
        return fixedTime;
    }

    @Override
    public LocalDate today() {
        return fixedTime.toLocalDate();
    }
}