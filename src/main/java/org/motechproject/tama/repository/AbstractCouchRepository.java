package org.motechproject.tama.repository;

import java.util.List;

import org.ektorp.CouchDbConnector;
import org.ektorp.support.CouchDbRepositorySupport;
import org.ektorp.support.GenerateView;

public abstract class AbstractCouchRepository<T> extends
		CouchDbRepositorySupport<T> {

	public AbstractCouchRepository(Class<T> clazz, CouchDbConnector db) {
		super(clazz, db);
		initStandardDesignDocument();
	}

	@GenerateView
	@Override
	public List<T> getAll() {
		return super.getAll();
	}
}