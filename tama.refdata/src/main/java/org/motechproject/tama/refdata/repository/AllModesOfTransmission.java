package org.motechproject.tama.refdata.repository;

import org.ektorp.CouchDbConnector;
import org.ektorp.support.CouchDbRepositorySupport;
import org.ektorp.support.View;
import org.motechproject.tama.refdata.domain.ModeOfTransmission;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

@Repository
@View(name = "all", map = "function(doc) { if (doc.documentType == 'ModeOfTransmission') { emit(null, doc) } }")
public class AllModesOfTransmission extends CouchDbRepositorySupport<ModeOfTransmission> {

    @Autowired
    public AllModesOfTransmission(@Qualifier("tamaDbConnector") CouchDbConnector db) {
        super(ModeOfTransmission.class, db);
        initStandardDesignDocument();
    }
}
