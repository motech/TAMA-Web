package org.motechproject.tama.repository;

import org.ektorp.CouchDbConnector;
import org.ektorp.support.CouchDbRepositorySupport;
import org.ektorp.support.View;
import org.motechproject.tama.Gender;

@View( name="all", map = "function(doc) { if (doc.documentType == 'Gender') { emit(null, doc) } }")
public class Genders extends CouchDbRepositorySupport<Gender> {

    public Genders(CouchDbConnector db) {
        super(Gender.class, db);
        initStandardDesignDocument();
    }
}
