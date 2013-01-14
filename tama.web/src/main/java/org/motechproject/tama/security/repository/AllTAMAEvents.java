package org.motechproject.tama.security.repository;

import org.ektorp.CouchDbConnector;
import org.motechproject.tama.common.repository.AbstractCouchRepository;
import org.motechproject.tama.security.domain.AccessEvent;
import org.motechproject.tama.security.domain.ChangePasswordEvent;
import org.motechproject.tama.security.domain.TAMAEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

@Component
public class AllTAMAEvents extends AbstractCouchRepository<TAMAEvent> {

    @Autowired
    public AllTAMAEvents(@Qualifier("tamaDbConnector") CouchDbConnector db) {
        super(TAMAEvent.class, db);
    }

    public String newChangePasswordEvent(String clinicianName, String clinicName, String clinicId, String username) {
        ChangePasswordEvent changePasswordEvent = new ChangePasswordEvent(clinicianName, clinicName, clinicId, username);
        add(changePasswordEvent);
        return changePasswordEvent.getId();
    }

    public String newLoginEvent(String userName, String sourceAddress, String sessionId, String status) {
        AccessEvent accessEvent = new AccessEvent(userName, sourceAddress, sessionId, AccessEvent.AccessEventType.Login, status);
        add(accessEvent);
        return accessEvent.getId();
    }

    public String newLogoutEvent(String userName, String sourceAddress, String sessionId) {
        AccessEvent accessEvent = new AccessEvent(userName, sourceAddress, sessionId, AccessEvent.AccessEventType.Logout, null);
        add(accessEvent);
        return accessEvent.getId();
    }
}