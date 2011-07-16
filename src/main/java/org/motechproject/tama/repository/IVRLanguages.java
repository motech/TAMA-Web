package org.motechproject.tama.repository;

import org.ektorp.CouchDbConnector;
import org.ektorp.support.CouchDbRepositorySupport;
import org.ektorp.support.View;
import org.motechproject.tama.domain.IVRLanguage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
@View( name="all", map = "function(doc) { if (doc.documentType == 'IVRLanguage') { emit(null, doc) } }")
public class IVRLanguages extends CouchDbRepositorySupport<IVRLanguage> {

    @Autowired
    public IVRLanguages(CouchDbConnector db) {
        super(IVRLanguage.class, db);
        initStandardDesignDocument();
    }
}
