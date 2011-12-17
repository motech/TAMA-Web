package org.motechproject.tama.common.util;

import org.junit.Test;

import static junit.framework.Assert.assertEquals;

public class FileUtilTest {
    @Test
    public void shouldReturnLowerCaseFilename() {
        String sanitized = FileUtil.sanitizeFilename("PiER");
        assertEquals("pier", sanitized);
    }

    @Test
    public void shouldStripSpacesFromFilename() {
        String sanitized = FileUtil.sanitizeFilename("pier walkway");
        assertEquals("pier_walkway", sanitized);
    }

    @Test
    public void shouldStripMultipleSpacesFromFilename() {
        String sanitized = FileUtil.sanitizeFilename("pier   walkway");
        assertEquals("pier_walkway", sanitized);
    }

    @Test
    public void shouldStripSpecialCharactersFromFilename() {
        String sanitized = FileUtil.sanitizeFilename("pier//walkway");
        assertEquals("pier_walkway", sanitized);
    }

    @Test
    public void shouldFoo() {
        String sanitized = FileUtil.sanitizeFilename("pier _ walkway");
        assertEquals("pier_walkway", sanitized);
    }

    @Test
    public void shouldLeaveFileExtensionAlone() {
        String sanitized = FileUtil.sanitizeFilename("pier.walkway");
        assertEquals("pier.walkway", sanitized);
    }
}
