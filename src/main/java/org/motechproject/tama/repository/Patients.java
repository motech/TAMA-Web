package org.motechproject.tama.repository;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.log4j.Logger;
import org.ektorp.CouchDbConnector;
import org.ektorp.ViewQuery;
import org.ektorp.support.CouchDbRepositorySupport;
import org.ektorp.support.View;
import org.motechproject.tama.domain.Patient;

import java.util.Collections;
import java.util.List;

@View(name = "all", map = "function(doc) { if (doc.documentType == 'Patient') { emit(null, doc) } }")
public class Patients extends CouchDbRepositorySupport<Patient> {
    private static Logger LOG = Logger.getLogger(Patients.class);

    private Clinics clinics;

    public Patients(CouchDbConnector db, Clinics clinics) {
        super(Patient.class, db);
        this.clinics = clinics;
        initStandardDesignDocument();
    }

    @View(name = "find_by_patient_id", map = "function(doc) {if (doc.documentType =='Patient' && doc.patientId) {emit(doc.patientId, doc._id);}}")
    public List<Patient> findByPatientId(String patientId) {
        ViewQuery q = createQuery("find_by_patient_id").key(patientId).includeDocs(true);
        return db.queryView(q, Patient.class);
    }

    @View(name = "find_by_clinic", map = "function(doc) {if (doc.documentType =='Patient' && doc.clinic_id) {emit(doc.clinic_id, doc._id);}}")
    public List<Patient> findByClinic(String clinicId) {
        ViewQuery q = createQuery("find_by_clinic").key(clinicId).includeDocs(true);
        return db.queryView(q, Patient.class);
    }

    @View(name = "find_by_mobile_number", map = "function(doc) {if (doc.documentType =='Patient' && doc.mobilePhoneNumber) {emit(doc.mobilePhoneNumber, doc._id);}}")
    public Patient findByMobileNumber(String phoneNumber) {
        ViewQuery q = createQuery("find_by_mobile_number").key(phoneNumber).includeDocs(true);
        List<Patient> patients = db.queryView(q, Patient.class);
        return singleResult(patients);
    }

    public List<Patient> findByPatientIdAndClinicId(final String patientId, final String clinicId) {
        List<Patient> patients = findByClinic(clinicId);
        if (patients == null) return patients;
        CollectionUtils.filter(patients, new Predicate() {
            @Override
            public boolean evaluate(Object o) {
                Patient patient = (Patient) o;
                return patientId.equals(patient.getPatientId());
            }
        });
        return patients;
    }

    public Patient findByIdAndClinicId(final String id, String clinicId) {
        List<Patient> patients = findByClinic(clinicId);
        if (patients == null) return null;
        CollectionUtils.filter(patients, new Predicate() {
            @Override
            public boolean evaluate(Object o) {
                Patient patient = (Patient) o;
                return id.equals(patient.getId());
            }
        });
        return singleResult(patients);
    }

    public String findClinicFor(Patient patient) {
        return get(patient.getId()).getClinic_id();
    }

    public void addToClinic(Patient patient, String clinicId) {
        patient.setClinic_id(clinicId);
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
        if (checkIfActive(patient)) patient.activate();
        patient.setClinic_id(findClinicFor(patient));
        patient.setRevision(get(patient.getId()).getRevision());
        update(patient);
    }

    public void remove(String id) {
        remove(get(id));
    }

    private Patient singleResult(List<Patient> patients) {
        return (patients == null || patients.isEmpty()) ? null : patients.get(0);
    }
}
