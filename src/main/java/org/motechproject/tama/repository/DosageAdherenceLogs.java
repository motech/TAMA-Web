package org.motechproject.tama.repository;

import org.ektorp.ComplexKey;
import org.ektorp.CouchDbConnector;
import org.ektorp.ViewQuery;
import org.ektorp.ViewResult;
import org.ektorp.support.View;
import org.motechproject.tama.domain.DosageAdherenceLog;
import org.motechproject.tama.domain.DosageStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Repository
public class DosageAdherenceLogs extends AbstractCouchRepository<DosageAdherenceLog> {

    @Autowired
    public DosageAdherenceLogs(@Qualifier("tamaDbConnector") CouchDbConnector db) {
        super(DosageAdherenceLog.class, db);
    }

    @View(name = "find_by_dosage_id", map = "function(doc) {if (doc.documentType =='DosageAdherenceLog' && doc.dosageId) {emit(doc.dosageId, doc._id);}}")
    public List<DosageAdherenceLog> findByDosageId(String dosageId) {
        ViewQuery q = createQuery("find_by_dosage_id").key(dosageId).includeDocs(true);
        List<DosageAdherenceLog> adherenceLogs = db.queryView(q, DosageAdherenceLog.class);
        return adherenceLogs;
    }

    @View(name = "find_logs_for_a_given_date_range", map = "function(doc) {if (doc.documentType =='DosageAdherenceLog' && doc.dosageId) {emit([doc.dosageId, doc.dosageDate], doc._id);}}")
    public int sample(String dosageId, Date fromDate, Date toDate) {
        ComplexKey startkey = ComplexKey.of(dosageId, fromDate);
        ComplexKey endkey = ComplexKey.of(dosageId, toDate);
        ViewQuery q = createQuery("find_logs_for_a_given_date_range").startKey(startkey).endKey(endkey).includeDocs(true);
        List<DosageAdherenceLog> adherenceLogs = db.queryView(q, DosageAdherenceLog.class);
        return adherenceLogs.size();
    }

    @View(name = "find_by_dosage_id_count", map = "function(doc) {if (doc.documentType =='DosageAdherenceLog' && doc.dosageId) {emit([doc.dosageId, doc.dosageStatus], doc._id);}}", reduce = "_count")
    public int sample1(String dosageId) {
        ComplexKey startkey = ComplexKey.of(dosageId, DosageStatus.TAKEN);
        ViewQuery q = createQuery("find_by_dosage_id_count").key(startkey);
        ViewResult viewResult = db.queryView(q);
        return viewResult.getRows().get(0).getValueAsInt();
    }

    public int findNumberOfScheduledDosages(String dosageId, Date fromDate, Date toDate) {
        List<DosageAdherenceLog> dosageIdLogs = findByDosageId(dosageId);
        List<DosageAdherenceLog> dosageAdherenceLogs = new ArrayList<DosageAdherenceLog>();

        for (DosageAdherenceLog log : dosageIdLogs) {
            if ((log.getDosageDate().compareTo(fromDate) >= 0) && (log.getDosageDate().compareTo(toDate) <= 0)) {
                dosageAdherenceLogs.add(log);
            }
        }

        return dosageAdherenceLogs.size();
    }
}