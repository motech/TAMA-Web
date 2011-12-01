package org.motechproject.tamadomain.repository;

import org.ektorp.CouchDbConnector;
import org.ektorp.support.CouchDbRepositorySupport;
import org.ektorp.support.View;
import org.motechproject.tamadomain.domain.PatientAlert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

@Repository
@View(name = "all", map = "function(doc) { if (doc.documentType == 'PatientAlert') { emit(null, doc) } }")
public class AllSymptomReportingAlerts extends CouchDbRepositorySupport<PatientAlert> {

    @Autowired
    public AllSymptomReportingAlerts(@Qualifier("tamaDbConnector") CouchDbConnector db) {
        super(PatientAlert.class, db);
    }
}
