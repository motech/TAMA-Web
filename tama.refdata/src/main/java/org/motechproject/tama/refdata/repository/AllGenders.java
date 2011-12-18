package org.motechproject.tama.refdata.repository;

import org.ektorp.CouchDbConnector;
import org.motechproject.tama.common.repository.AbstractCouchRepository;
import org.motechproject.tama.refdata.domain.Gender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

@Repository
public class AllGenders extends AbstractCouchRepository<Gender> {

    @Autowired
    public AllGenders(@Qualifier("tamaDbConnector") CouchDbConnector db) {
        super(Gender.class, db);
        initStandardDesignDocument();
    }
}
