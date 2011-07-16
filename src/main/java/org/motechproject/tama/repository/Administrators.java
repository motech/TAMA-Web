package org.motechproject.tama.repository;

import org.ektorp.CouchDbConnector;
import org.ektorp.support.CouchDbRepositorySupport;
import org.ektorp.support.GenerateView;
import org.ektorp.support.View;
import org.motechproject.tama.domain.Administrator;
import org.motechproject.tama.domain.Clinician;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@View(name = "all", map = "function(doc) { if (doc.documentType == 'Administrator') { emit(null, doc) } }")
public class Administrators extends CouchDbRepositorySupport<Administrator> {

    @Autowired
    public Administrators(CouchDbConnector db) {
        super(Administrator.class, db);
        initStandardDesignDocument();
    }

     public Administrator findByUserNameAndPassword(String username, String password) {
        Administrator administrator = findByUsername(username);
        if (administrator != null && administrator.credentialsAre(password)) return administrator;
        return null;
    }

    @GenerateView
    public Administrator findByUsername(String username) {
        List<Administrator> administrators = queryView("by_username", username);
        if (administrators != null && !administrators.isEmpty()) return administrators.get(0);
        return null;
    }
}
