package org.motechproject.tama.refdata.seed;

import org.motechproject.deliverytools.seed.Seed;
import org.motechproject.tama.refdata.domain.IVRLanguage;
import org.motechproject.tama.refdata.repository.AllIVRLanguages;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class IVRLanguageSeed {

    @Autowired
    private AllIVRLanguages languages;

    @Seed(version = "1.0", priority = 0)
    public void load() {
        languages.add(IVRLanguage.newIVRLanguage("Hindi", IVRLanguage.HINDI_CODE));
        languages.add(IVRLanguage.newIVRLanguage("English", IVRLanguage.ENGLISH_CODE));
        languages.add(IVRLanguage.newIVRLanguage("Telugu", IVRLanguage.TELUGU_CODE));
        languages.add(IVRLanguage.newIVRLanguage("Kannada", IVRLanguage.KANNADA_CODE));
        languages.add(IVRLanguage.newIVRLanguage("Tamil", IVRLanguage.TAMIL_CODE));
        languages.add(IVRLanguage.newIVRLanguage("Manipuri", IVRLanguage.MANIPURI_CODE));
        languages.add(IVRLanguage.newIVRLanguage("Marathi", IVRLanguage.MARATHI_CODE));
    }
}