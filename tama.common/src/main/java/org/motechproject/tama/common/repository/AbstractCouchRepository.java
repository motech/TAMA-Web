package org.motechproject.tama.common.repository;

import org.ektorp.CouchDbConnector;
import org.ektorp.ViewResult;
import org.ektorp.support.CouchDbRepositorySupport;
import org.ektorp.support.GenerateView;

import java.util.List;

public abstract class AbstractCouchRepository<T> extends CouchDbRepositorySupport<T> {

    public AbstractCouchRepository(Class<T> clazz, CouchDbConnector db) {
        super(clazz, db);
        initStandardDesignDocument();
    }

    @GenerateView
    @Override
    public List<T> getAll() {
        return super.getAll();
    }

    protected int rowCount(ViewResult viewResult) {
        if (viewResult.getRows().size() == 0) return 0;
        return viewResult.getRows().get(0).getValueAsInt();
    }

    protected T singleResult(List<T> resultSet) {
        return (resultSet == null || resultSet.isEmpty()) ? null : resultSet.get(0);
    }
}