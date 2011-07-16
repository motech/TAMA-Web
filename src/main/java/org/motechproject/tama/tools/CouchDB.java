package org.motechproject.tama.tools;

import org.ektorp.CouchDbConnector;
import org.ektorp.CouchDbInstance;
import org.springframework.beans.factory.annotation.Autowired;

public class CouchDB {
    @Autowired
    private CouchDbInstance couchDbInstance;
    @Autowired
    private CouchDbConnector couchDbConnector;

    public void recreate() {
        String dbName = couchDbConnector.getDatabaseName();
        couchDbInstance.deleteDatabase(dbName);
        couchDbInstance.createDatabase(dbName);
    }
}

