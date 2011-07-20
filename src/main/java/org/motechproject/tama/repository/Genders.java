package org.motechproject.tama.repository;

import org.ektorp.CouchDbConnector;
import org.ektorp.support.CouchDbRepositorySupport;
import org.ektorp.support.View;
import org.motechproject.tama.domain.Gender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

@Repository
@View( name="all", map = "function(doc) { if (doc.documentType == 'Gender') { emit(null, doc) } }")
public class Genders extends CouchDbRepositorySupport<Gender> {

    @Autowired
    public Genders(@Qualifier("tamaDbConnector") CouchDbConnector db) {
        super(Gender.class, db);
        initStandardDesignDocument();
    }
}
