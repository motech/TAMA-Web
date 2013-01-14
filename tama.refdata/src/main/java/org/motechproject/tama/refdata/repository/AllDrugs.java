package org.motechproject.tama.refdata.repository;

import org.ektorp.CouchDbConnector;
import org.motechproject.tama.common.repository.AbstractCouchRepository;
import org.motechproject.tama.refdata.domain.Drug;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

@Component
public class AllDrugs extends AbstractCouchRepository<Drug> {

    @Autowired
    protected AllDrugs(@Qualifier("tamaDbConnector") CouchDbConnector db) {
        super(Drug.class, db);
    }
}