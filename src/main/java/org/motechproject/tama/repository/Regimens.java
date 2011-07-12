package org.motechproject.tama.repository;

import org.ektorp.CouchDbConnector;
import org.ektorp.support.CouchDbRepositorySupport;
import org.ektorp.support.View;
import org.motechproject.tama.domain.Regimen;

@View( name="all", map = "function(doc) { if (doc.documentType == 'Regimen') { emit(null, doc) } }")
public class Regimens extends CouchDbRepositorySupport<Regimen> {

    public Regimens(CouchDbConnector db) {
        super(Regimen.class, db);
        initStandardDesignDocument();
    }
}
