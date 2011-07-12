package org.motechproject.tama.repository;

import org.ektorp.CouchDbConnector;
import org.motechproject.tama.domain.Company;

public class Companies extends AbstractCouchRepository<Company> {

    public Companies(CouchDbConnector db) {
        super(Company.class, db);
    }
}
