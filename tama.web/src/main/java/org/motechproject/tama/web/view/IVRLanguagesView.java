package org.motechproject.tama.web.view;


import org.motechproject.tama.refdata.domain.IVRLanguage;
import org.motechproject.tama.refdata.objectcache.AllIVRLanguagesCache;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class IVRLanguagesView {

    private final AllIVRLanguagesCache allIVRLanguages;

    public IVRLanguagesView(AllIVRLanguagesCache allIVRLanguages) {
        this.allIVRLanguages = allIVRLanguages;
    }

    public List<IVRLanguage> getAll() {
        List<IVRLanguage> allIvrLanguages = allIVRLanguages.getAll();
        Collections.sort(allIvrLanguages, new Comparator<IVRLanguage>() {
            @Override
            public int compare(IVRLanguage ivrLanguage, IVRLanguage otherIVRLanguage) {
                return ivrLanguage.getName().toLowerCase().compareTo(otherIVRLanguage.getName().toLowerCase());
            }
        });
        return allIvrLanguages;
    }
}
