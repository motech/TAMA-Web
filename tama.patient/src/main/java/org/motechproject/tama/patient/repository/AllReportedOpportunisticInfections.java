package org.motechproject.tama.patient.repository;

import org.ektorp.CouchDbConnector;
import org.motechproject.tama.common.repository.AllAuditRecords;
import org.motechproject.tama.common.repository.AuditableCouchRepository;
import org.motechproject.tama.patient.domain.ReportedOpportunisticInfections;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

@Component
public class AllReportedOpportunisticInfections extends AuditableCouchRepository<ReportedOpportunisticInfections> {

    @Autowired
    public AllReportedOpportunisticInfections(@Qualifier("tamaDbConnector") CouchDbConnector db, AllAuditRecords allAuditRecords) {
        super(ReportedOpportunisticInfections.class, db, allAuditRecords);
        initStandardDesignDocument();
    }

}
