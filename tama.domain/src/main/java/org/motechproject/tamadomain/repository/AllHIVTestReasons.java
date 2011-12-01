package org.motechproject.tamadomain.repository;

import org.ektorp.CouchDbConnector;
import org.ektorp.support.CouchDbRepositorySupport;
import org.ektorp.support.View;
import org.motechproject.tamadomain.domain.HIVTestReason;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

@Repository
@View( name="all", map = "function(doc) { if (doc.documentType == 'HIVTestReason') { emit(null, doc) } }")
public class AllHIVTestReasons extends CouchDbRepositorySupport<HIVTestReason> {

    @Autowired
    public AllHIVTestReasons(@Qualifier("tamaDbConnector") CouchDbConnector db) {
        super(HIVTestReason.class, db);
        initStandardDesignDocument();
    }
}
