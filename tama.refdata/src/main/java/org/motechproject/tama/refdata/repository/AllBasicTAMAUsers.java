package org.motechproject.tama.refdata.repository;

import org.ektorp.CouchDbConnector;
import org.jasypt.encryption.pbe.PBEStringEncryptor;
import org.motechproject.tama.common.domain.BasicTAMAUser;
import org.motechproject.tama.common.repository.AbstractCouchRepository;

public abstract class AllBasicTAMAUsers<USER extends BasicTAMAUser> extends AbstractCouchRepository<USER> {

    protected PBEStringEncryptor encryptor;

    public AllBasicTAMAUsers(Class<USER> clazz, CouchDbConnector db) {
        super(clazz, db);
    }

    public AllBasicTAMAUsers(Class<USER> clazz, CouchDbConnector db, PBEStringEncryptor encryptor) {
        this(clazz, db);
        initStandardDesignDocument();
        this.encryptor = encryptor;
    }

    public abstract USER findByUsername(String username);

    @Override
    public void add(USER user) {
        user.setEncryptedPassword(encryptor.encrypt(user.getPassword()));
        super.add(user);
    }

    public void updatePassword(USER user) {
        user.setEncryptedPassword(encryptor.encrypt(user.getPassword()));
        super.update(user);
    }

    public USER findByUserNameAndPassword(String username, String password) {
        USER user = findByUsername(username);
        if (user != null && user.credentialsAre(password)) return user;
        return null;
    }

    protected USER withDecryptedPassword(USER user) {
        if (user != null) {
            user.setPassword(encryptor.decrypt(user.getEncryptedPassword()));
        }
        return user;
    }
}
