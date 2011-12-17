package org.motechproject.tama.facility.repository;

import org.ektorp.CouchDbConnector;
import org.motechproject.tama.common.repository.AbstractCouchRepository;
import org.motechproject.tama.facility.domain.Clinician;
import org.motechproject.tama.facility.domain.ClinicianId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

@Repository
public class AllClinicianIds extends AbstractCouchRepository<ClinicianId> {

    @Autowired
    public AllClinicianIds(@Qualifier("tamaDbConnector") CouchDbConnector db) {
        super(ClinicianId.class, db);
        initStandardDesignDocument();
    }

    public void add(Clinician clinician) {
        this.add(new ClinicianId(clinician.getUsername()));
    }

    public void remove(Clinician clinician) {
        this.remove(get(clinician.getUsername()));
    }

    public ClinicianId get(Clinician clinician) {
        return this.get(clinician.getUsername());
    }

    public void update(Clinician clinician) {
        this.get(clinician).setId(clinician.getUsername());
    }
}
