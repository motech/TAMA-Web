package org.motechproject.tama.repository;

import org.ektorp.CouchDbConnector;
import org.motechproject.tama.domain.Company;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class Companies extends AbstractCouchRepository<Company> {

    @Autowired
    public Companies(CouchDbConnector db) {
        super(Company.class, db);
    }
}
