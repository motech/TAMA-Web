package org.motechproject.tama.refdata.repository;

import org.ektorp.CouchDbConnector;
import org.ektorp.support.GenerateView;
import org.jasypt.encryption.pbe.PBEStringEncryptor;
import org.motechproject.tama.common.repository.AbstractCouchRepository;
import org.motechproject.tama.refdata.domain.Administrator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class AllAdministrators extends AbstractCouchRepository<Administrator> {

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
        Administrator administrator = singleResult(administrators);
        if (administrator != null) {
            administrator.setPassword(encryptor.decrypt(administrator.getEncryptedPassword()));
        }
        return administrator;
    }
}
