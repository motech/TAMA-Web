package org.motechproject.tama.dailypillreminder.repository;

import org.ektorp.ComplexKey;
import org.ektorp.CouchDbConnector;
import org.ektorp.ViewQuery;
import org.ektorp.ViewResult;
import org.ektorp.support.View;
import org.joda.time.LocalDate;
import org.motechproject.tama.common.repository.AbstractCouchRepository;
import org.motechproject.tama.dailypillreminder.domain.DosageAdherenceLog;
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

    @View(name = "find_success_log_count", map = "function(doc) {if (doc.documentType =='DosageAdherenceLog') {emit([doc.regimenId, doc.dosageStatus], doc._id);}}", reduce = "_count")
    public int getDosageTakenCount(String regimenId) {
        ComplexKey key = ComplexKey.of(regimenId, DosageStatus.TAKEN);
        ViewQuery q = createQuery("find_success_log_count").key(key);
        ViewResult viewResult = db.queryView(q);
        return rowCount(viewResult);
    }

    @View(name = "find_by_regimen_and_date_range", map = "function(doc) {if (doc.documentType =='DosageAdherenceLog') {emit([doc.regimenId, doc.dosageDate], doc._id);}}")
    public int countByDosageDate(String regimenId, LocalDate from, LocalDate till) {
        ViewQuery q = createQuery("find_by_regimen_and_date_range").startKey(ComplexKey.of(regimenId, from)).endKey(ComplexKey.of(regimenId, till)).inclusiveEnd(true).includeDocs(true);
        return db.queryView(q, DosageAdherenceLog.class).size();
    }

    @View(name = "find_by_regimen_id_dosage_status_and_dosage_date", map = "function(doc) {if (doc.documentType =='DosageAdherenceLog') {emit([doc.regimenId, doc.dosageStatus, doc.dosageDate], doc._id);}}", reduce = "_count")
    public int countByDosageStatusAndDate(String regimenId, DosageStatus dosageStatus, LocalDate fromDate, LocalDate toDate) {
        ComplexKey startDosageDatekey = ComplexKey.of(regimenId, dosageStatus, fromDate);
        ComplexKey endDosageDatekey = ComplexKey.of(regimenId, dosageStatus, toDate);
        ViewQuery q = createQuery("find_by_regimen_id_dosage_status_and_dosage_date").startKey(startDosageDatekey).endKey(endDosageDatekey);
        ViewResult viewResult = db.queryView(q);
        return rowCount(viewResult);
    }

    @View(name = "find_by_dosage_id_and_dosageDate", map = "function(doc) {if (doc.documentType =='DosageAdherenceLog' && doc.dosageId && doc.dosageDate) {emit([doc.dosageId, doc.dosageDate], doc._id);}}")
    public DosageAdherenceLog findByDosageIdAndDate(String dosageId, LocalDate dosageDate) {
        ViewQuery q = createQuery("find_by_dosage_id_and_dosageDate").key(ComplexKey.of(dosageId, dosageDate)).includeDocs(true);
        List<DosageAdherenceLog> adherenceLogs = db.queryView(q, DosageAdherenceLog.class);
        return singleResult(adherenceLogs);
    }

    @View(name = "count_of_dose_taken_late_since", map = "function(doc) {if (doc.documentType =='DosageAdherenceLog') {emit([doc.regimenId, doc.dosageTakenLate, doc.dosageDate], doc._id);}}", reduce = "_count")
    public int getDoseTakenLateCount(String regimenId, LocalDate since, boolean doseTakenLate) {
        ViewQuery q = createQuery("count_of_dose_taken_late_since").startKey(ComplexKey.of(regimenId, doseTakenLate, since)).endKey(ComplexKey.of(regimenId, doseTakenLate, DateUtil.today()));
        ViewResult viewResult = db.queryView(q);
        return rowCount(viewResult);
    }
}