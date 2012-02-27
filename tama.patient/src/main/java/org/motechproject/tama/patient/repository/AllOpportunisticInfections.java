package org.motechproject.tama.patient.repository;

import org.ektorp.CouchDbConnector;
import org.motechproject.tama.common.repository.AbstractCouchRepository;
import org.motechproject.tama.patient.domain.OpportunisticInfections;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

@Repository
public class AllOpportunisticInfections extends AbstractCouchRepository<OpportunisticInfections> {

    @Autowired
    public AllOpportunisticInfections(@Qualifier("tamaDbConnector") CouchDbConnector db) {
        super(OpportunisticInfections.class, db);
        initStandardDesignDocument();
    }
}
