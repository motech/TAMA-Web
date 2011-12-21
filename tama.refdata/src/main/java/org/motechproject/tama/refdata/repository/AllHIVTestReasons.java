package org.motechproject.tama.refdata.repository;

import org.ektorp.CouchDbConnector;
import org.ektorp.support.View;
import org.motechproject.tama.common.repository.AbstractCouchRepository;
import org.motechproject.tama.refdata.domain.HIVTestReason;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

@Repository
public class AllHIVTestReasons extends AbstractCouchRepository<HIVTestReason> {

    @Autowired
    public AllHIVTestReasons(@Qualifier("tamaDbConnector") CouchDbConnector db) {
        super(HIVTestReason.class, db);
        initStandardDesignDocument();
    }
}