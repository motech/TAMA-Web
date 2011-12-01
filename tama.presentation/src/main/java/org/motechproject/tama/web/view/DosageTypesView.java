package org.motechproject.tama.web.view;


import org.motechproject.tamadomain.domain.DosageType;
import org.motechproject.tamadomain.repository.AllDosageTypes;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class DosageTypesView {

    private final AllDosageTypes allDosageTypes;

    public DosageTypesView(AllDosageTypes allDosageTypes){
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
