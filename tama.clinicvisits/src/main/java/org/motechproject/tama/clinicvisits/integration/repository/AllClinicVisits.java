package org.motechproject.tama.clinicvisits.integration.repository;

import org.ektorp.CouchDbConnector;
import org.ektorp.ViewQuery;
import org.ektorp.support.View;
import org.motechproject.tama.clinicvisits.domain.ClinicVisit;
import org.motechproject.tama.common.repository.AbstractCouchRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class AllClinicVisits extends AbstractCouchRepository<ClinicVisit> {

    @Autowired
    public AllClinicVisits(@Qualifier("tamaDbConnector") CouchDbConnector db) {
        super(ClinicVisit.class, db);
        initStandardDesignDocument();
    }

    @View(name = "find_by_patient_id", map = "function(doc) {if (doc.documentType =='ClinicVisit' && doc.patientId) {emit(doc.patientId, doc._id);}}")
    public List<ClinicVisit> find_by_patient_id(String patientId) {
        ViewQuery q = createQuery("find_by_patient_id").key(patientId).includeDocs(true);
        return db.queryView(q, ClinicVisit.class);
    }

}
