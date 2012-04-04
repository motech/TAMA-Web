package org.motechproject.tama.refdata.objectcache;

import org.motechproject.tama.refdata.domain.DosageType;
import org.motechproject.tama.refdata.repository.AllDosageTypes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AllDosageTypesCache extends Cachable<DosageType> {

    @Autowired
    public AllDosageTypesCache(AllDosageTypes allDosageTypes) {
        super(allDosageTypes);
    }

    @Override
    protected String getKey(DosageType dosageType) {
        return dosageType.getId();
    }

    @Override
    protected int compareTo(DosageType t1, DosageType t2) {
        return t1.getType().compareTo(t2.getType());
    }
}
