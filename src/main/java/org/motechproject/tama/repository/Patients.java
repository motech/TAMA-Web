package org.motechproject.tama.repository;

import org.apache.log4j.Logger;
import org.ektorp.CouchDbConnector;
import org.ektorp.ViewQuery;
import org.ektorp.support.CouchDbRepositorySupport;
import org.ektorp.support.GenerateView;
import org.ektorp.support.View;
import org.motechproject.tama.domain.Patient;
import org.motechproject.tama.security.AuthenticatedUser;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

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

    @View(name = "find_by_clinic", map = "function(doc) {if (doc.documentType =='Patient' && doc.clinic_id) {emit(doc.clinic_id, doc._id);}}")
    public List<Patient> findByClinic(String clinicId) {
        ViewQuery q = createQuery("find_by_clinic").key(clinicId).includeDocs(true);
        return db.queryView(q, Patient.class);
    }

    public List<Patient> getPatientsForClinic() {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        AuthenticatedUser user = (AuthenticatedUser) securityContext.getAuthentication().getPrincipal();
        return findByClinic(user.marker());
    }

}
