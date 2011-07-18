package org.motechproject.tama.tools.seed;

import org.motechproject.tama.domain.IVRLanguage;
import org.motechproject.tama.repository.IVRLanguages;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class IVRLanguageSeed extends Seed {

    @Autowired
    private IVRLanguages languages;

    @Override
    public void load() {
        languages.add(IVRLanguage.newIVRLanguage("Hindi"));
        languages.add(IVRLanguage.newIVRLanguage("English"));
        languages.add(IVRLanguage.newIVRLanguage("Telugu"));
        languages.add(IVRLanguage.newIVRLanguage("Kannada"));
        languages.add(IVRLanguage.newIVRLanguage("Tamil"));
        languages.add(IVRLanguage.newIVRLanguage("Manipuri"));
        languages.add(IVRLanguage.newIVRLanguage("Marathi"));
    }

}