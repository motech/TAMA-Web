package org.motechproject.tama.repository;

import org.ektorp.CouchDbConnector;
import org.ektorp.support.CouchDbRepositorySupport;
import org.ektorp.support.View;
import org.motechproject.tama.domain.HIVTestReason;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

@Repository
@View( name="all", map = "function(doc) { if (doc.documentType == 'HIVTestReason') { emit(null, doc) } }")
public class HIVTestReasons extends CouchDbRepositorySupport<HIVTestReason> {

    @Autowired
    public HIVTestReasons(@Qualifier("tamaDbConnector") CouchDbConnector db) {
        super(HIVTestReason.class, db);
        initStandardDesignDocument();
    }
}
