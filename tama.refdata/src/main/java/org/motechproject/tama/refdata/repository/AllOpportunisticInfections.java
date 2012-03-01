package org.motechproject.tama.refdata.repository;

import org.ektorp.CouchDbConnector;
import org.motechproject.tama.common.repository.AbstractCouchRepository;
import org.motechproject.tama.refdata.domain.OpportunisticInfection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

@Repository
public class AllOpportunisticInfections extends AbstractCouchRepository<OpportunisticInfection> {

    @Autowired
    public AllOpportunisticInfections(@Qualifier("tamaDbConnector") CouchDbConnector db) {
        super(OpportunisticInfection.class, db);
        initStandardDesignDocument();
    }
}
