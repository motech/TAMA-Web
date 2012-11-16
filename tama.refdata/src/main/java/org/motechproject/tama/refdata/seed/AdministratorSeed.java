package org.motechproject.tama.refdata.seed;

import org.motechproject.deliverytools.seed.Seed;
import org.motechproject.tama.refdata.domain.Administrator;
import org.motechproject.tama.refdata.repository.AllAdministrators;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AdministratorSeed {

    @Autowired
    private AllAdministrators allAdministrators;

    @Seed(version = "1.0", priority = 0)
    public void load() {
        allAdministrators.add(new Administrator("Administrator", "admin", "password"));
    }
}
