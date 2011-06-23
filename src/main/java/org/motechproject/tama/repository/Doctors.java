package org.motechproject.tama.repository;

import org.ektorp.CouchDbConnector;
import org.ektorp.ViewQuery;
import org.ektorp.support.CouchDbRepositorySupport;
import org.ektorp.support.GenerateView;
import org.ektorp.support.View;
import org.motechproject.tama.Doctor;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.List;

public class Doctors extends CouchDbRepositorySupport<Doctor> {

    public Doctors(CouchDbConnector db) {
        super(Doctor.class, db);
        initStandardDesignDocument();
    }

}
