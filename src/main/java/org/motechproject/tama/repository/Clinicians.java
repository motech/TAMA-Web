package org.motechproject.tama.repository;

import org.ektorp.CouchDbConnector;
import org.motechproject.tama.domain.Clinician;
import sun.reflect.generics.repository.AbstractRepository;

public class Clinicians extends AbstractCouchRepository<Clinician>{

    public Clinicians(CouchDbConnector db) {
        super(Clinician.class, db);
        initStandardDesignDocument();
    }
}
