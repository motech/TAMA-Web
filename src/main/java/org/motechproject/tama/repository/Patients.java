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
    private static Logger LOG = Logger.getLogger(Patients.class);

    public Patients(CouchDbConnector db) {
        super(Patient.class, db);
        initStandardDesignDocument();
    }

    @GenerateView
    public List<Patient> findById(String patientId) {
        return queryView("by_patientId", patientId);
    }

    @View(name = "find_by_clinic", map = "function(doc) {if (doc.documentType =='Patient' && doc.clinic_id) {emit(doc.clinic_id, doc._id);}}")
    public List<Patient> findByClinic(String clinicId) {
        ViewQuery q = createQuery("find_by_clinic").key(clinicId).includeDocs(true);
        return db.queryView(q, Patient.class);
    }

    public List<Patient> byClinic() {
        return findByClinic(loggedInClinic());
    }

    public void addToClinic(Patient patient) {
        patient.setClinic_id(loggedInClinic());
        add(patient);
    }

    public void activate(String id) {
        Patient patient = get(id);
        patient.activate();
        merge(patient);
    }

    public boolean checkIfActive(Patient patient) {
        return get(patient.getId()).isActive();
    }

    public void merge(Patient patient) {
        patient.setRevision(get(patient.getId()).getRevision());
        update(patient);
    }

    public void remove(String id) {
        remove(get(id));
    }

    private String loggedInClinic() {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        AuthenticatedUser user = (AuthenticatedUser) securityContext.getAuthentication().getPrincipal();
        return user.marker();
    }

    public String findClinicFor(Patient patient) {
        return get(patient.getId()).getClinic_id();
    }
}
