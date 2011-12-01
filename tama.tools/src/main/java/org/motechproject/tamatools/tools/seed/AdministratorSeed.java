package org.motechproject.tamatools.tools.seed;

import org.motechproject.tamadomain.domain.Administrator;
import org.motechproject.tamadomain.repository.AllAdministrators;
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
