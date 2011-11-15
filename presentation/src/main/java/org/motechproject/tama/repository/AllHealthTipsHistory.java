package org.motechproject.tama.repository;

import org.ektorp.CouchDbConnector;
import org.ektorp.support.CouchDbRepositorySupport;
import org.ektorp.support.View;
import org.motechproject.tama.domain.HealthTipsHistory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@View( name="all", map = "function(doc) { if (doc.documentType == 'HealthTipsHistory') { emit(null, doc) } }")
public class AllHealthTipsHistory extends CouchDbRepositorySupport<HealthTipsHistory> {

    @Autowired
    public AllHealthTipsHistory(@Qualifier("tamaDbConnector") CouchDbConnector db) {
        super(HealthTipsHistory.class, db);
        initStandardDesignDocument();
    }

    @View(name="findByPatientId", map = "function(doc) { if (doc.documentType == 'HealthTipsHistory') { emit(doc.patientDocumentId, doc) } }")
    public List<HealthTipsHistory> findByPatientId(String patientDocumentId) {
        return queryView("findByPatientId", patientDocumentId);
    }
}
