package org.motechproject.tama.refdata.seed;

import org.motechproject.deliverytools.seed.Seed;
import org.motechproject.tama.refdata.domain.IVRLanguage;
import org.motechproject.tama.refdata.repository.AllIVRLanguages;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ManipuriRemovalSeed {

    @Autowired
    private AllIVRLanguages languages;

    @Seed(version = "2.0", priority = 0)
    public void load() {
        languages.removeByCode(IVRLanguage.MANIPURI_CODE);
    }
}
