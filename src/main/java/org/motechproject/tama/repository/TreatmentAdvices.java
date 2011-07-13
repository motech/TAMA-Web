package org.motechproject.tama.repository;

import org.ektorp.CouchDbConnector;
import org.ektorp.ViewQuery;
import org.ektorp.support.CouchDbRepositorySupport;
import org.ektorp.support.View;
import org.motechproject.tama.domain.TreatmentAdvice;

import java.util.List;

@View( name="all", map = "function(doc) { if (doc.documentType == 'TreatmentAdvice') { emit(null, doc) } }")
public class TreatmentAdvices extends CouchDbRepositorySupport<TreatmentAdvice> {

    public TreatmentAdvices(CouchDbConnector db) {
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
