package org.motechproject.tama.repository;

import org.ektorp.CouchDbConnector;
import org.motechproject.tama.domain.DosageType;

public class DosageTypes extends AbstractCouchRepository<DosageType> {
	protected DosageTypes(CouchDbConnector db) {
		super(DosageType.class, db); 
	}
}
