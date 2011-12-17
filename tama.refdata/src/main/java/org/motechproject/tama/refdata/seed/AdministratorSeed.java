package org.motechproject.tama.refdata.seed;

import org.motechproject.tama.refdata.domain.Administrator;
import org.motechproject.tama.refdata.repository.AllAdministrators;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AdministratorSeed extends Seed {
    @Autowired
    private AllAdministrators allAdministrators;

    @Override
    protected void load() {
        allAdministrators.add(new Administrator("Administrator", "admin", "password"));
    }
}
