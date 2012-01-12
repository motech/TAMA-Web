package org.motechproject.tama.symptomreporting.repository;

import org.ektorp.ComplexKey;
import org.ektorp.CouchDbConnector;
import org.ektorp.ViewQuery;
import org.ektorp.support.View;
import org.joda.time.LocalDate;
import org.motechproject.tama.common.repository.AbstractCouchRepository;
import org.motechproject.tama.symptomreporting.domain.SymptomReport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class AllSymptomReports extends AbstractCouchRepository<SymptomReport> {

    @Autowired
    protected AllSymptomReports(@Qualifier("tamaDbConnector") CouchDbConnector db) {
        super(SymptomReport.class, db);
    }

    public SymptomReport insertOrMerge(SymptomReport report) {
        SymptomReport oldReport = findByCallId(report.getCallId());
        if (oldReport == null) {
            add(report);
            return report;
        } else {
            update(oldReport.merge(report));
            return oldReport;
        }
    }

    @View(name = "find_by_callId", map = "function(doc) {if (doc.documentType =='SymptomReport' && doc.callId) {emit(doc.callId, doc._id);}}")
    public SymptomReport findByCallId(String callId) {
        ViewQuery q = createQuery("find_by_callId").key(callId).includeDocs(true);
        return singleResult(db.queryView(q, SymptomReport.class));
    }

    @View(name = "find_symptom_report_by_date_range", map = "function(doc) {if (doc.documentType =='SymptomReport') {emit([doc.patientDocId, doc.reportedAt], doc._id);}}")
    public List<SymptomReport> getSymptomReports(String patientDocId, LocalDate from, LocalDate till) {
        ViewQuery q = createQuery("find_symptom_report_by_date_range").startKey(
                ComplexKey.of(patientDocId, from)).endKey(ComplexKey.of(patientDocId, till)).inclusiveEnd(true)
                .includeDocs(true);
        return db.queryView(q, SymptomReport.class);
    }
}
