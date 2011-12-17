package org.motechproject.tama.refdata.repository;

import org.ektorp.CouchDbConnector;
import org.motechproject.tama.refdata.domain.Company;
import org.motechproject.tamacommon.repository.AbstractCouchRepository;
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
