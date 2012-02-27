package org.motechproject.tama.ivr.repository;

import org.ektorp.ComplexKey;
import org.ektorp.CouchDbConnector;
import org.ektorp.ViewQuery;
import org.ektorp.support.View;
import org.joda.time.DateTime;
import org.motechproject.tama.common.repository.AbstractCouchRepository;
import org.motechproject.tama.ivr.domain.CallLog;
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

    public List<CallLog> findCallLogsForDateRangeAndClinic(DateTime fromDate, DateTime toDate, String clinicId, Integer startIndex, Integer limit) {
        ComplexKey startDosageDateKey = ComplexKey.of(clinicId, fromDate);
        ComplexKey endDosageDateKey = ComplexKey.of(clinicId, toDate);
        ViewQuery q = createQuery("find_all_call_logs_between_a_given_date_range_and_by_clinicId")
                .startKey(startDosageDateKey).endKey(endDosageDateKey).includeDocs(true)
                .skip(startIndex).limit(limit).reduce(false);
        return db.queryView(q, CallLog.class);
    }

    public List<CallLog> findCallLogsForDateRange(DateTime fromDate, DateTime toDate, Integer startIndex, Integer limit) {
        ViewQuery q = createQuery("find_all_call_logs_between_a_given_date_range").startKey(fromDate).endKey(toDate).includeDocs(true).skip(startIndex).limit(limit).reduce(false);
        return db.queryView(q, CallLog.class);
    }

    @View(name = "find_all_call_logs_between_a_given_date_range", map = "function(doc) {if (doc.documentType =='CallLog') {emit(doc.startTime, doc._id);}}", reduce = "_count")
    public int findTotalNumberOfCallLogsForDateRange(DateTime fromDate, DateTime toDate) {
        ViewQuery q = createQuery("find_all_call_logs_between_a_given_date_range").startKey(fromDate).endKey(toDate).reduce(true);
        return rowCount(db.queryView(q));
    }

    @View(name = "find_all_call_logs_between_a_given_date_range_and_by_clinicId", map = "function(doc) {if (doc.documentType =='CallLog') {emit([doc.clinicId, doc.startTime], doc._id);}}", reduce = "_count")
    public int findTotalNumberOfCallLogsForDateRangeAndClinic(DateTime fromDate, DateTime toDate, String clinicId) {
        ComplexKey startDosageDateKey = ComplexKey.of(clinicId, fromDate);
        ComplexKey endDosageDateKey = ComplexKey.of(clinicId, toDate);
        ViewQuery q = createQuery("find_all_call_logs_between_a_given_date_range_and_by_clinicId").startKey(startDosageDateKey).endKey(endDosageDateKey).reduce(true);
        return rowCount(db.queryView(q));
    }

    /*  ----- START : Missed Call Logs */
    public List<CallLog> findMissedCallLogsForDateRangeAndClinic(DateTime fromDate, DateTime toDate, String clinicId, Integer startIndex, Integer limit) {
        ComplexKey startDosageDateKey = ComplexKey.of(CallLog.CallLogType.Missed.name(), clinicId, fromDate);
        ComplexKey endDosageDateKey = ComplexKey.of(CallLog.CallLogType.Missed.name(), clinicId, toDate);
        ViewQuery q = createQuery("find_all_missed_call_logs_between_a_given_date_range_and_by_clinicId")
                .startKey(startDosageDateKey).endKey(endDosageDateKey).includeDocs(true)
                .skip(startIndex).limit(limit).reduce(false);
        return db.queryView(q, CallLog.class);
    }

    public List<CallLog> findMissedCallLogsForDateRange(DateTime fromDate, DateTime toDate, Integer startIndex, Integer limit) {
        ComplexKey startDosageDateKey = ComplexKey.of(CallLog.CallLogType.Missed.name(), fromDate);
        ComplexKey endDosageDateKey = ComplexKey.of(CallLog.CallLogType.Missed.name(), toDate);
        ViewQuery q = createQuery("find_all_missed_call_logs_between_a_given_date_range").startKey(startDosageDateKey).endKey(endDosageDateKey).includeDocs(true).skip(startIndex).limit(limit).reduce(false);
        return db.queryView(q, CallLog.class);
    }

    @View(name = "find_all_missed_call_logs_between_a_given_date_range", map = "function(doc) { if(doc.documentType == 'CallLog') { var callType = 'Answered'; if(doc.callEvents) { for(var idx in doc.callEvents) { if (doc.callEvents[idx].name == 'Missed') { callType = 'Missed'; } } } emit([callType, doc.startTime], doc._id); } }", reduce = "_count")
    public int findTotalNumberOfMissedCallLogsForDateRange(DateTime fromDate, DateTime toDate) {
        ComplexKey startDosageDateKey = ComplexKey.of(CallLog.CallLogType.Missed.name(), fromDate);
        ComplexKey endDosageDateKey = ComplexKey.of(CallLog.CallLogType.Missed.name(), toDate);
        ViewQuery q = createQuery("find_all_missed_call_logs_between_a_given_date_range").startKey(startDosageDateKey).endKey(endDosageDateKey).reduce(true);
        return rowCount(db.queryView(q));
    }

    @View(name = "find_all_missed_call_logs_between_a_given_date_range_and_by_clinicId", map = "function(doc) { if(doc.documentType == 'CallLog') { var callType = 'Answered'; if(doc.callEvents) { for(var idx in doc.callEvents) { if (doc.callEvents[idx].name == 'Missed') { callType = 'Missed'; } } } emit([callType, doc.clinicId, doc.startTime], doc._id); } }", reduce = "_count")
    public int findTotalNumberOfMissedCallLogsForDateRangeAndClinic(DateTime fromDate, DateTime toDate, String clinicId) {
        ComplexKey startDosageDateKey = ComplexKey.of(CallLog.CallLogType.Missed.name(), clinicId, fromDate);
        ComplexKey endDosageDateKey = ComplexKey.of(CallLog.CallLogType.Missed.name(), clinicId, toDate);
        ViewQuery q = createQuery("find_all_missed_call_logs_between_a_given_date_range_and_by_clinicId").startKey(startDosageDateKey).endKey(endDosageDateKey).reduce(true);
        return rowCount(db.queryView(q));
    }
    /*  ----- END : Missed Call Logs */
}
