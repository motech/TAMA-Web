package org.motechproject.tama.security.repository;

import org.ektorp.CouchDbConnector;
import org.motechproject.tama.common.repository.AbstractCouchRepository;
import org.motechproject.tama.security.domain.AccessEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

@Repository
public class AllAccessEvents extends AbstractCouchRepository<AccessEvent> {

    @Autowired
    public AllAccessEvents(@Qualifier("tamaDbConnector") CouchDbConnector db) {
        super(AccessEvent.class, db);
    }

    public void newLoginEvent(String userName, String sourceAddress, String sessionId, String status) {
        add(new AccessEvent(userName, sourceAddress, sessionId, AccessEvent.AccessEventType.Login, status));
    }

    public void newLogoutEvent(String userName, String sourceAddress, String sessionId) {
        add(new AccessEvent(userName, sourceAddress, sessionId, AccessEvent.AccessEventType.Logout, null));
    }
}