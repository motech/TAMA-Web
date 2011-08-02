package org.motechproject.tama.repository;

import org.ektorp.CouchDbConnector;
import org.ektorp.support.CouchDbRepositorySupport;

public abstract class AbstractCouchRepository<T> extends CouchDbRepositorySupport<T> {

	public AbstractCouchRepository(Class<T> clazz, CouchDbConnector db) {
		super(clazz, db);
		initStandardDesignDocument();
	}
}