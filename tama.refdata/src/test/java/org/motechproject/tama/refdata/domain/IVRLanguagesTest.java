package org.motechproject.tama.refdata.domain;

import org.junit.Test;

import java.util.Arrays;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNull;

public class IVRLanguagesTest {
    @Test
    public void getById(){
        IVRLanguage english = IVRLanguage.newIVRLanguage("English", "en");
        IVRLanguage hindi = IVRLanguage.newIVRLanguage("Hindi", "hi");
        IVRLanguages ivrLanguages = new IVRLanguages(Arrays.asList(english, hindi));

        assertEquals("en", ivrLanguages.getBy("en").getCode());
        assertEquals("hi", ivrLanguages.getBy("hi").getCode());
        assertNull(ivrLanguages.getBy("ta"));
    }
}
