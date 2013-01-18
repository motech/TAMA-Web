package org.motechproject.tama.ivr.repository;

import org.apache.commons.lang.StringUtils;
import org.ektorp.ComplexKey;
import org.ektorp.CouchDbConnector;
import org.ektorp.ViewQuery;
import org.ektorp.support.View;
import org.joda.time.DateTime;
import org.motechproject.tama.common.repository.AbstractCouchRepository;
import org.motechproject.tama.ivr.domain.CallLog;
import org.motechproject.tama.ivr.domain.CallLogSearch;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import java.util.List;

@Component
public class AllCallLogs extends AbstractCouchRepository<CallLog> {

    @Autowired
    protected AllCallLogs(@Qualifier("tamaDbConnector") CouchDbConnector db) {
        super(CallLog.class, db);
        initStandardDesignDocument();
    }

    public List<CallLog> findCallLogsForDateRangeAndClinic(CallLogSearch callLogSearch) {
        ComplexKey startKey = ComplexKey.of(callLogSearch.getCallLogType().name(), callLogSearch.getClinicId(), callLogSearch.getFromDate());
        ComplexKey endKey = ComplexKey.of(callLogSearch.getCallLogType().name(), callLogSearch.getClinicId(), callLogSearch.getToDate());
        ViewQuery q = createQuery("find_by_callType_clinicId_and_date_range")
                .startKey(startKey).endKey(endKey).includeDocs(true)
                .skip(callLogSearch.getStartIndex()).limit(callLogSearch.getLimit()).reduce(false);
        return db.queryView(q, CallLog.class);
    }

    public List<CallLog> findCallLogsForDateRange(CallLogSearch callLogSearch) {
        ComplexKey startKey = ComplexKey.of(callLogSearch.getCallLogType().name(), callLogSearch.getFromDate());
        ComplexKey endKey = ComplexKey.of(callLogSearch.getCallLogType().name(), callLogSearch.getToDate());
        ViewQuery q = createQuery("find_by_callType_and_date_range").startKey(startKey).endKey(endKey).includeDocs(true).skip(callLogSearch.getStartIndex()).limit(callLogSearch.getLimit()).reduce(false);
        return db.queryView(q, CallLog.class);
    }

    @View(name = "find_by_date_range", map = "function copy(d, s) {" +
            "  for (i in s) {" +
            "    if (i === 'responseXML') continue;" +
            "    if (typeof s[i] == 'object') {" +
            "      d[i] = (typeof s[i].length == 'undefined'?{}:[]);" +
            "      copy(d[i], s[i]);" +
            "    }" +
            "    else d[i] = s[i];" +
            "  }" +
            "}" +
            "" +
            "function(doc) { " +
            " if(doc.documentType == 'CallLog') { " +
            "  var d= {};" +
            "  copy(d,doc);" +
            "  emit(doc.startTime, d); " +
            " }" +
            "}" )
    public List<CallLog> findAllCallLogsForDateRange(DateTime startDateTime, DateTime endDateTime, int pageSize, String startDocId) {
        ViewQuery q = createQuery("find_by_date_range").startKey(startDateTime).endKey(endDateTime).limit(pageSize);
        if (StringUtils.isNotEmpty(startDocId)) q.startDocId(startDocId);
        return db.queryView(q, CallLog.class);
    }

    @View(name = "find_by_callType_and_date_range", map = "function(doc) { if(doc.documentType == 'CallLog') { var callType = 'Answered'; if(doc.callEvents) { for(var idx in doc.callEvents) { if (doc.callEvents[idx].name == 'Missed') { callType = 'Missed'; } } } emit([callType, doc.startTime], doc._id); } }", reduce = "_count")
    public int findTotalNumberOfCallLogsForDateRange(CallLogSearch callLogSearch) {
        ComplexKey startKey = ComplexKey.of(callLogSearch.getCallLogType().name(), callLogSearch.getFromDate());
        ComplexKey endKey = ComplexKey.of(callLogSearch.getCallLogType().name(), callLogSearch.getToDate());
        ViewQuery q = createQuery("find_by_callType_and_date_range").startKey(startKey).endKey(endKey).reduce(true);
        return rowCount(db.queryView(q));
    }

    @View(name = "find_by_callType_clinicId_and_date_range", map = "function(doc) { if(doc.documentType == 'CallLog') { var callType = 'Answered'; if(doc.callEvents) { for(var idx in doc.callEvents) { if (doc.callEvents[idx].name == 'Missed') { callType = 'Missed'; } } } emit([callType, doc.clinicId, doc.startTime], doc._id); } }", reduce = "_count")
    public int findTotalNumberOfCallLogsForDateRangeAndClinic(CallLogSearch callLogSearch) {
        ComplexKey startKey = ComplexKey.of(callLogSearch.getCallLogType().name(), callLogSearch.getClinicId(), callLogSearch.getFromDate());
        ComplexKey endKey = ComplexKey.of(callLogSearch.getCallLogType().name(), callLogSearch.getClinicId(), callLogSearch.getToDate());
        ViewQuery q = createQuery("find_by_callType_clinicId_and_date_range").startKey(startKey).endKey(endKey).reduce(true);
        return rowCount(db.queryView(q));
    }

    public List<CallLog> findCallLogsForDateRangePatientIdAndClinic(CallLogSearch callLogSearch) {
        ComplexKey startKey = ComplexKey.of(callLogSearch.getCallLogType().name(), callLogSearch.getClinicId(), callLogSearch.getPatientId(), callLogSearch.getFromDate());
        ComplexKey endKey = ComplexKey.of(callLogSearch.getCallLogType().name(), callLogSearch.getClinicId(), callLogSearch.getPatientId(), callLogSearch.getToDate());
        ViewQuery q = createQuery("find_by_callType_clinicId_patientId_and_date_range")
                .startKey(startKey).endKey(endKey).includeDocs(true)
                .skip(callLogSearch.getStartIndex()).limit(callLogSearch.getLimit()).reduce(false);
        return db.queryView(q, CallLog.class);
    }

    public List<CallLog> findCallLogsForDateRangeAndPatientId(CallLogSearch callLogSearch) {
        ComplexKey startKey = ComplexKey.of(callLogSearch.getCallLogType().name(), callLogSearch.getPatientId(), callLogSearch.getFromDate());
        ComplexKey endKey = ComplexKey.of(callLogSearch.getCallLogType().name(), callLogSearch.getPatientId(), callLogSearch.getToDate());
        ViewQuery q = createQuery("find_by_callType_patientId_and_date_range").startKey(startKey).endKey(endKey).includeDocs(true).skip(callLogSearch.getStartIndex()).limit(callLogSearch.getLimit()).reduce(false);
        return db.queryView(q, CallLog.class);
    }

    @View(name = "find_by_callType_patientId_and_date_range", map = "function(doc) { if(doc.documentType == 'CallLog') { var callType = 'Answered'; if(doc.callEvents) { for(var idx in doc.callEvents) { if (doc.callEvents[idx].name == 'Missed') { callType = 'Missed'; } } } emit([callType, doc.patientId.toLowerCase(), doc.startTime], doc._id); } }", reduce = "_count")
    public int findTotalNumberOfCallLogsForDateRangeAndPatientId(CallLogSearch callLogSearch) {
        ComplexKey startKey = ComplexKey.of(callLogSearch.getCallLogType().name(), callLogSearch.getPatientId(), callLogSearch.getFromDate());
        ComplexKey endKey = ComplexKey.of(callLogSearch.getCallLogType().name(), callLogSearch.getPatientId(), callLogSearch.getToDate());
        ViewQuery q = createQuery("find_by_callType_patientId_and_date_range").startKey(startKey).endKey(endKey).reduce(true);
        return rowCount(db.queryView(q));
    }

    @View(name = "find_by_callType_clinicId_patientId_and_date_range", map = "function(doc) { if(doc.documentType == 'CallLog') { var callType = 'Answered'; if(doc.callEvents) { for(var idx in doc.callEvents) { if (doc.callEvents[idx].name == 'Missed') { callType = 'Missed'; } } } emit([callType, doc.clinicId, doc.patientId.toLowerCase(), doc.startTime], doc._id); } }", reduce = "_count")
    public int findTotalNumberOfCallLogsForDateRangePatientIdAndClinic(CallLogSearch callLogSearch) {
        ComplexKey startKey = ComplexKey.of(callLogSearch.getCallLogType().name(), callLogSearch.getClinicId(), callLogSearch.getPatientId(), callLogSearch.getFromDate());
        ComplexKey endKey = ComplexKey.of(callLogSearch.getCallLogType().name(), callLogSearch.getClinicId(), callLogSearch.getPatientId(), callLogSearch.getToDate());
        ViewQuery q = createQuery("find_by_callType_clinicId_patientId_and_date_range").startKey(startKey).endKey(endKey).reduce(true);
        return rowCount(db.queryView(q));
    }
}
