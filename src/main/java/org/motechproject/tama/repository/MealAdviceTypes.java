package org.motechproject.tama.repository;

import org.ektorp.CouchDbConnector;
import org.motechproject.tama.domain.MealAdviceType;

public class MealAdviceTypes extends AbstractCouchRepository<MealAdviceType>{

	public MealAdviceTypes(CouchDbConnector db) {
		super(MealAdviceType.class, db);
	}
}
