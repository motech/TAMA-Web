package org.motechproject.tama.repository;

import org.ektorp.CouchDbConnector;
import org.ektorp.support.CouchDbRepositorySupport;
import org.ektorp.support.View;
import org.motechproject.tama.domain.Regimen;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@View( name="all", map = "function(doc) { if (doc.documentType == 'Regimen') { emit(null, doc) } }")
public class Regimens extends CouchDbRepositorySupport<Regimen> {

    public Regimens(CouchDbConnector db) {
        super(Regimen.class, db);
        initStandardDesignDocument();
    }

    @Override
    public List<Regimen> getAll() {
        List<Regimen> all = super.getAll();
        Collections.sort(all, new Comparator<Regimen>() {
            @Override
            public int compare(Regimen regimen1, Regimen regimen2) {
                return regimen1.getRegimenDisplayName().compareTo(regimen2.getRegimenDisplayName());
            }
        });
        return all;
    }
}
