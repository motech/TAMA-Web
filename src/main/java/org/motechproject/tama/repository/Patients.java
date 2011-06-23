package org.motechproject.tama.repository;

import org.ektorp.CouchDbConnector;
import org.ektorp.ViewQuery;
import org.ektorp.ViewResult;
import org.ektorp.support.CouchDbRepositorySupport;
import org.ektorp.support.GenerateView;
import org.ektorp.support.View;
import org.motechproject.tama.Patient;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.List;

public class Patients extends CouchDbRepositorySupport<Patient> {

    public Patients(CouchDbConnector db) {
        super(Patient.class, db);
        initStandardDesignDocument();
    }

}
