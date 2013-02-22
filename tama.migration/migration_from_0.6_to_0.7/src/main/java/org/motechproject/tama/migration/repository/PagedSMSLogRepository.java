package org.motechproject.tama.migration.repository;


import org.ektorp.CouchDbConnector;
import org.ektorp.ViewQuery;
import org.motechproject.tama.ivr.domain.SMSLog;
import org.motechproject.tama.ivr.repository.AllSMSLogs;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class PagedSMSLogRepository extends AllSMSLogs implements Paged<SMSLog> {

    @Autowired
    public PagedSMSLogRepository(@Qualifier("tamaDbConnector") CouchDbConnector db) {
        super(db);
    }

    @Override
    public List<SMSLog> get(int skip, int limit) {
        ViewQuery query = createQuery("all").includeDocs(true).skip(skip).limit(limit);
        return db.queryView(query, SMSLog.class);
    }
}
