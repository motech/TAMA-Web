package org.motechproject.tama.refdata.repository;

import org.ektorp.CouchDbConnector;
import org.motechproject.tama.common.repository.AbstractCouchRepository;
import org.motechproject.tama.refdata.domain.DosageType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

@Repository
public class AllDosageTypes extends AbstractCouchRepository<DosageType> {

    @Autowired
    protected AllDosageTypes(@Qualifier("tamaDbConnector") CouchDbConnector db) {
        super(DosageType.class, db);
    }
}
