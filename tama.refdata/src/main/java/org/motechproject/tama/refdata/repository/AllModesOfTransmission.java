package org.motechproject.tama.refdata.repository;

import org.ektorp.CouchDbConnector;
import org.motechproject.tama.common.repository.AbstractCouchRepository;
import org.motechproject.tama.refdata.domain.ModeOfTransmission;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

@Repository
public class AllModesOfTransmission extends AbstractCouchRepository<ModeOfTransmission> {

    @Autowired
    public AllModesOfTransmission(@Qualifier("tamaDbConnector") CouchDbConnector db) {
        super(ModeOfTransmission.class, db);
        initStandardDesignDocument();
    }
}
