package org.motechproject.tama.repository;

import org.ektorp.CouchDbConnector;
import org.ektorp.support.CouchDbRepositorySupport;
import org.motechproject.tama.domain.TreatmentAdvice;

public class TreatmentAdvices extends CouchDbRepositorySupport<TreatmentAdvice> {

    public TreatmentAdvices(CouchDbConnector db) {
        super(TreatmentAdvice.class, db);
        initStandardDesignDocument();
    }
}
