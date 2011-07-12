package org.motechproject.tama.tools.seed;

import org.motechproject.tama.domain.Administrator;
import org.motechproject.tama.repository.Administrators;
import org.springframework.beans.factory.annotation.Autowired;

public class AdministratorSeed extends Seed {
    @Autowired
    private Administrators administrators;

    @Override
    protected void load() {
        administrators.add(new Administrator("Administrator", "admin", "password"));
    }
}
