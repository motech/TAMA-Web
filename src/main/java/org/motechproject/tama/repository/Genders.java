package org.motechproject.tama.repository;

import org.ektorp.CouchDbConnector;
import org.ektorp.support.CouchDbRepositorySupport;
import org.motechproject.tama.Gender;
import org.motechproject.tama.Patient;

public class Genders extends CouchDbRepositorySupport<Gender> {

    public Genders(CouchDbConnector db) {
        super(Gender.class, db);
        initStandardDesignDocument();
    }
}
