package org.motechproject.tama.util;

import org.junit.Test;

import static junit.framework.Assert.assertEquals;

public class StringUtilTest {
    @Test
    public void shouldReturnLastMatch_WhenActualStringIsEmpty() {
        String lastMatchedString = StringUtil.lastMatch("", "/");
        assertEquals("", lastMatchedString);
    }

    @Test
    public void shouldReturnLastMatch_WhenActualStringMatches_CharacterToSplitAt() {
        String lastMatchedString = StringUtil.lastMatch("http://localhost:8080/tama", "/");
        assertEquals("tama", lastMatchedString);
    }

    @Test
    public void shouldReturnLastMatch_WhenActualStringMatches_NoCharacterToSplitAt() {
        String lastMatchedString = StringUtil.lastMatch("http://localhost:8080/tama", "#");
        assertEquals("http://localhost:8080/tama", lastMatchedString);
    }

    @Test
    public void shouldGetIVRMobilePhoneNumber() {
        assertEquals("09876543210", StringUtil.ivrMobilePhoneNumber("9876543210"));
    }
}
