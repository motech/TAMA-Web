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

    @View(name = "all", map = "function(doc) { if (doc.documentType == 'Patient') { emit(null, doc) } }")
    @Override
    public List<Patient> getAll() {
        ViewQuery q = createQuery("all").descending(true);
        return db.queryView(q, Patient.class);
    }

    @View(name = "count", map = "function(doc) { if (doc.documentType == 'Patient') { emit('count', 1) } }", reduce = "function(key, values, rereduce){return sum(values)}")
    public long countAllPatients() {
        ViewQuery q = createQuery("count").descending(true);
        ViewResult result = db.queryView(q);
        long total = 0;
        for (ViewResult.Row row : result.getRows()) {
            String stringValue = row.getValue();
            total = row.getValueAsInt();
        }
        return total;
    }

}
