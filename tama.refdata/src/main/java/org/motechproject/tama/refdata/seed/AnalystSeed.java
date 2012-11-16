package org.motechproject.tama.refdata.seed;

import org.motechproject.deliverytools.seed.Seed;
import org.motechproject.tama.refdata.domain.Analyst;
import org.motechproject.tama.refdata.repository.AllAnalysts;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AnalystSeed {

    @Autowired
    AllAnalysts allAnalysts;

    @Seed(version = "2.0", priority = 0)
    public void load() {
        allAnalysts.add(new Analyst("Analyst", "analyst", "password"));
    }
}
