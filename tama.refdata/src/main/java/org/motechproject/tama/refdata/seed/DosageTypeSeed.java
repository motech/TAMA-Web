package org.motechproject.tama.refdata.seed;

import org.motechproject.deliverytools.seed.Seed;
import org.motechproject.tama.refdata.domain.DosageType;
import org.motechproject.tama.refdata.repository.AllDosageTypes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DosageTypeSeed {

    @Autowired
    private AllDosageTypes allDosageTypes;

    @Seed(version = "1.0", priority = 0)
    public void load() {
        allDosageTypes.add(new DosageType("Morning Daily"));
        allDosageTypes.add(new DosageType("Evening Daily"));
        allDosageTypes.add(new DosageType("Twice Daily"));
        allDosageTypes.add(new DosageType("Variable Dosage"));
    }
}
