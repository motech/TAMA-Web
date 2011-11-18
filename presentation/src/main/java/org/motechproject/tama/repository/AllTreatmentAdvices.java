package org.motechproject.tama.repository;

import org.apache.commons.lang.StringUtils;
import org.ektorp.CouchDbConnector;
import org.ektorp.ViewQuery;
import org.ektorp.support.CouchDbRepositorySupport;
import org.ektorp.support.View;
import org.motechproject.tama.domain.TreatmentAdvice;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.List;

@Repository
@View( name="all", map = "function(doc) { if (doc.documentType == 'TreatmentAdvice') { emit(null, doc) } }")
public class AllTreatmentAdvices extends CouchDbRepositorySupport<TreatmentAdvice> {

    @Autowired
    public AllTreatmentAdvices(@Qualifier("tamaDbConnector") CouchDbConnector db) {
        super(TreatmentAdvice.class, db);
        initStandardDesignDocument();
    }

    //TODO: This should be renamed to findByPatientIdAndReasonForDiscontinuing. A class should not assume "how" its method would be used and should just try to do "what"?
    public TreatmentAdvice currentTreatmentAdvice(String patientId) {
        List<TreatmentAdvice> treatmentAdvices = find_by_patient_id(patientId);
        for (TreatmentAdvice treatmentAdvice : treatmentAdvices) {
            if (StringUtils.isEmpty(treatmentAdvice.getReasonForDiscontinuing())) {
                return treatmentAdvice;
            }
        }
        return null;
    }

    public TreatmentAdvice earliestTreatmentAdvice(String patientId) {
        List<TreatmentAdvice> treatmentAdvices = find_by_patient_id(patientId);
        Collections.sort(treatmentAdvices);
        return (treatmentAdvices.isEmpty()) ? null : treatmentAdvices.get(0);
    }

    @View(name = "find_by_patient_id", map = "function(doc) {if (doc.documentType =='TreatmentAdvice' && doc.patientId) {emit(doc.patientId, doc._id);}}")
    private List<TreatmentAdvice> find_by_patient_id(String patientId) {
        ViewQuery q = createQuery("find_by_patient_id").key(patientId).includeDocs(true);
        return db.queryView(q, TreatmentAdvice.class);
    }
}
