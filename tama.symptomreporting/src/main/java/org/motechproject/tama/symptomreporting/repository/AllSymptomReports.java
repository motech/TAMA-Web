package org.motechproject.tama.symptomreporting.repository;

import org.ektorp.ComplexKey;
import org.ektorp.CouchDbConnector;
import org.ektorp.ViewQuery;
import org.ektorp.support.GenerateView;
import org.ektorp.support.View;
import org.joda.time.LocalDate;
import org.motechproject.dao.MotechBaseRepository;
import org.motechproject.tama.symptomreporting.domain.SymptomReport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import java.util.List;

@Component
public class AllSymptomReports extends MotechBaseRepository<SymptomReport> {

    @Autowired
    protected AllSymptomReports(@Qualifier("tamaDbConnector") CouchDbConnector db) {
        super(SymptomReport.class, db);
    }

    public SymptomReport addOrReplace(SymptomReport report) {
        addOrReplace(report, "callId", report.getCallId());
        return report;
    }

    @GenerateView
    public SymptomReport findByCallId(String callId) {
        return singleResult(queryView("by_callId", callId));
    }

    @View(name = "find_symptom_report_by_date_range", map = "function(doc) {if (doc.documentType =='SymptomReport') {emit([doc.patientDocId, doc.reportedAt], doc._id);}}")
    public List<SymptomReport> getSymptomReports(String patientDocId, LocalDate from, LocalDate till) {
        ViewQuery q = createQuery("find_symptom_report_by_date_range").startKey(
                ComplexKey.of(patientDocId, from)).endKey(ComplexKey.of(patientDocId, till)).inclusiveEnd(true)
                .includeDocs(true);
        return db.queryView(q, SymptomReport.class);
    }
}
