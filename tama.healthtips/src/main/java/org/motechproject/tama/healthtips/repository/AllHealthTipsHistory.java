package org.motechproject.tama.healthtips.repository;

import org.ektorp.ComplexKey;
import org.ektorp.CouchDbConnector;
import org.ektorp.support.View;
import org.motechproject.tama.common.repository.AbstractCouchRepository;
import org.motechproject.tama.healthtips.domain.HealthTipsHistory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import java.util.List;

@Component
public class AllHealthTipsHistory extends AbstractCouchRepository<HealthTipsHistory> {

    @Autowired
    public AllHealthTipsHistory(@Qualifier("tamaDbConnector") CouchDbConnector db) {
        super(HealthTipsHistory.class, db);
        initStandardDesignDocument();
    }

    @View(name = "findByPatientId", map = "function(doc) { if (doc.documentType == 'HealthTipsHistory') { emit(doc.patientDocumentId, doc) } }")
    public List<HealthTipsHistory> findByPatientId(String patientDocumentId) {
        return queryView("findByPatientId", patientDocumentId);
    }

    @View(name = "findByPatientIdAndAudioFilename", map = "function(doc) { if (doc.documentType == 'HealthTipsHistory') { emit([doc.patientDocumentId, doc.audioFilename], doc) } }")
    public HealthTipsHistory findByPatientIdAndAudioFilename(String patientDocumentId, String audioFilename) {
        return singleResult(queryView("findByPatientIdAndAudioFilename", ComplexKey.of(patientDocumentId, audioFilename)));
    }
}
