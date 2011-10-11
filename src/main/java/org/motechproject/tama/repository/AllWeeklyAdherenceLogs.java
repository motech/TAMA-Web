package org.motechproject.tama.repository;

import org.ektorp.ComplexKey;
import org.ektorp.CouchDbConnector;
import org.ektorp.ViewQuery;
import org.ektorp.ViewResult;
import org.ektorp.support.View;
import org.joda.time.LocalDate;
import org.motechproject.tama.domain.WeeklyAdherenceLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class AllWeeklyAdherenceLogs extends AbstractCouchRepository<WeeklyAdherenceLog> {

    @Autowired
    public AllWeeklyAdherenceLogs(@Qualifier("tamaDbConnector") CouchDbConnector db) {
        super(WeeklyAdherenceLog.class, db);
    }

    @View(name = "find_log_count_by_patient_id_and_treatment_advice_id_and_date_range", map = "function(doc) {if (doc.documentType =='WeeklyAdherenceLog') {emit([doc.patientId, doc.treatmentAdviceId, doc.logDate], doc._id);}}", reduce="_count")
    public int findLogCountByPatientIDAndTreatmentAdviceIdAndDateRange(String patientDocId, String treatmentAdviceId, LocalDate fromDate, LocalDate toDate) {
        ComplexKey startKey = ComplexKey.of(patientDocId, treatmentAdviceId, fromDate);
        ComplexKey endKey = ComplexKey.of(patientDocId, treatmentAdviceId, toDate);
        ViewQuery q = createQuery("find_log_count_by_patient_id_and_treatment_advice_id_and_date_range").startKey(startKey).endKey(endKey);
        ViewResult viewResult = db.queryView(q);
        return rowCount(viewResult);
    }

    @View(name = "find_by_date_range", map = "function(doc) {if (doc.documentType =='WeeklyAdherenceLog') {emit([doc.patientId, doc.treatmentAdviceId, doc.logDate], doc._id);}}")
    public List<WeeklyAdherenceLog> findByDateRange(String patientDocId, String treatmentAdviceId, LocalDate fromDate, LocalDate toDate) {
        ComplexKey startKey = ComplexKey.of(patientDocId, treatmentAdviceId, fromDate);
        ComplexKey endKey = ComplexKey.of(patientDocId, treatmentAdviceId, toDate);
        ViewQuery q = createQuery("find_by_date_range").startKey(startKey).endKey(endKey).includeDocs(true);
        return db.queryView(q, WeeklyAdherenceLog.class);
    }
}