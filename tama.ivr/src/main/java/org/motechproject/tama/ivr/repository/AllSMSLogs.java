package org.motechproject.tama.ivr.repository;

import org.ektorp.CouchDbConnector;
import org.motechproject.dao.MotechBaseRepository;
import org.motechproject.tama.common.repository.AbstractCouchRepository;
import org.motechproject.tama.ivr.domain.SMSLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

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
}
