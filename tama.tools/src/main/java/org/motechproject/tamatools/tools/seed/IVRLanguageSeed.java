package org.motechproject.tamatools.tools.seed;

import org.motechproject.tamadomain.domain.IVRLanguage;
import org.motechproject.tamadomain.repository.AllIVRLanguages;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class IVRLanguageSeed extends Seed {

    @Autowired
    private AllIVRLanguages languages;

    @Override
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