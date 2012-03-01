package org.motechproject.tama.outbox.repository;

import org.ektorp.CouchDbConnector;
import org.motechproject.dao.MotechBaseRepository;
import org.motechproject.tama.outbox.domain.OutboxMessageLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class AllOutboxEvents extends MotechBaseRepository<OutboxMessageLog> {

    @Autowired
    protected AllOutboxEvents(@Qualifier("tamaDbConnector") CouchDbConnector db) {
        super(OutboxMessageLog.class, db);
    }
}
