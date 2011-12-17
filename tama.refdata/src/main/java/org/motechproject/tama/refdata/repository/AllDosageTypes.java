package org.motechproject.tama.refdata.repository;

import org.ektorp.CouchDbConnector;
import org.motechproject.tama.common.repository.AbstractCouchRepository;
import org.motechproject.tama.refdata.domain.DosageType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@Repository
public class AllDosageTypes extends AbstractCouchRepository<DosageType> {

    @Autowired
    protected AllDosageTypes(@Qualifier("tamaDbConnector") CouchDbConnector db) {
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
