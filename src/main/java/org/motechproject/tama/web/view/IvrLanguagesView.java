package org.motechproject.tama.web.view;


import org.motechproject.tama.domain.IVRLanguage;
import org.motechproject.tama.repository.IVRLanguages;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class IvrLanguagesView {

    private final IVRLanguages ivrLanguages;

    public IvrLanguagesView(IVRLanguages ivrLanguages){
        this.ivrLanguages = ivrLanguages;
    }

    public List<IVRLanguage> getAll() {
        List<IVRLanguage> allIvrLanguages = ivrLanguages.getAll();
        Collections.sort(allIvrLanguages, new Comparator<IVRLanguage>() {
            @Override
            public int compare(IVRLanguage ivrLanguage, IVRLanguage otherIVRLanguage) {
                return ivrLanguage.getName().toLowerCase().compareTo(otherIVRLanguage.getName().toLowerCase());
            }
        });
        return allIvrLanguages;
    }
}
