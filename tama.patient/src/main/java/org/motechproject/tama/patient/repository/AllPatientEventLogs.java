package org.motechproject.tama.patient.repository;

import org.ektorp.CouchDbConnector;
import org.ektorp.ViewQuery;
import org.ektorp.support.View;
import org.motechproject.tama.common.repository.AbstractCouchRepository;
import org.motechproject.tama.patient.domain.PatientEventLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import java.util.List;

@Component
public class AllPatientEventLogs extends AbstractCouchRepository<PatientEventLog> {

    @Autowired
    public AllPatientEventLogs(@Qualifier("tamaDbConnector") CouchDbConnector db) {
        super(PatientEventLog.class, db);
        initStandardDesignDocument();
    }

    @View(name = "find_by_patient_id", map = "function(doc) {if (doc.documentType =='PatientEventLog' && doc.patientId) {emit(doc.patientId, doc._id);}}")
    public List<PatientEventLog> findByPatientId(String patientId) {
        ViewQuery q = createQuery("find_by_patient_id").key(patientId).includeDocs(true);
        return  db.queryView(q, PatientEventLog.class);
    }

}