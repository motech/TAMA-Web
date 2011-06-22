package org.motechproject.tama.repository;

import org.ektorp.CouchDbConnector;
import org.ektorp.support.CouchDbRepositorySupport;
import org.motechproject.tama.Patient;
import org.springframework.beans.factory.annotation.Qualifier;

public class Patients extends CouchDbRepositorySupport<Patient> {

    public Patients(@Qualifier("patients") CouchDbConnector db) {
        super(Patient.class, db);
        initStandardDesignDocument();
    }
}
