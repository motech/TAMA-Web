package org.motechproject.tama.common.repository;

import org.ektorp.CouchDbConnector;
import org.motechproject.tama.common.domain.AuditEvent;
import org.motechproject.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

@Repository
public class AllAuditEvents extends AbstractCouchRepository<AuditEvent> {

    @Autowired
    public AllAuditEvents(@Qualifier("auditDbConnector") CouchDbConnector db) {
        super(AuditEvent.class, db);
    }

    public String recordAppointmentEvent(String user, String description) {
        AuditEvent auditEvent = new AuditEvent(user, DateUtil.now(), AuditEvent.AuditEventType.Appointment, description);
        add(auditEvent);
        return auditEvent.getId();
    }

    public String recordAlertEvent(String user, String description) {
        AuditEvent auditEvent = new AuditEvent(user, DateUtil.now(), AuditEvent.AuditEventType.Alert, description);
        add(auditEvent);
        return auditEvent.getId();
    }

}