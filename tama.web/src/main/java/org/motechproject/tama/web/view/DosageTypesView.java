package org.motechproject.tama.web.view;


import org.motechproject.tama.refdata.domain.DosageType;
import org.motechproject.tama.refdata.objectcache.AllDosageTypesCache;
import org.motechproject.tama.refdata.repository.AllDosageTypes;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class DosageTypesView {

    private final AllDosageTypesCache allDosageTypes;

    public DosageTypesView(AllDosageTypesCache allDosageTypes) {
        this.allDosageTypes = allDosageTypes;
    }

    public List<DosageType> getAll() {
        List<DosageType> allDosageTypes = this.allDosageTypes.getAll();
        Collections.sort(allDosageTypes, new Comparator<DosageType>() {
            @Override
            public int compare(DosageType dosageType, DosageType dosageType1) {
                return dosageType.getType().toLowerCase().compareTo(dosageType1.getType().toLowerCase());
            }
        });
        return allDosageTypes;
    }
}
