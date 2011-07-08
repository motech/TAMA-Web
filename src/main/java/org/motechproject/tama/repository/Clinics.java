package org.motechproject.tama.repository;

import org.ektorp.CouchDbConnector;
import org.ektorp.support.GenerateView;
import org.motechproject.tama.domain.Clinic;
import org.motechproject.tama.domain.Company;

import java.util.List;

public class Clinics extends AbstractCouchRepository<Clinic> {

    public Clinics(CouchDbConnector db) {
        super(Clinic.class, db);
        initStandardDesignDocument();
    }

    public List<Clinic> findClinicEntries(int i, int sizeNo) {
        return getAll();
    }

}
