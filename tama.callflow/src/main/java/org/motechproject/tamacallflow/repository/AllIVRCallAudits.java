package org.motechproject.tamacallflow.repository;

import org.ektorp.CouchDbConnector;
import org.ektorp.support.CouchDbRepositorySupport;
import org.ektorp.support.View;
import org.motechproject.tamacallflow.domain.IVRCallAudit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

@Repository
@View(name = "all", map = "function(doc) { if (doc.documentType == 'IVRCallAudit') { emit(null, doc) } }")
public class AllIVRCallAudits extends CouchDbRepositorySupport<IVRCallAudit> {

    @Autowired
    public AllIVRCallAudits(@Qualifier("tamaDbConnector") CouchDbConnector db) {
        super(IVRCallAudit.class, db);
        initStandardDesignDocument();
    }
}
