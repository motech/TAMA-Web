package org.motechproject.tama.repository;

import org.ektorp.CouchDbConnector;
import org.ektorp.support.CouchDbRepositorySupport;
import org.ektorp.support.View;
import org.motechproject.tama.domain.IVRLanguage;

@View( name="all", map = "function(doc) { if (doc.documentType == 'IVRLanguage') { emit(null, doc) } }")
public class IVRLanguages extends CouchDbRepositorySupport<IVRLanguage> {

    public IVRLanguages(CouchDbConnector db) {
        super(IVRLanguage.class, db);
        initStandardDesignDocument();
    }
}
