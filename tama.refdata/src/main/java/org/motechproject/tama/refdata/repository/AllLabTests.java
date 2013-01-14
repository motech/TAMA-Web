package org.motechproject.tama.refdata.repository;

import org.ektorp.CouchDbConnector;
import org.motechproject.tama.common.repository.AbstractCouchRepository;
import org.motechproject.tama.refdata.domain.LabTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

@Component
public class AllLabTests extends AbstractCouchRepository<LabTest> {

    @Autowired
    public AllLabTests(@Qualifier("tamaDbConnector") CouchDbConnector db) {
        super(LabTest.class, db);
        initStandardDesignDocument();
    }
}
