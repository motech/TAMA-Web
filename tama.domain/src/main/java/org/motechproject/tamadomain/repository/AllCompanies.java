package org.motechproject.tamadomain.repository;

import org.ektorp.CouchDbConnector;
import org.motechproject.tamacommon.repository.AbstractCouchRepository;
import org.motechproject.tamadomain.domain.Company;
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
