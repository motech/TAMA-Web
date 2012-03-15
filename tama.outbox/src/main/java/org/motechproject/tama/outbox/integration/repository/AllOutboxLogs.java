package org.motechproject.tama.outbox.integration.repository;

import org.ektorp.ComplexKey;
import org.ektorp.CouchDbConnector;
import org.ektorp.ViewQuery;
import org.ektorp.support.View;
import org.joda.time.DateTime;
import org.motechproject.dao.MotechBaseRepository;
import org.motechproject.tama.outbox.domain.OutboxMessageLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class AllOutboxLogs extends MotechBaseRepository<OutboxMessageLog> {

    @Autowired
    protected AllOutboxLogs(@Qualifier("tamaDbConnector") CouchDbConnector db) {
        super(OutboxMessageLog.class, db);
    }

    @View(name = "byPatientIdInDateRange", map = "function(doc) {if (doc.documentType =='OutboxMessageLog') {emit([doc.patientDocId, doc.createdOn], doc._id);}}")
    public List<OutboxMessageLog> list(String patientDocId, DateTime start, DateTime end) {
        final ViewQuery byPatientIdInDateRange = createQuery("byPatientIdInDateRange")
                .startKey(ComplexKey.of(patientDocId, start))
                .endKey(ComplexKey.of(patientDocId, end))
                .includeDocs(true)
                .inclusiveEnd(true);

        return db.queryView(byPatientIdInDateRange, OutboxMessageLog.class);
    }

    @View(name = "findByPatientAndMessage", map = "function(doc) {if (doc.documentType =='OutboxMessageLog') {emit([doc.patientDocId, doc.outboxMessageId], doc._id);}}")
    public OutboxMessageLog find(String patientDocId, String messageId) {
        final ViewQuery byPatientAndMessage = createQuery("findByPatientAndMessage")
                .key(ComplexKey.of(patientDocId, messageId))
                .includeDocs(true);

        return singleResult(db.queryView(byPatientAndMessage, OutboxMessageLog.class));
    }
}
