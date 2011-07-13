package org.motechproject.tama.repository;

import org.ektorp.CouchDbConnector;
import org.ektorp.support.GenerateView;
import org.motechproject.tama.domain.Clinician;

import java.util.List;

public class Clinicians extends AbstractCouchRepository<Clinician> {
    public Clinicians(CouchDbConnector db) {
        super(Clinician.class, db);
        initStandardDesignDocument();
    }

    public Clinician findByUserNameAndPassword(String username, String password) {
        Clinician clinician = findByUsername(username);
        if (clinician != null && clinician.credentialsAre(password)) return clinician;
        return null;
    }

    @GenerateView
    public Clinician findByUsername(String username) {
        List<Clinician> clinicians = queryView("by_username", username);
        if (clinicians != null && !clinicians.isEmpty()) return clinicians.get(0);
        return null;
    }

}
