package org.motechproject.tama.repository;

import org.ektorp.CouchDbConnector;
import org.motechproject.tama.domain.Company;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

@Repository
public class AllCompanies extends AbstractCouchRepository<Company> {

    @Autowired
    public AllCompanies(@Qualifier("tamaDbConnector") CouchDbConnector db) {
        super(Company.class, db);
    }
}
