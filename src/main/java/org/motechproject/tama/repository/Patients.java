package org.motechproject.tama.repository;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.log4j.Logger;
import org.ektorp.CouchDbConnector;
import org.ektorp.ViewQuery;
import org.ektorp.support.CouchDbRepositorySupport;
import org.ektorp.support.GenerateView;
import org.ektorp.support.View;
import org.motechproject.tama.TAMAConstants;
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

    @View(name = "find_by_patient_id", map = "function(doc) {if (doc.documentType =='Patient' && doc.patientId) {emit(doc.patientId, doc._id);}}")
    public List<Patient> findByPatientId(String patientId) {
        ViewQuery q = createQuery("find_by_patient_id").key(patientId).includeDocs(true);
        return db.queryView(q, Patient.class);
    }

    public List<Patient> findByPatientIdAndClinic(final String patientId) {
        List<Patient> patients = byClinic();
        if (patients != null) {
            CollectionUtils.filter(patients, new Predicate() {
                @Override
                public boolean evaluate(Object o) {
                    Patient patient = (Patient) o;
                    return patientId.equals(patient.getPatientId());
                }
            });
        }
        return patients;
    }

    @View(name = "find_by_clinic", map = "function(doc) {if (doc.documentType =='Patient' && doc.clinic_id) {emit(doc.clinic_id, doc._id);}}")
    public List<Patient> findByClinic(String clinicId) {
        ViewQuery q = createQuery("find_by_clinic").key(clinicId).includeDocs(true);
        return db.queryView(q, Patient.class);
    }

    public List<Patient> byClinic() {
        String clinicId = loggedInClinic();
        return findByClinic(clinicId);
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

    @View(name = "find_by_mobile_number", map = "function(doc) {if (doc.documentType =='Patient' && doc.mobilePhoneNumber) {emit(doc.mobilePhoneNumber, doc._id);}}")
    public Patient findByMobileNumber(String phoneNumber) {
        ViewQuery q = createQuery("find_by_mobile_number").key(phoneNumber).includeDocs(true);
        return db.queryView(q, Patient.class).get(0);
    }
}
