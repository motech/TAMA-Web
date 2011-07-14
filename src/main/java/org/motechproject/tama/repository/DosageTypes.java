package org.motechproject.tama.repository;

import org.ektorp.CouchDbConnector;
import org.motechproject.tama.domain.DosageType;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class DosageTypes extends AbstractCouchRepository<DosageType> {
	protected DosageTypes(CouchDbConnector db) {
		super(DosageType.class, db); 
	}

    @Override
    public List<DosageType> getAll() {
        List<DosageType> all = super.getAll();
        Collections.sort(all, new Comparator<DosageType>() {
            @Override
            public int compare(DosageType dosageType1, DosageType dosageType2) {
                return dosageType1.getType().compareTo(dosageType2.getType());
            }
        });
        return all;
    }
}
