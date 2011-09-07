package org.motechproject.tama.tools.seed;

import org.motechproject.tama.domain.IVRLanguage;
import org.motechproject.tama.repository.AllIVRLanguages;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class IVRLanguageSeed extends Seed {

    @Autowired
    private AllIVRLanguages languages;

    @Override
    public void load() {
        languages.add(IVRLanguage.newIVRLanguage("Hindi", "hi"));
        languages.add(IVRLanguage.newIVRLanguage("English", "en"));
        languages.add(IVRLanguage.newIVRLanguage("Telugu", "te"));
        languages.add(IVRLanguage.newIVRLanguage("Kannada", "kn"));
        languages.add(IVRLanguage.newIVRLanguage("Tamil", "ta"));
        languages.add(IVRLanguage.newIVRLanguage("Manipuri", "mni"));
        languages.add(IVRLanguage.newIVRLanguage("Marathi", "mr"));
    }

}