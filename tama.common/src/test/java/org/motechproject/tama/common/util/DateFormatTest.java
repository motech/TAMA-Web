package org.motechproject.tama.common.util;

import org.joda.time.DateTime;
import org.junit.Test;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import static org.motechproject.tama.common.util.DateFormat.format;
import static org.motechproject.util.DateUtil.newDateTime;

public class DateFormatTest {

    @Test
    public void shouldFormatDateAccordingToPattern() {
        assertEquals("10/10/2013", format(newDateTime(2013, 10, 10), "dd/MM/yyyy"));
    }

    @Test
    public void shouldReturnEmptyStringWhenNull() {
        assertTrue(format((DateTime) null, "").isEmpty());
    }

    @Test
    public void shouldMaintainTimeZoneWhenFormattingDate() {
        assertEquals("10/10/2013", format(newDateTime(2013, 10, 10, 0, 0, 0).toDate(), "dd/MM/yyyy"));
    }
}
