package org.motechproject.tama.repository;

import org.ektorp.CouchDbConnector;
import org.motechproject.tama.domain.Drug;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class Drugs extends AbstractCouchRepository<Drug>{

	protected Drugs(CouchDbConnector dbConnector) {
		super(Drug.class, dbConnector);
	}

    public List<Drug> getDrugs(Set<String> drugIds) {
        List<Drug> drugs = new ArrayList<Drug>();
        for (String drugId : drugIds) {
            drugs.add(get(drugId));
        }
        return drugs;
    }
}