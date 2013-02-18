package org.motechproject.tama.migration.repository;

import org.ektorp.CouchDbConnector;
import org.ektorp.ViewQuery;
import org.motechproject.tama.ivr.domain.CallLog;
import org.motechproject.tama.ivr.repository.AllCallLogs;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class PagedCallLogsRepository extends AllCallLogs implements Paged<CallLog> {

    @Autowired
    public PagedCallLogsRepository(@Qualifier("tamaDbConnector") CouchDbConnector db) {
        super(db);
    }

    @Override
    public List<CallLog> get(int skip, int limit) {
        ViewQuery query = createQuery("all").includeDocs(true).skip(skip).limit(limit);
        List<CallLog> callLogs = db.queryView(query, CallLog.class);
        return callLogs;
    }
}

