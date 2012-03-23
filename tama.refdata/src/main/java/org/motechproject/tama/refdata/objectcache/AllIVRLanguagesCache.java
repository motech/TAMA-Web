package org.motechproject.tama.refdata.objectcache;

import org.motechproject.tama.refdata.domain.IVRLanguage;
import org.motechproject.tama.refdata.repository.AllIVRLanguages;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;

@Component
public class AllIVRLanguagesCache extends Cachable<IVRLanguage>{
    private HashMap<String, IVRLanguage> codeObjectMap = new HashMap<String, IVRLanguage>();

    @Autowired
    public AllIVRLanguagesCache(AllIVRLanguages allIVRLanguages) {
        super(allIVRLanguages);
        populateCodeObjectMap();
    }

    public IVRLanguage getByCode(String code){
        return codeObjectMap.get(code);
    }

    @Override
    protected String getKey(IVRLanguage ivrLanguage) {
        return ivrLanguage.getId();
    }

    private void populateCodeObjectMap() {
        for (IVRLanguage ivrLanguage : getAll()) {
            codeObjectMap.put(ivrLanguage.getCode(), ivrLanguage);
        }
    }
}
