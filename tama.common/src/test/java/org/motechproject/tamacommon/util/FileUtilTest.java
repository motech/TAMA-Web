package org.motechproject.tamacommon.util;

import org.junit.Test;

import static junit.framework.Assert.assertEquals;

public class FileUtilTest {
    @Test
    public void shouldReturnLowerCaseFilename() {
        String sanitized = new FileUtil().sanitizeFilename("PiER");
        assertEquals("pier", sanitized);
    }

    @Test
    public void shouldStripSpacesFromFilename() {
        String sanitized = new FileUtil().sanitizeFilename("pier walkway");
        assertEquals("pier_walkway", sanitized);
    }

    @Test
    public void shouldStripMultipleSpacesFromFilename() {
        String sanitized = new FileUtil().sanitizeFilename("pier   walkway");
        assertEquals("pier_walkway", sanitized);
    }

    @Test
    public void shouldStripSpecialCharactersFromFilename() {
        String sanitized = new FileUtil().sanitizeFilename("pier//walkway");
        assertEquals("pier_walkway", sanitized);
    }

    @Test
    public void shouldFoo() {
        String sanitized = new FileUtil().sanitizeFilename("pier _ walkway");
        assertEquals("pier_walkway", sanitized);
    }

    @Test
    public void shouldLeaveFileExtensionAlone() {
        String sanitized = new FileUtil().sanitizeFilename("pier.walkway");
        assertEquals("pier.walkway", sanitized);
    }
}
