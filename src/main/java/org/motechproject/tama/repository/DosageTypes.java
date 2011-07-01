package org.motechproject.tama.repository;

import org.ektorp.CouchDbConnector;
import org.ektorp.support.CouchDbRepositorySupport;
import org.ektorp.support.View;
import org.motechproject.tama.domain.DosageType;

@View( name="all", map = "function(doc) { if (doc.documentType == 'DosageType') { emit(null, doc) } }")
public class DosageTypes extends CouchDbRepositorySupport<DosageType> {

	protected DosageTypes(CouchDbConnector db) {
		super(DosageType.class, db);
		initStandardDesignDocument();
	}
}
