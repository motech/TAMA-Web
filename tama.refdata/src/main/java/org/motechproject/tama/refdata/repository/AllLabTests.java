package org.motechproject.tama.refdata.repository;

import org.ektorp.CouchDbConnector;
import org.ektorp.ViewQuery;
import org.ektorp.support.GenerateView;
import org.ektorp.support.View;
import org.motechproject.tama.common.TAMAConstants;
import org.motechproject.tama.common.repository.AbstractCouchRepository;
import org.motechproject.tama.refdata.domain.LabTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

@Repository
public class AllLabTests extends AbstractCouchRepository<LabTest> {

    @Autowired
    public AllLabTests(@Qualifier("tamaDbConnector") CouchDbConnector db) {
        super(LabTest.class, db);
        initStandardDesignDocument();
    }

    @GenerateView
    public LabTest findByName(TAMAConstants.LabTestType testType) {
        ViewQuery by_name = createQuery("by_name").key(testType.getName()).includeDocs(true);
        return singleResult(db.queryView(by_name, LabTest.class));
    }
}
