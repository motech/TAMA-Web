package org.motechproject.tama.refdata.repository;

import org.junit.Before;
import org.junit.Test;
import org.motechproject.tama.common.integration.repository.SpringIntegrationTest;
import org.motechproject.tama.refdata.domain.IVRLanguage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import static org.junit.Assert.assertNull;

@ContextConfiguration(locations = "classpath*:/applicationRefDataContext.xml")
public class AllIVRLanguagesTest extends SpringIntegrationTest {

    @Autowired
    private AllIVRLanguages allIVRLanguages;

    @Before
    public void setup() {
        allIVRLanguages.removeAll();
    }

    @Test
    public void shouldNotContainRemovedLanguage() {
        String languageCode = "languageCode";
        IVRLanguage languageRemoved = removedLanguage(addedLanguage(languageCode).getCode());
        assertNull(languageRemoved);
    }

    @Test
    public void shouldNotContainRemovedLanguageWithUnknownCode() {
        IVRLanguage languageRemoved = removedLanguage("unknownCode");
        assertNull(languageRemoved);
    }

    private IVRLanguage addedLanguage(String languageCode) {
        IVRLanguage language = newIVRLanguage(languageCode);
        allIVRLanguages.add(language);
        return allIVRLanguages.findByLanguageCode(language.getCode());
    }

    private IVRLanguage removedLanguage(String languageCode) {
        allIVRLanguages.removeByCode(languageCode);
        return allIVRLanguages.findByLanguageCode(languageCode);
    }

    private IVRLanguage newIVRLanguage(String languageCode) {
        IVRLanguage language = new IVRLanguage();
        language.setCode(languageCode);
        return language;
    }
}
