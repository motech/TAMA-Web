package org.motechproject.tama.repository;

import org.ektorp.CouchDbConnector;
import org.ektorp.support.CouchDbRepositorySupport;
import org.ektorp.support.View;
import org.motechproject.tama.domain.Drug;
import org.motechproject.tama.domain.DrugComposition;
import org.motechproject.tama.domain.DrugCompositionGroup;
import org.motechproject.tama.domain.Regimen;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
@View( name="all", map = "function(doc) { if (doc.documentType == 'Regimen') { emit(null, doc) } }")
public class Regimens extends CouchDbRepositorySupport<Regimen> {

    private Drugs drugs;

    @Autowired
    public Regimens(@Qualifier("tamaDbConnector") CouchDbConnector db, Drugs drugs) {
        super(Regimen.class, db);
        this.drugs = drugs;
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
                    drugSet.add(drugs.get(drugId));
                }
                drugComposition.setDrugs(drugSet);
            }
        }

    }
}
