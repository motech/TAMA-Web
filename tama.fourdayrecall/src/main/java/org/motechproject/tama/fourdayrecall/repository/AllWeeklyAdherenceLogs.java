package org.motechproject.tama.fourdayrecall.repository;

import org.ektorp.ComplexKey;
import org.ektorp.CouchDbConnector;
import org.ektorp.ViewQuery;
import org.ektorp.support.View;
import org.joda.time.LocalDate;
import org.motechproject.tama.common.repository.AbstractCouchRepository;
import org.motechproject.tama.fourdayrecall.domain.WeeklyAdherenceLog;
import org.motechproject.tama.patient.domain.Patient;
import org.motechproject.tama.patient.domain.TreatmentAdvice;
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

    @View(name = "find_log_count_by_patient_id_and_treatment_advice_id_and_start_date", map = "function(doc) {if (doc.documentType =='WeeklyAdherenceLog') {emit([doc.patientId, doc.treatmentAdviceId, doc.weekStartDate], doc._id);}}")
    public WeeklyAdherenceLog findLogByWeekStartDate(String patientDocId, String treatmentAdviceId, LocalDate weekStartDate) {
        ComplexKey key = ComplexKey.of(patientDocId, treatmentAdviceId, weekStartDate);
        ViewQuery q = createQuery("find_log_count_by_patient_id_and_treatment_advice_id_and_start_date").key(key).includeDocs(true);
        return singleResult(db.queryView(q, WeeklyAdherenceLog.class));
    }

    @View(name = "find_by_patient_id", map = "function(doc) {if (doc.documentType =='WeeklyAdherenceLog' && doc.patientId) {emit(doc.patientId, doc._id);}}")
    public List<WeeklyAdherenceLog> findAllByPatientId(String patientDocId) {
        ViewQuery q = createQuery("find_by_patient_id").key(patientDocId).includeDocs(true);
        return  db.queryView(q, WeeklyAdherenceLog.class);
    }

    public WeeklyAdherenceLog findLogsByWeekStartDate(Patient patient, TreatmentAdvice treatmentAdvice, LocalDate weekStartDate) {
        return findLogByWeekStartDate(patient.getId(), treatmentAdvice.getId(), weekStartDate);
    }
}