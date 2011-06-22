package org.motechproject.tama.repository;

import org.ektorp.CouchDbConnector;
import org.ektorp.ViewQuery;
import org.ektorp.support.CouchDbRepositorySupport;
import org.ektorp.support.GenerateView;
import org.ektorp.support.View;
import org.motechproject.tama.Patient;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.List;

@View(name = "all", map = "function(doc) { if (doc.documentType == 'Patient') { emit(null, doc) } }")
public class Patients extends CouchDbRepositorySupport<Patient> {

    public Patients(@Qualifier("patients") CouchDbConnector db) {
        super(Patient.class, db);
        initStandardDesignDocument();
    }

    @GenerateView
    @Override
    public List<Patient> getAll() {
        ViewQuery q = createQuery("all").descending(true);
        return db.queryView(q, Patient.class);
    }

}
