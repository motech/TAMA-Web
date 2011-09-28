package org.motechproject.tama.ivr.logging.repository;

import org.ektorp.CouchDbConnector;
import org.ektorp.ViewQuery;
import org.ektorp.support.CouchDbRepositorySupport;
import org.ektorp.support.View;
import org.motechproject.tama.ivr.logging.domain.CallLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import java.util.List;

import static ch.lambdaj.Lambda.on;
import static ch.lambdaj.Lambda.sort;

@Repository
@View(name = "all", map = "function(doc) { if (doc.documentType == 'CallLog') { emit(null, doc) } }")
public class AllCallLogs extends CouchDbRepositorySupport<CallLog> {

    @Autowired
    protected AllCallLogs(@Qualifier("tamaDbConnector") CouchDbConnector db) {
        super(CallLog.class, db);
        initStandardDesignDocument();
    }

    @View(name = "find_by_call_id", map = "function(doc) {if (doc.documentType =='CallLog' && doc.callId) {emit(doc.callId, doc._id);}}")
    public CallLog getByCallId(String callId) {
        ViewQuery byCallId = createQuery("find_by_call_id").key(callId).includeDocs(true);
        List<CallLog> callLogs = db.queryView(byCallId, CallLog.class);
        return singleResult(callLogs);
    }

    private CallLog singleResult(List<CallLog> callLogs) {
        return (callLogs == null || callLogs.isEmpty()) ? null : callLogs.get(0);
    }

    @View(name = "find_open_call_log_by_patientDocumentId", map = "function(doc) {if (doc.documentType =='CallLog' && !(doc.callId)) {emit(doc.patientDocumentId, doc._id);}}")
    public CallLog getLatestOpenCallLog(String patientDocumentId) {
        ViewQuery viewQuery  = createQuery("find_open_call_log_by_patientDocumentId").key(patientDocumentId).includeDocs(true);
        List<CallLog> openCallLogs = db.queryView(viewQuery, CallLog.class);
        List<Object> orderedByStartTime = sort(openCallLogs, on(CallLog.class).getStartTime());
        return (orderedByStartTime == null || orderedByStartTime.isEmpty())? null : (CallLog) orderedByStartTime.get(orderedByStartTime.size() - 1);
    }
}
