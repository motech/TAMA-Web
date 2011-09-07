package org.motechproject.tama.repository;

import org.ektorp.CouchDbConnector;
import org.motechproject.tama.domain.Patient;
import org.motechproject.tama.domain.PatientId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

@Repository
public class AllPatientIds extends AbstractCouchRepository<PatientId> {

    @Autowired
    public AllPatientIds(@Qualifier("tamaDbConnector") CouchDbConnector db) {
        super(PatientId.class, db);
        initStandardDesignDocument();
    }

    public void add(Patient patient) {
        this.add(new PatientId(patient.uniqueId()));
    }

    public void remove(Patient patient) {
        this.remove(get(patient.uniqueId()));
    }

    public PatientId get(Patient patient) {
        return this.get(patient.uniqueId());
    }
}
