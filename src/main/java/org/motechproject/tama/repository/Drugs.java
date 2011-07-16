package org.motechproject.tama.repository;

import org.ektorp.CouchDbConnector;
import org.motechproject.tama.domain.Drug;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Repository
public class Drugs extends AbstractCouchRepository<Drug>{

    @Autowired
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