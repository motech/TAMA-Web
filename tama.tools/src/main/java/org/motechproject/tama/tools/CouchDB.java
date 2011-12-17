package org.motechproject.tama.tools;

import org.ektorp.CouchDbConnector;
import org.ektorp.CouchDbInstance;
import org.springframework.beans.factory.annotation.Autowired;

public class CouchDB {
    @Autowired
    private CouchDbInstance couchDbInstance;
    @Autowired
    private CouchDbConnector tamaDbConnector;

    public void recreate() {
        String dbName = tamaDbConnector.getDatabaseName();
        couchDbInstance.deleteDatabase(dbName);
        couchDbInstance.createDatabase(dbName);
    }
}

