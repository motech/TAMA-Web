package org.motechproject.tama.repository;

import org.ektorp.CouchDbConnector;
import org.ektorp.support.CouchDbRepositorySupport;
import org.ektorp.support.View;
import org.motechproject.tama.domain.TreatmentAdvice;

@View( name="all", map = "function(doc) { if (doc.documentType == 'TreatmentAdvice') { emit(null, doc) } }")
public class TreatmentAdvices extends CouchDbRepositorySupport<TreatmentAdvice> {

    public TreatmentAdvices(CouchDbConnector db) {
        super(TreatmentAdvice.class, db);
        initStandardDesignDocument();
    }
}
