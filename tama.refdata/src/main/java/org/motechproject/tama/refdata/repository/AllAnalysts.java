package org.motechproject.tama.refdata.repository;

import org.ektorp.CouchDbConnector;
import org.ektorp.support.GenerateView;
import org.jasypt.encryption.pbe.PBEStringEncryptor;
import org.motechproject.tama.refdata.domain.Analyst;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import java.util.List;

@Component
public class AllAnalysts extends AllBasicTAMAUsers<Analyst> {

    @Autowired
    public AllAnalysts(@Qualifier("tamaDbConnector") CouchDbConnector db, PBEStringEncryptor encryptor) {
        super(Analyst.class, db, encryptor);
    }

    @GenerateView
    public Analyst findByUsername(String username) {
        List<Analyst> analysts = queryView("by_username", username);
        Analyst analyst = singleResult(analysts);
        return withDecryptedPassword(analyst);
    }
}
