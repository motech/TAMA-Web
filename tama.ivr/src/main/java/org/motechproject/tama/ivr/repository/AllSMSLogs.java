package org.motechproject.tama.ivr.repository;

import org.ektorp.CouchDbConnector;
import org.ektorp.ViewQuery;
import org.ektorp.support.View;
import org.joda.time.DateTime;
import org.motechproject.tama.common.repository.AbstractCouchRepository;
import org.motechproject.tama.ivr.domain.SMSLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class AllSMSLogs extends AbstractCouchRepository<SMSLog> {

    @Autowired
    public AllSMSLogs(@Qualifier("tamaDbConnector") CouchDbConnector db) {
        super(SMSLog.class, db);
        initStandardDesignDocument();
    }

    public void log(String recipient, String message) {
        this.add(new SMSLog(recipient, message));
    }

    @View(name = "find_by_date_range", map = "function(doc) { if(doc.documentType == 'SMSLog') { emit(doc.sentDateTime, doc._id); } }")
    public List<SMSLog> findAllSMSLogsForDateRange(DateTime startDateTime, DateTime endDateTime) {
        ViewQuery q = createQuery("find_by_date_range").startKey(startDateTime).endKey(endDateTime).includeDocs(true);
        return db.queryView(q, SMSLog.class);
    }
}
