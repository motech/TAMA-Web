package org.motechproject.tama.common.repository;

import org.ektorp.CouchDbConnector;
import org.motechproject.tama.common.domain.CouchEntity;
import org.motechproject.tama.common.repository.AbstractCouchRepository;
import org.springframework.beans.factory.annotation.Autowired;

public class AuditableCouchRepository<T extends CouchEntity> extends AbstractCouchRepository<T> {

    private AllAuditRecords allAuditRecords;

    @Autowired
    public AuditableCouchRepository(Class<T> clazz, CouchDbConnector db, AllAuditRecords allAuditRecords) {
        super(clazz, db);
        this.allAuditRecords = allAuditRecords;
    }

    @Override
    public void add(T entity) {
        throw new UnsupportedOperationException("Use add(entity, user) method");
    }

    @Override
    public void update(T entity) {
        throw new UnsupportedOperationException("Use update(entity, user) method");
    }

    public void add(T entity, String user) {
        allAuditRecords.add(null, entity, user);
        super.add(entity);
    }

    public void update(T entity, String user) {
        CouchEntity objectFromDB = get(entity.getId());
        allAuditRecords.add(objectFromDB, entity, user);
        super.update(entity);
    }
}
