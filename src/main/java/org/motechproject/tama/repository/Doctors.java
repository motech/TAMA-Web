package org.motechproject.tama.repository;

import org.ektorp.CouchDbConnector;
import org.ektorp.support.CouchDbRepositorySupport;
import org.ektorp.support.View;
import org.motechproject.tama.Doctor;

@View( name="all", map = "function(doc) { if (doc.documentType == 'Doctor') { emit(null, doc) } }")
public class Doctors extends CouchDbRepositorySupport<Doctor> {

    public Doctors(CouchDbConnector db) {
        super(Doctor.class, db);
        initStandardDesignDocument();
    }

}
