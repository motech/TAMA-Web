package org.motechproject.tama.ivr.repository;

import org.ektorp.CouchDbConnector;
import org.motechproject.tama.common.repository.AbstractCouchRepository;
import org.motechproject.tama.ivr.domain.IVRCallAudit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

@Repository
public class AllIVRCallAudits extends AbstractCouchRepository<IVRCallAudit> {

    @Autowired
    public AllIVRCallAudits(@Qualifier("tamaDbConnector") CouchDbConnector db) {
        super(IVRCallAudit.class, db);
        initStandardDesignDocument();
    }
}
