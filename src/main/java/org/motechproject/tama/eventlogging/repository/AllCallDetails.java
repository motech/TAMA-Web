package org.motechproject.tama.eventlogging.repository;

import org.ektorp.CouchDbConnector;
import org.ektorp.ViewQuery;
import org.ektorp.support.CouchDbRepositorySupport;
import org.ektorp.support.View;
import org.motechproject.tama.eventlogging.domain.CallLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@View(name = "all", map = "function(doc) { if (doc.documentType == 'CallDetail') { emit(null, doc) } }")
public class AllCallDetails extends CouchDbRepositorySupport<CallLog> {

    @Autowired
    protected AllCallDetails(@Qualifier("tamaDbConnector") CouchDbConnector db) {
        super(CallLog.class, db);
        initStandardDesignDocument();
    }

    @View(name = "find_by_call_id", map = "function(doc) {if (doc.documentType =='CallDetail' && doc.callId) {emit(doc.callId, doc._id);}}")
    public CallLog getByCallId(String callId) {
        ViewQuery byCallId = createQuery("find_by_call_id").key(callId).includeDocs(true);
        List<CallLog> callLogs = db.queryView(byCallId, CallLog.class);
        return singleResult(callLogs);
    }

    private CallLog singleResult(List<CallLog> callLogs) {
        return (callLogs == null || callLogs.isEmpty()) ? null : callLogs.get(0);
    }
}
