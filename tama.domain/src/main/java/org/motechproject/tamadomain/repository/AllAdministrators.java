package org.motechproject.tamadomain.repository;

import org.ektorp.CouchDbConnector;
import org.ektorp.support.CouchDbRepositorySupport;
import org.ektorp.support.GenerateView;
import org.ektorp.support.View;
import org.jasypt.encryption.pbe.PBEStringEncryptor;
import org.motechproject.tamadomain.domain.Administrator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@View(name = "all", map = "function(doc) { if (doc.documentType == 'Administrator') { emit(null, doc) } }")
public class AllAdministrators extends CouchDbRepositorySupport<Administrator> {

    private PBEStringEncryptor encryptor;

    @Autowired
    public AllAdministrators(@Qualifier("tamaDbConnector") CouchDbConnector db, PBEStringEncryptor encryptor) {
        super(Administrator.class, db);
        initStandardDesignDocument();
        this.encryptor = encryptor;
    }

    @Override
    public void add(Administrator administrator) {
        administrator.setEncryptedPassword(encryptor.encrypt(administrator.getPassword()));
        super.add(administrator);
    }

    public void updatePassword(Administrator administrator) {
        administrator.setEncryptedPassword(encryptor.encrypt(administrator.getPassword()));
        super.update(administrator);
    }

    public Administrator findByUserNameAndPassword(String username, String password) {
        Administrator administrator = findByUsername(username);
        if (administrator != null && administrator.credentialsAre(password)) return administrator;
        return null;
    }

    @GenerateView
    public Administrator findByUsername(String username) {
        List<Administrator> administrators = queryView("by_username", username);
        if (administrators != null && !administrators.isEmpty()) {
            Administrator administrator = administrators.get(0);
            administrator.setPassword(encryptor.decrypt(administrator.getEncryptedPassword()));
            return administrator;
        }
        return null;
    }
}
