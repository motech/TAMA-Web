package org.motechproject.tama.web.view;


import org.motechproject.tama.domain.DosageType;
import org.motechproject.tama.repository.DosageTypes;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class DosageTypesView {

    private final DosageTypes dosageTypes;

    public DosageTypesView(DosageTypes dosageTypes){
        this.dosageTypes = dosageTypes;
    }

    public List<DosageType> getAll() {
        List<DosageType> allDosageTypes = dosageTypes.getAll();
        Collections.sort(allDosageTypes, new Comparator<DosageType>() {
            @Override
            public int compare(DosageType dosageType, DosageType dosageType1) {
                return dosageType.getType().toLowerCase().compareTo(dosageType1.getType().toLowerCase());
            }
        });
        return allDosageTypes;
    }
}
