package org.motechproject.tama.refdata.seed;

import org.motechproject.deliverytools.seed.Seed;
import org.motechproject.tama.refdata.domain.IVRLanguage;
import org.motechproject.tama.refdata.objectcache.AllIVRLanguagesCache;
import org.motechproject.tama.refdata.repository.AllIVRLanguages;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class IVRLanguageSeedV2 {

    @Autowired
    private AllIVRLanguages languages;

    @Autowired
    private AllIVRLanguagesCache allIVRLanguagesCache;

    @Seed(version = "2.0", priority = 0)
    public void load() {
        languages.add(IVRLanguage.newIVRLanguage("Gujarati", IVRLanguage.GUJARATI_CODE));
        allIVRLanguagesCache.refresh();
    }
}