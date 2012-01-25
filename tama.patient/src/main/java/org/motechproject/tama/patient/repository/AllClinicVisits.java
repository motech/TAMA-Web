package org.motechproject.tama.patient.repository;

import org.ektorp.CouchDbConnector;
import org.motechproject.tama.common.repository.AbstractCouchRepository;
import org.motechproject.tama.patient.domain.ClinicVisit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

@Repository
public class AllClinicVisits extends AbstractCouchRepository<ClinicVisit> {

    @Autowired
    public AllClinicVisits(@Qualifier("tamaDbConnector") CouchDbConnector db) {
        super(ClinicVisit.class, db);
        initStandardDesignDocument();
    }

}
