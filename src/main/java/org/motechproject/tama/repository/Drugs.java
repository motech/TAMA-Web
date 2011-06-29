package org.motechproject.tama.repository;

import org.ektorp.CouchDbConnector;
import org.ektorp.support.CouchDbRepositorySupport;
import org.ektorp.support.View;
import org.motechproject.tama.domain.Drug;

@View( name="all", map = "function(doc) { if (doc.documentType == 'Drug') { emit(null, doc) } }")
public class Drugs extends CouchDbRepositorySupport<Drug>{

	public Drugs(CouchDbConnector dbConnector) {
		super(Drug.class, dbConnector);
		initStandardDesignDocument();
	}
}
