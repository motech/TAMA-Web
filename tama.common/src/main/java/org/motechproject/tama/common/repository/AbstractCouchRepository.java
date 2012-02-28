package org.motechproject.tama.common.repository;

import org.ektorp.CouchDbConnector;
import org.ektorp.ViewResult;
import org.motechproject.dao.MotechBaseRepository;
import org.motechproject.model.MotechBaseDataObject;

import java.util.List;

public abstract class AbstractCouchRepository<T extends MotechBaseDataObject> extends MotechBaseRepository<T> {

    public AbstractCouchRepository(Class<T> clazz, CouchDbConnector db) {
        super(clazz, db);
    }

    protected int rowCount(ViewResult viewResult) {
        if (viewResult.getRows().size() == 0) return 0;
        return viewResult.getRows().get(0).getValueAsInt();
    }

    protected T lastResult(List<T> resultSet) {
        return (resultSet == null || resultSet.isEmpty()) ? null : resultSet.get(resultSet.size() - 1);
    }
}