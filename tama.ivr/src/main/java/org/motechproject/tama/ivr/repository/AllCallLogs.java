package org.motechproject.tama.ivr.repository;

import org.ektorp.ComplexKey;
import org.ektorp.CouchDbConnector;
import org.ektorp.ViewQuery;
import org.ektorp.support.View;
import org.motechproject.tama.common.repository.AbstractCouchRepository;
import org.motechproject.tama.ivr.domain.CallLog;
import org.motechproject.tama.ivr.domain.CallLogSearch;
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

    private String getStartPatientDocIdKey(CallLogSearch callLogSearch) {
        return callLogSearch.getPatientDocId() == null ? null : callLogSearch.getPatientDocId();
    }

    private Object getEndPatientDocIdKey(CallLogSearch callLogSearch) {
        return callLogSearch.getPatientDocId() == null ? ComplexKey.emptyObject() : callLogSearch.getPatientDocId();
    }

    public List<CallLog> findCallLogsForDateRangeAndClinic(CallLogSearch callLogSearch) {
        ComplexKey startDosageDateKey = ComplexKey.of(callLogSearch.getCallLogType().name(), callLogSearch.getClinicId(), getStartPatientDocIdKey(callLogSearch), callLogSearch.getFromDate());
        ComplexKey endDosageDateKey = ComplexKey.of(callLogSearch.getCallLogType().name(), callLogSearch.getClinicId(), getEndPatientDocIdKey(callLogSearch), callLogSearch.getToDate());
        ViewQuery q = createQuery("find_all_call_logs_between_a_given_date_range_and_by_clinicId")
                .startKey(startDosageDateKey).endKey(endDosageDateKey).includeDocs(true)
                .skip(callLogSearch.getStartIndex()).limit(callLogSearch.getLimit()).reduce(false);
        return db.queryView(q, CallLog.class);
    }

    public List<CallLog> findCallLogsForDateRange(CallLogSearch callLogSearch) {
        ComplexKey startDosageDateKey = ComplexKey.of(callLogSearch.getCallLogType().name(), getStartPatientDocIdKey(callLogSearch), callLogSearch.getFromDate());
        ComplexKey endDosageDateKey = ComplexKey.of(callLogSearch.getCallLogType().name(), getEndPatientDocIdKey(callLogSearch), callLogSearch.getToDate());
        ViewQuery q = createQuery("find_all_call_logs_between_a_given_date_range").startKey(startDosageDateKey).endKey(endDosageDateKey).includeDocs(true).skip(callLogSearch.getStartIndex()).limit(callLogSearch.getLimit()).reduce(false);
        return db.queryView(q, CallLog.class);
    }

    @View(name = "find_all_call_logs_between_a_given_date_range", map = "function(doc) { if(doc.documentType == 'CallLog') { var callType = 'Answered'; if(doc.callEvents) { for(var idx in doc.callEvents) { if (doc.callEvents[idx].name == 'Missed') { callType = 'Missed'; } } } emit([callType, doc.patientDocumentId, doc.startTime], doc._id); } }", reduce = "_count")
    public int findTotalNumberOfCallLogsForDateRange(CallLogSearch callLogSearch) {
        ComplexKey startDosageDateKey = ComplexKey.of(callLogSearch.getCallLogType().name(), getStartPatientDocIdKey(callLogSearch), callLogSearch.getFromDate());
        ComplexKey endDosageDateKey= ComplexKey.of(callLogSearch.getCallLogType().name(), getEndPatientDocIdKey(callLogSearch), callLogSearch.getToDate());
        ViewQuery q = createQuery("find_all_call_logs_between_a_given_date_range").startKey(startDosageDateKey).endKey(endDosageDateKey).reduce(true);
        return rowCount(db.queryView(q));
    }

    @View(name = "find_all_call_logs_between_a_given_date_range_and_by_clinicId", map = "function(doc) { if(doc.documentType == 'CallLog') { var callType = 'Answered'; if(doc.callEvents) { for(var idx in doc.callEvents) { if (doc.callEvents[idx].name == 'Missed') { callType = 'Missed'; } } } emit([callType, doc.clinicId, doc.patientDocumentId, doc.startTime], doc._id); } }", reduce = "_count")
    public int findTotalNumberOfCallLogsForDateRangeAndClinic(CallLogSearch callLogSearch) {
        ComplexKey startDosageDateKey = ComplexKey.of(callLogSearch.getCallLogType().name(), callLogSearch.getClinicId(), getStartPatientDocIdKey(callLogSearch), callLogSearch.getFromDate());
        ComplexKey endDosageDateKey = ComplexKey.of(callLogSearch.getCallLogType().name(), callLogSearch.getClinicId(), getEndPatientDocIdKey(callLogSearch), callLogSearch.getToDate());
        ViewQuery q = createQuery("find_all_call_logs_between_a_given_date_range_and_by_clinicId").startKey(startDosageDateKey).endKey(endDosageDateKey).reduce(true);
        return rowCount(db.queryView(q));
    }
}
