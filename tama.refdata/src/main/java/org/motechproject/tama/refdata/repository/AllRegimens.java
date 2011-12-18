package org.motechproject.tama.refdata.repository;

import org.ektorp.CouchDbConnector;
import org.motechproject.tama.common.repository.AbstractCouchRepository;
import org.motechproject.tama.refdata.domain.Drug;
import org.motechproject.tama.refdata.domain.DrugComposition;
import org.motechproject.tama.refdata.domain.DrugCompositionGroup;
import org.motechproject.tama.refdata.domain.Regimen;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class AllRegimens extends AbstractCouchRepository<Regimen> {

    private AllDrugs allDrugs;

    @Autowired
    public AllRegimens(@Qualifier("tamaDbConnector") CouchDbConnector db, AllDrugs allDrugs) {
        super(Regimen.class, db);
        this.allDrugs = allDrugs;
        initStandardDesignDocument();
    }

    @Override
    public List<Regimen> getAll() {
        List<Regimen> all = super.getAll();
        Collections.sort(all, new Comparator<Regimen>() {
            @Override
            public int compare(Regimen regimen1, Regimen regimen2) {
                return regimen1.getDisplayName().compareTo(regimen2.getDisplayName());
            }
        });
        for (Regimen regimen : all) {
            loadDependencies(regimen);
        }
        return all;

    }

    private void loadDependencies(Regimen regimen) {
        for (DrugCompositionGroup group : regimen.getDrugCompositionGroups()) {
            for (DrugComposition drugComposition : group.getDrugCompositions()) {
                Set<Drug> drugSet = new HashSet<Drug>();
                for (String drugId : drugComposition.getDrugIds()) {
                    drugSet.add(allDrugs.get(drugId));
                }
                drugComposition.setDrugs(drugSet);
            }
        }
    }
}
