package org.motechproject.tamacallflow.repository;

import org.ektorp.ComplexKey;
import org.ektorp.CouchDbConnector;
import org.ektorp.ViewQuery;
import org.ektorp.support.View;
import org.joda.time.DateTime;
import org.motechproject.tama.common.repository.AbstractCouchRepository;
import org.motechproject.tamacallflow.domain.CallLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class AllCallLogs extends AbstractCouchRepository<CallLog> {

    @Autowired
    protected AllCallLogs(@Qualifier("tamaDbConnector") CouchDbConnector db) {
        super(CallLog.class, db);
        initStandardDesignDocument();
    }

    @View(name = "find_all_call_logs_between_a_given_date_range_and_by_clinicId", map = "function(doc) {if (doc.documentType =='CallLog') {emit([doc.clinicId, doc.startTime], doc._id);}}")
    public List<CallLog> findByClinic(DateTime fromDate, DateTime toDate, String clinicId) {
        ComplexKey startDosageDatekey = ComplexKey.of(clinicId, fromDate);
        ComplexKey endDosageDatekey = ComplexKey.of(clinicId, toDate);
        ViewQuery q = createQuery("find_all_call_logs_between_a_given_date_range_and_by_clinicId").startKey(startDosageDatekey).endKey(endDosageDatekey).includeDocs(true);
        return db.queryView(q, CallLog.class);
    }

    @View(name = "find_all_call_logs_between_a_given_date_range", map = "function(doc) {if (doc.documentType =='CallLog') {emit(doc.startTime, doc._id);}}")
    public List<CallLog> findCallLogsBetweenGivenDates(DateTime fromDate, DateTime toDate) {
        ViewQuery q = createQuery("find_all_call_logs_between_a_given_date_range").startKey(fromDate).endKey(toDate).includeDocs(true);
        return db.queryView(q, CallLog.class);
    }
}
