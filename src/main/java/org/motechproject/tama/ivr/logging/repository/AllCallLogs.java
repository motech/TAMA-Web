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

@Repository
@View(name = "all", map = "function(doc) { if (doc.documentType == 'CallLog') { emit(null, doc) } }")
public class AllCallLogs extends CouchDbRepositorySupport<CallLog> {

    @Autowired
    protected AllCallLogs(@Qualifier("tamaDbConnector") CouchDbConnector db) {
        super(CallLog.class, db);
        initStandardDesignDocument();
    }

    @View(name = "find_by_clinic_id", map = "function(doc) {if (doc.documentType =='CallLog' && doc.clinicId) {emit(doc.clinicId, doc._id);}}")
    public CallLog findByClinic(String clinicId) {
        ViewQuery byClinicId = createQuery("find_by_clinic_id").key(clinicId).includeDocs(true);
        List<CallLog> callLogs = db.queryView(byClinicId, CallLog.class);
        return singleResult(callLogs);
    }

    private CallLog singleResult(List<CallLog> callLogs) {
        return (callLogs == null || callLogs.isEmpty()) ? null : callLogs.get(0);
    }
}
