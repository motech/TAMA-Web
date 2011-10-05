package org.motechproject.tama.tools.seed;

import org.motechproject.tama.domain.DosageType;
import org.motechproject.tama.repository.AllDosageTypes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DosageTypeSeed extends Seed {

    @Autowired
    private AllDosageTypes allDosageTypes;

    @Override
    public void load() {
        allDosageTypes.add(new DosageType("Morning Daily"));
        allDosageTypes.add(new DosageType("Evening Daily"));
        allDosageTypes.add(new DosageType("Twice Daily"));
        allDosageTypes.add(new DosageType("Variable Dosage"));
    }
}
