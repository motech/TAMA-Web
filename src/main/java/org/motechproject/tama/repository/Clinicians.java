package org.motechproject.tama.repository;

import org.ektorp.CouchDbConnector;
import org.ektorp.ViewQuery;
import org.ektorp.support.GenerateView;
import org.ektorp.support.View;
import org.motechproject.tama.domain.Clinician;

import java.util.Arrays;
import java.util.List;

public class Clinicians extends AbstractCouchRepository<Clinician> {

    public Clinicians(CouchDbConnector db) {
        super(Clinician.class, db);
        initStandardDesignDocument();
    }

    @View(name = "findByUserNameAndPassword", map = "function(doc){if(doc.documentType =='Clinician'){emit(doc.username+':'+doc.password,doc._id);}}")
    public Clinician findByUserNameAndPassword(String username, String password) {
        ViewQuery query = createQuery("findByUserNameAndPassword").includeDocs(true);
        query.key(username + ":" + password);
        List<Clinician> clinicians = db.queryView(query, Clinician.class);
        if (clinicians != null && !clinicians.isEmpty()) return clinicians.get(0);
        return null;
    }

}
