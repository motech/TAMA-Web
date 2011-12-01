package org.motechproject.tamadomain.repository;

import org.ektorp.CouchDbConnector;
import org.ektorp.support.CouchDbRepositorySupport;
import org.ektorp.support.View;
import org.motechproject.tamadomain.domain.Drug;
import org.motechproject.tamadomain.domain.Regimen;
import org.motechproject.tamadomain.domain.DrugComposition;
import org.motechproject.tamadomain.domain.DrugCompositionGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
@View( name="all", map = "function(doc) { if (doc.documentType == 'Regimen') { emit(null, doc) } }")
public class AllRegimens extends CouchDbRepositorySupport<Regimen> {

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
        for(Regimen regimen: all) {
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
