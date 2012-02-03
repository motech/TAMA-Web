package org.motechproject.tama.patient.repository;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.commons.lang.StringUtils;
import org.ektorp.ComplexKey;
import org.ektorp.CouchDbConnector;
import org.ektorp.ViewQuery;
import org.ektorp.support.GenerateView;
import org.ektorp.support.View;
import org.motechproject.tama.common.repository.AbstractCouchRepository;
import org.motechproject.tama.common.util.UUIDUtil;
import org.motechproject.tama.facility.repository.AllClinics;
import org.motechproject.tama.patient.domain.HIVMedicalHistory;
import org.motechproject.tama.patient.domain.MedicalHistory;
import org.motechproject.tama.patient.domain.Patient;
import org.motechproject.tama.patient.domain.PatientPreferences;
import org.motechproject.tama.refdata.repository.AllGenders;
import org.motechproject.tama.refdata.repository.AllHIVTestReasons;
import org.motechproject.tama.refdata.repository.AllIVRLanguages;
import org.motechproject.tama.refdata.repository.AllModesOfTransmission;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class AllPatients extends AbstractCouchRepository<Patient> {
    private AllClinics allClinics;
    private AllGenders allGenders;
    private AllIVRLanguages allIVRLanguages;
    private AllUniquePatientFields allUniquePatientFields;
    private AllHIVTestReasons allHIVTestReasons;
    private AllModesOfTransmission allModesOfTransmission;

    @Autowired
    public AllPatients(@Qualifier("tamaDbConnector") CouchDbConnector db, AllClinics allClinics, AllGenders allGenders, AllIVRLanguages allIVRLanguages, AllUniquePatientFields allUniquePatientFields, AllHIVTestReasons allHIVTestReasons, AllModesOfTransmission allModesOfTransmission) {
        super(Patient.class, db);
        this.allClinics = allClinics;
        this.allGenders = allGenders;
        this.allIVRLanguages = allIVRLanguages;
        this.allUniquePatientFields = allUniquePatientFields;
        this.allHIVTestReasons = allHIVTestReasons;
        this.allModesOfTransmission = allModesOfTransmission;
        initStandardDesignDocument();
    }

    @GenerateView
    public Patient findByPatientId(String patientId) {
        ViewQuery q = createQuery("by_patientId").key(patientId).includeDocs(true);
        List<Patient> patients = db.queryView(q, Patient.class);
        if (patients.isEmpty()) return null;

        Patient patient = patients.get(0);
        loadPatientDependencies(patient);
        return patient;
    }

    @View(name = "find_by_clinic", map = "function(doc) {if (doc.documentType =='Patient' && doc.clinic_id) {emit(doc.clinic_id, doc._id);}}")
    public List<Patient> findByClinic(String clinicId) {
        ViewQuery q = createQuery("find_by_clinic").key(clinicId).includeDocs(true);
        List<Patient> patients = db.queryView(q, Patient.class);
        for (Patient patient : patients) {
            loadPatientDependencies(patient);
        }
        return patients;
    }

    public Patient findByMobileNumber(String phoneNumber) {
        return singleResult(findAllByMobileNumber(phoneNumber));
    }

    @View(name = "find_by_mobile_number", map = "function(doc) {if (doc.documentType =='Patient' && doc.mobilePhoneNumber) {emit(doc.mobilePhoneNumber, doc._id);}}")
    public List<Patient> findAllByMobileNumber(String phoneNumber) {
        ViewQuery q = createQuery("find_by_mobile_number").key(phoneNumber).includeDocs(true);
        List<Patient> patients = db.queryView(q, Patient.class);
        for (Patient patient : patients) {
            loadPatientDependencies(patient);
        }
        return patients;
    }

    @View(name = "find_by_mobile_number_and_passcode", map = "function(doc) {if (doc.documentType =='Patient' && doc.mobilePhoneNumber && doc.patientPreferences.passcode) {emit([doc.mobilePhoneNumber, doc.patientPreferences.passcode], doc._id);}}")
    public Patient findByMobileNumberAndPasscode(String phoneNumber, String passcode) {
        ComplexKey key = ComplexKey.of(phoneNumber, passcode);
        ViewQuery q = createQuery("find_by_mobile_number_and_passcode").key(key).includeDocs(true);
        List<Patient> patients = db.queryView(q, Patient.class);
        Patient patient = singleResult(patients);
        loadPatientDependencies(patient);
        return patient;
    }

    public Patient findByPatientIdAndClinicId(final String patientId, final String clinicId) {
        List<Patient> patients = findByClinic(clinicId);
        if (patients == null) return null;
        CollectionUtils.filter(patients, new Predicate() {
            @Override
            public boolean evaluate(Object o) {
                Patient patient = (Patient) o;
                return patientId.equalsIgnoreCase(patient.getPatientId());
            }
        });
        return singleResult(patients);
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
        addOrReplace(patient, "patientId", patient.getPatientId());
    }

    @Override
    public void add(Patient entity) {
        entity.setId(UUIDUtil.newUUID());
        allUniquePatientFields.add(entity);
        super.add(entity);
    }

    @Override
    public void remove(Patient entity) {
        allUniquePatientFields.remove(entity);
        super.remove(entity);
    }

    @Override
    public Patient get(String id) {
        Patient patient = super.get(id);
        loadPatientDependencies(patient);
        return patient;
    }

    private void loadPatientDependencies(Patient patient) {
        if (patient == null) return;
        if (!StringUtils.isBlank(patient.getGenderId()))
            patient.setGender(allGenders.get(patient.getGenderId()));
        PatientPreferences patientPreferences = patient.getPatientPreferences();
        if (!StringUtils.isBlank(patientPreferences.getIvrLanguageId()))
            patientPreferences.setIvrLanguage(allIVRLanguages.get(patientPreferences.getIvrLanguageId()));
        if (!StringUtils.isBlank(patient.getClinic_id()))
            patient.setClinic(allClinics.get(patient.getClinic_id()));
        MedicalHistory medicalHistory = patient.getMedicalHistory();
        if (medicalHistory != null) {
            HIVMedicalHistory hivMedicalHistory = medicalHistory.getHivMedicalHistory();
            if (!StringUtils.isBlank(hivMedicalHistory.getTestReasonId()))
                hivMedicalHistory.setTestReason(allHIVTestReasons.get(hivMedicalHistory.getTestReasonId()));
            if (!StringUtils.isBlank(hivMedicalHistory.getModeOfTransmissionId()))
                hivMedicalHistory.setModeOfTransmission(allModesOfTransmission.get(hivMedicalHistory.getModeOfTransmissionId()));
        }
    }
}