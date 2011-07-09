package org.motechproject.tama.repository;

import org.apache.log4j.Logger;
import org.ektorp.CouchDbConnector;
import org.ektorp.support.CouchDbRepositorySupport;
import org.ektorp.support.GenerateView;
import org.ektorp.support.View;
import org.motechproject.tama.domain.Patient;
import org.motechproject.tama.security.AuthenticatedUser;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import sun.rmi.runtime.Log;

import java.util.List;


@View(name = "all", map = "function(doc) { if (doc.documentType == 'Patient') { emit(null, doc) } }")
public class Patients extends CouchDbRepositorySupport<Patient> {
    private static Logger LOG = Logger.getLogger("org.motechproject.tama");

    public Patients(CouchDbConnector db) {
        super(Patient.class, db);
        initStandardDesignDocument();
    }

    @GenerateView
    public List<Patient> findByPatientId(String patientId) {
        return queryView("by_patientId", patientId);
    }

    @GenerateView
    public List<Patient> findByClinicId(String clinicId) {
        return queryView("by_clinicId", clinicId);
    }

    public List<Patient> getPatientsForClinician() {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        AuthenticatedUser user = (AuthenticatedUser) securityContext.getAuthentication().getPrincipal();
        return findByClinicId(user.marker());
    }
}
