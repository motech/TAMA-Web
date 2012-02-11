package org.motechproject.tama.dailypillreminder.repository;

import org.ektorp.ComplexKey;
import org.ektorp.CouchDbConnector;
import org.ektorp.ViewQuery;
import org.ektorp.ViewResult;
import org.ektorp.support.View;
import org.joda.time.LocalDate;
import org.motechproject.tama.common.domain.AdherenceSummaryForAWeek;
import org.motechproject.tama.common.repository.AbstractCouchRepository;
import org.motechproject.tama.dailypillreminder.domain.DosageAdherenceLog;
import org.motechproject.tama.dailypillreminder.domain.DosageAdherenceLogPerDay;
import org.motechproject.tama.dailypillreminder.domain.DosageStatus;
import org.motechproject.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class AllDosageAdherenceLogs extends AbstractCouchRepository<DosageAdherenceLog> {

    @Autowired
    public AllDosageAdherenceLogs(@Qualifier("tamaDbConnector") CouchDbConnector db) {
        super(DosageAdherenceLog.class, db);
    }

    @View(name = "count_by_regimen_id_dosage_status_and_dosage_date", map = "function(doc) {if (doc.documentType =='DosageAdherenceLog') {emit([doc.regimenId, doc.dosageStatus, doc.dosageDate], doc._id);}}", reduce = "_count")
    public int countByDosageStatusAndDate(String regimenId, DosageStatus dosageStatus, LocalDate fromDate, LocalDate toDate) {
        ComplexKey startDosageDatekey = ComplexKey.of(regimenId, dosageStatus, fromDate);
        ComplexKey endDosageDatekey = ComplexKey.of(regimenId, dosageStatus, toDate);
        ViewQuery q = createQuery("count_by_regimen_id_dosage_status_and_dosage_date").startKey(startDosageDatekey).endKey(endDosageDatekey);
        ViewResult viewResult = db.queryView(q);
        return rowCount(viewResult);
    }

    public int getDosageTakenCount(String regimenId) {
        ComplexKey startKey = ComplexKey.of(regimenId, DosageStatus.TAKEN);
        ComplexKey endKey = ComplexKey.of(regimenId, DosageStatus.TAKEN, ComplexKey.emptyObject());
        ViewQuery q = createQuery("count_by_regimen_id_dosage_status_and_dosage_date").startKey(startKey).endKey(endKey);
        ViewResult viewResult = db.queryView(q);
        return rowCount(viewResult);
    }

    @View(name = "count_by_regimen_id_and_dosage_date", map = "function(doc) {if (doc.documentType =='DosageAdherenceLog') {emit([doc.regimenId, doc.dosageDate], doc._id);}}", reduce = "_count")
    public int countByDosageDate(String regimenId, LocalDate from, LocalDate till) {
        final ComplexKey startKey = ComplexKey.of(regimenId, from);
        final ComplexKey endKey = ComplexKey.of(regimenId, till);
        ViewQuery q = createQuery("count_by_regimen_id_and_dosage_date").startKey(startKey).endKey(endKey).inclusiveEnd(true);
        ViewResult viewResult = db.queryView(q);
        return rowCount(viewResult);
    }

    @View(name = "count_by_regimen_id_dose_taken_late_and_dosage_date", map = "function(doc) {if (doc.documentType =='DosageAdherenceLog') {emit([doc.regimenId, doc.dosageTakenLate, doc.dosageDate], doc._id);}}", reduce = "_count")
    public int getDoseTakenLateCount(String regimenId, LocalDate since, boolean doseTakenLate) {
        final ComplexKey startKey = ComplexKey.of(regimenId, doseTakenLate, since);
        final ComplexKey endKey = ComplexKey.of(regimenId, doseTakenLate, DateUtil.today());
        ViewQuery q = createQuery("count_by_regimen_id_dose_taken_late_and_dosage_date").startKey(startKey).endKey(endKey);
        ViewResult viewResult = db.queryView(q);
        return rowCount(viewResult);
    }

    @View(name = "find_by_dosage_id_and_dosageDate", map = "function(doc) {if (doc.documentType =='DosageAdherenceLog' && doc.dosageId && doc.dosageDate) {emit([doc.dosageId, doc.dosageDate], doc._id);}}")
    public DosageAdherenceLog findByDosageIdAndDate(String dosageId, LocalDate dosageDate) {
        ViewQuery q = createQuery("find_by_dosage_id_and_dosageDate").key(ComplexKey.of(dosageId, dosageDate)).includeDocs(true);
        List<DosageAdherenceLog> adherenceLogs = db.queryView(q, DosageAdherenceLog.class);
        return singleResult(adherenceLogs);
    }
    @View(name="getPillsTakenAndTotalCountPerWeek", file = "doseTakenSummaryPerWeekMapReduce.json")
    public List<AdherenceSummaryForAWeek> getDoseTakenSummaryPerWeek(String patientDocId) {
        final ComplexKey startKey = ComplexKey.of(patientDocId);
        final ComplexKey endKey = ComplexKey.of(patientDocId, ComplexKey.emptyObject());
        ViewQuery q = createQuery("getPillsTakenAndTotalCountPerWeek").startKey(startKey).endKey(endKey).reduce(true).inclusiveEnd(true).group(true);
        return db.queryView(q, AdherenceSummaryForAWeek.class);
    }

    @View(name="find_all_logs_by_patient_id_per_day", map = "function(doc) {if (doc.documentType =='DosageAdherenceLog' && doc.patientId && doc.dosageDate) {emit([doc.patientId, doc.dosageDate], doc);}}", reduce = "function(key, values) { return {'date': values[0].dosageDate, 'logs': values.map(function(elt) { return {id: elt._id, dosageStatus: elt.dosageStatus, dosageId: elt.dosageId}})}}")
    public List<DosageAdherenceLogPerDay> getLogsPerDay(String patientDocId, LocalDate startDate, LocalDate endDate) {
        final ComplexKey startKey = ComplexKey.of(patientDocId, startDate);
        final ComplexKey endKey = ComplexKey.of(patientDocId, endDate);
        ViewQuery q = createQuery("find_all_logs_by_patient_id_per_day").startKey(startKey).endKey(endKey).reduce(true).inclusiveEnd(true).group(true);
        return db.queryView(q, DosageAdherenceLogPerDay.class);
    }
}
