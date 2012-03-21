package org.motechproject.tama.patient.repository;

import org.ektorp.CouchDbConnector;
import org.motechproject.tama.common.domain.CouchEntity;
import org.motechproject.tama.common.repository.AbstractCouchRepository;
import org.motechproject.tama.patient.domain.AuditRecord;
import org.motechproject.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

@Repository
public class AllAuditRecords extends AbstractCouchRepository<AuditRecord> {

    @Autowired
    public AllAuditRecords(@Qualifier("auditDbConnector") CouchDbConnector db) {
        super(AuditRecord.class, db);
    }

    public void add(CouchEntity before, CouchEntity after, String user) {
        add(new AuditRecord(DateUtil.now(), user, before, after));
    }

}