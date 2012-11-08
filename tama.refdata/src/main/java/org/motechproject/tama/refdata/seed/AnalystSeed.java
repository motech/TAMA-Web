package org.motechproject.tama.refdata.seed;

import org.motechproject.tama.refdata.domain.Analyst;
import org.motechproject.tama.refdata.repository.AllAnalysts;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AnalystSeed extends Seed {
    @Autowired
    AllAnalysts allAnalysts;

    @Override
    protected void load() {
        allAnalysts.add(new Analyst("Analyst", "analyst", "password"));
    }
}
