package org.motechproject.tama.dailypillreminder.repository;

import org.apache.commons.collections.CollectionUtils;
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

    @View(name = "find_by_dosage_id", map = "function(doc) {if (doc.documentType =='DosageAdherenceLog' && doc.dosageId) {emit(doc.dosageId, doc._id);}}")
    public List<DosageAdherenceLog> findByDosageId(String dosageId) {
        ViewQuery q = createQuery("find_by_dosage_id").key(dosageId).includeDocs(true);
        return db.queryView(q, DosageAdherenceLog.class);
    }

    //TODO: Should be renamed to findByRegimenDosageStatusAndDosageDate. This should not call willCurrentDosageBeTakenLater instead should be called by service/command.
    @View(name = "find_success_log_count_for_a_given_date_range", map = "function(doc) {if (doc.documentType =='DosageAdherenceLog') {emit([doc.regimenId, doc.dosageStatus, doc.dosageDate], doc._id);}}", reduce = "_count")
    public int findScheduledDosagesSuccessCount(String regimenId, LocalDate fromDate, LocalDate toDate) {
        ComplexKey startDosageDatekey = ComplexKey.of(regimenId, DosageStatus.TAKEN, fromDate.plusDays(1)); //exclude start Day
        ComplexKey endDosageDatekey = ComplexKey.of(regimenId, DosageStatus.TAKEN, toDate);
        ViewQuery q = createQuery("find_success_log_count_for_a_given_date_range").startKey(startDosageDatekey).endKey(endDosageDatekey);
        ViewResult viewResult = db.queryView(q);
        int successLogCount = rowCount(viewResult);

        if (willCurrentDosageBeTakenLater(regimenId)) return successLogCount + 1;
        return successLogCount;
    }

    @View(name = "find_success_log_count", map = "function(doc) {if (doc.documentType =='DosageAdherenceLog') {emit([doc.regimenId, doc.dosageStatus], doc._id);}}", reduce = "_count")
    public int getDosageTakenCount(String regimenId) {
        ComplexKey key = ComplexKey.of(regimenId, DosageStatus.TAKEN);
        ViewQuery q = createQuery("find_success_log_count").key(key);
        ViewResult viewResult = db.queryView(q);
        return rowCount(viewResult);
    }

    //TODO: Should be renamed to findByRegimenDosageStatusAndDosageDate.
    @View(name = "find_whether_current_dosage_will_be_taken_later", map = "function(doc) {if (doc.documentType =='DosageAdherenceLog') {emit([doc.regimenId, doc.dosageStatus, doc.dosageDate], doc._id);}}", reduce = "_count")
    public boolean willCurrentDosageBeTakenLater(String regimenId) {
        ComplexKey key = ComplexKey.of(regimenId, DosageStatus.WILL_TAKE_LATER, DateUtil.today());
        ViewQuery q = createQuery("find_whether_current_dosage_will_be_taken_later").key(key);
        ViewResult viewResult = db.queryView(q);
        return rowCount(viewResult) == 1;
    }

    @View(name = "find_by_dosage_id_and_dosageDate", map = "function(doc) {if (doc.documentType =='DosageAdherenceLog' && doc.dosageId && doc.dosageDate) {emit([doc.dosageId, doc.dosageDate], doc._id);}}")
    public DosageAdherenceLog findByDosageIdAndDate(String dosageId, LocalDate dosageDate) {
        ViewQuery q = createQuery("find_by_dosage_id_and_dosageDate").key(ComplexKey.of(dosageId, dosageDate)).includeDocs(true);
        List<DosageAdherenceLog> adherenceLogs = db.queryView(q, DosageAdherenceLog.class);
        return singleResult(adherenceLogs);
    }

    @View(name = "find_by_regimen_and_dosage_status_and_date_range", map = "function(doc) {if (doc.documentType =='DosageAdherenceLog') {emit([doc.regimenId, doc.dosageStatus, doc.dosageDate], doc._id);}}")
    public int countBy(String regimenId, DosageStatus dosageStatus, LocalDate from, LocalDate till) {
        ViewQuery q = createQuery("find_by_regimen_and_dosage_status_and_date_range").startKey(ComplexKey.of(regimenId, dosageStatus, from)).endKey(ComplexKey.of(regimenId, dosageStatus, till)).inclusiveEnd(true).includeDocs(true);
        return db.queryView(q, DosageAdherenceLog.class).size();
    }

    @View(name = "ordered_by_date", map = "function(doc) {if (doc.documentType =='DosageAdherenceLog') {emit([doc.patientId, doc.dosageDate], doc._id);}}")
    public DosageAdherenceLog getLatestLogForPatient(String patientId) {
        ViewQuery q = createQuery("ordered_by_date").startKey(patientId).includeDocs(true);
        List<DosageAdherenceLog> logsSortedOnDateAscending = db.queryView(q, DosageAdherenceLog.class);
        return CollectionUtils.isEmpty(logsSortedOnDateAscending) ? null : logsSortedOnDateAscending.get(logsSortedOnDateAscending.size() - 1);
    }

    @View(name = "count_of_dose_taken_late_since", map = "function(doc) {if (doc.documentType =='DosageAdherenceLog') {emit([doc.patientId, doc.dosageTakenLate, doc.dosageDate], doc._id);}}", reduce = "_count")
    public int getDoseTakenLateCount(String patientId, LocalDate since, boolean doseTakenLate) {
        ViewQuery q = createQuery("count_of_dose_taken_late_since").startKey(ComplexKey.of(patientId, doseTakenLate, since)).endKey(ComplexKey.of(patientId, doseTakenLate, DateUtil.today()));
        ViewResult viewResult = db.queryView(q);
        return rowCount(viewResult);
    }
}