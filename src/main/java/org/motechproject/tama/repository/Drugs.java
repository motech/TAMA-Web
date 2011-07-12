package org.motechproject.tama.repository;

import org.ektorp.CouchDbConnector;
import org.motechproject.tama.domain.Drug;

public class Drugs extends AbstractCouchRepository<Drug>{

	public Drugs(CouchDbConnector dbConnector) {
		super(Drug.class, dbConnector);
	}
}