package org.motechproject.tama.patient.repository;

import org.ektorp.CouchDbConnector;
import org.motechproject.tama.common.repository.AbstractCouchRepository;
import org.motechproject.tama.patient.domain.ReportedOpportunisticInfections;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

@Repository
public class AllReportedOpportunisticInfections extends AbstractCouchRepository<ReportedOpportunisticInfections> {

    @Autowired
    public AllReportedOpportunisticInfections(@Qualifier("tamaDbConnector") CouchDbConnector db) {
        super(ReportedOpportunisticInfections.class, db);
        initStandardDesignDocument();
    }
}
