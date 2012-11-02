package org.motechproject.tama.refdata.repository;

import org.ektorp.CouchDbConnector;
import org.ektorp.support.GenerateView;
import org.jasypt.encryption.pbe.PBEStringEncryptor;
import org.motechproject.tama.refdata.domain.Administrator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class AllAdministrators extends AllBasicTAMAUsers<Administrator> {

    @Autowired
    public AllAdministrators(@Qualifier("tamaDbConnector") CouchDbConnector db, PBEStringEncryptor encryptor) {
        super(Administrator.class, db, encryptor);
    }

    @GenerateView
    public Administrator findByUsername(String username) {
        List<Administrator> administrators = queryView("by_username", username);
        Administrator administrator = singleResult(administrators);
        return withDecryptedPassword(administrator);
    }
}
