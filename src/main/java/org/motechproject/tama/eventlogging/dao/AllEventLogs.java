package org.motechproject.tama.eventlogging.dao;

import org.ektorp.CouchDbConnector;
import org.ektorp.support.CouchDbRepositorySupport;
import org.ektorp.support.View;
import org.motechproject.dao.MotechBaseRepository;
import org.motechproject.tama.eventlogging.domain.EventLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

@Repository
@View(name = "all", map = "function(doc) { if (doc.documentType == 'EventLog') { emit(null, doc) } }")
public class AllEventLogs extends CouchDbRepositorySupport<EventLog> {
    @Autowired
    public AllEventLogs(@Qualifier("tamaDbConnector") CouchDbConnector db) {
        super(EventLog.class, db);
        initStandardDesignDocument();
    }
}