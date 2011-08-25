package org.motechproject.tama.repository;

import org.ektorp.CouchDbConnector;
import org.ektorp.support.CouchDbRepositorySupport;
import org.ektorp.support.View;
import org.motechproject.tama.domain.LabTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

@Repository
@View(name = "all", map = "function(doc) { if (doc.documentType == 'LabTest') { emit(null, doc) } }")
public class LabTests extends CouchDbRepositorySupport<LabTest> {

    @Autowired
    public LabTests(@Qualifier("tamaDbConnector") CouchDbConnector db) {
        super(LabTest.class, db);
        initStandardDesignDocument();
    }
}
