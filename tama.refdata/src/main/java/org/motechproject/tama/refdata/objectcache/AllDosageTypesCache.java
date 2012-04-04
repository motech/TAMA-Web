package org.motechproject.tama.refdata.objectcache;

import org.motechproject.tama.refdata.domain.DosageType;
import org.motechproject.tama.refdata.repository.AllDosageTypes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

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
    public List<DosageType> getAll() {
        List<DosageType> all = super.getAll();
        Collections.sort(all, new Comparator<DosageType>() {
            @Override
            public int compare(DosageType dosageType1, DosageType dosageType2) {
                return dosageType1.getType().compareTo(dosageType2.getType());
            }
        });
        return all;
    }
}
