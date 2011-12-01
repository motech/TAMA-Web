package org.motechproject.tamadomain.repository;

import org.ektorp.CouchDbConnector;
import org.ektorp.support.CouchDbRepositorySupport;
import org.ektorp.support.View;
import org.motechproject.tamadomain.domain.Gender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

@Repository
@View( name="all", map = "function(doc) { if (doc.documentType == 'Gender') { emit(null, doc) } }")
public class AllGenders extends CouchDbRepositorySupport<Gender> {

    @Autowired
    public AllGenders(@Qualifier("tamaDbConnector") CouchDbConnector db) {
        super(Gender.class, db);
        initStandardDesignDocument();
    }
}
