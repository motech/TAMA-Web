package org.motechproject.tama.repository;

import org.ektorp.CouchDbConnector;
import org.ektorp.ViewQuery;
import org.ektorp.support.CouchDbRepositorySupport;
import org.ektorp.support.View;
import org.motechproject.tama.domain.TreatmentAdvice;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@View( name="all", map = "function(doc) { if (doc.documentType == 'TreatmentAdvice') { emit(null, doc) } }")
public class TreatmentAdvices extends CouchDbRepositorySupport<TreatmentAdvice> {

    @Autowired
    public TreatmentAdvices(@Qualifier("tamaDbConnector") CouchDbConnector db) {
        super(TreatmentAdvice.class, db);
        initStandardDesignDocument();
    }

    @View(name = "find_by_patient_id", map = "function(doc) {if (doc.documentType =='TreatmentAdvice' && doc.patientId) {emit(doc.patientId, doc._id);}}")
    public TreatmentAdvice findByPatientId(String patientId) {
        ViewQuery q = createQuery("find_by_patient_id").key(patientId).includeDocs(true);
        List<TreatmentAdvice> treatmentAdvices = db.queryView(q, TreatmentAdvice.class);
        return treatmentAdvices.isEmpty() ? null : treatmentAdvices.get(0);
    }
}
