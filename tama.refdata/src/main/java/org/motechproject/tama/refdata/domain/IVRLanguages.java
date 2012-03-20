package org.motechproject.tama.refdata.domain;

import java.util.ArrayList;
import java.util.Collection;

public class IVRLanguages extends ArrayList<IVRLanguage> {
    public IVRLanguages() {
    }

    public IVRLanguages(Collection<? extends IVRLanguage> c) {
        super(c);
    }

    public IVRLanguage getBy(String code) {
        for (IVRLanguage ivrLanguage : this) {
            if (ivrLanguage.getCode().equals(code))
                return ivrLanguage;
        }
        return null;
    }
}
