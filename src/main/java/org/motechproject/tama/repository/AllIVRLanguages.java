package org.motechproject.tama.repository;

import org.ektorp.CouchDbConnector;
import org.ektorp.support.CouchDbRepositorySupport;
import org.ektorp.support.View;
import org.motechproject.tama.domain.IVRLanguage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

@Repository
@View( name="all", map = "function(doc) { if (doc.documentType == 'IVRLanguage') { emit(null, doc) } }")
public class AllIVRLanguages extends CouchDbRepositorySupport<IVRLanguage> {

    @Autowired
    public AllIVRLanguages(@Qualifier("tamaDbConnector") CouchDbConnector db) {
        super(IVRLanguage.class, db);
        initStandardDesignDocument();
    }
}
