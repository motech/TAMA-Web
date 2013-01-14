package org.motechproject.tama.patient.repository;

import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.map.ObjectMapper;
import org.ektorp.ComplexKey;
import org.ektorp.CouchDbConnector;
import org.ektorp.ViewQuery;
import org.ektorp.ViewResult;
import org.ektorp.support.GenerateView;
import org.ektorp.support.ListFunction;
import org.ektorp.support.View;
import org.motechproject.tama.common.repository.AllAuditRecords;
import org.motechproject.tama.common.repository.AuditableCouchRepository;
import org.motechproject.tama.common.util.UUIDUtil;
import org.motechproject.tama.facility.domain.Clinic;
import org.motechproject.tama.facility.repository.AllClinics;
import org.motechproject.tama.patient.domain.HIVMedicalHistory;
import org.motechproject.tama.patient.domain.MedicalHistory;
import org.motechproject.tama.patient.domain.Patient;
import org.motechproject.tama.patient.domain.PatientPreferences;
import org.motechproject.tama.refdata.objectcache.AllGendersCache;
import org.motechproject.tama.refdata.objectcache.AllHIVTestReasonsCache;
import org.motechproject.tama.refdata.objectcache.AllIVRLanguagesCache;
import org.motechproject.tama.refdata.objectcache.AllModesOfTransmissionCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Component
public class AllPatients extends AuditableCouchRepository<Patient> {

    private AllClinics allClinics;
    private AllGendersCache allGenders;
    private AllIVRLanguagesCache allIVRLanguages;
    private AllUniquePatientFields allUniquePatientFields;
    private AllHIVTestReasonsCache allHIVTestReasons;
    private AllModesOfTransmissionCache allModesOfTransmission;

    @Autowired
    public AllPatients(@Qualifier("tamaDbConnector") CouchDbConnector db, AllClinics allClinics, AllGendersCache allGenders,
                       AllIVRLanguagesCache allIVRLanguages, AllUniquePatientFields allUniquePatientFields,
                       AllHIVTestReasonsCache allHIVTestReasons, AllModesOfTransmissionCache allModesOfTransmission,
                       AllAuditRecords allAuditRecords) {
        super(Patient.class, db, allAuditRecords);
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
        loadPatientDependencies(patient, true);
        return patient;
    }

    @View(name = "find_by_clinic", map = "function(doc) {if (doc.documentType =='Patient' && doc.clinic_id) {emit(doc.clinic_id, doc._id);}}")
    public List<Patient> findByClinic(String clinicId) {
        final Clinic clinic = allClinics.get(clinicId);
        ViewQuery q = createQuery("find_by_clinic").key(clinicId).includeDocs(true);
        List<Patient> patients = db.queryView(q, Patient.class);
        for (Patient patient : patients) {
            loadPatientDependencies(patient, false);
            patient.setClinic(clinic);
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
            loadPatientDependencies(patient, true);
        }
        return patients;
    }

    public Patient findByMobileNumberAndPasscode(String phoneNumber, String passcode) {
        List<Patient> patients = findAllByMobileNumberAndPasscode(phoneNumber, passcode);
        Patient patient = singleResult(patients);
        loadPatientDependencies(patient, true);
        return patient;
    }


    @View(name = "find_by_patient_id_and_clinic_id", map = "function(doc) {if (doc.documentType =='Patient' && doc.patientId && doc.clinic_id) {emit([doc.patientId.toLowerCase(), doc.clinic_id.toLowerCase()], doc._id);}}")
    public Patient findByPatientIdAndClinicId(final String patientId, final String clinicId) {
        ComplexKey key = ComplexKey.of(patientId.toLowerCase(), clinicId.toLowerCase());
        ViewQuery q = createQuery("find_by_patient_id_and_clinic_id").key(key).includeDocs(true);
        Patient patient = singleResult(db.queryView(q, Patient.class));
        loadPatientDependencies(patient, true);
        return patient;
    }

    public Patient findByIdAndClinicId(final String id, String clinicId) {
        Patient patient = get(id);
        if (!patient.getClinic_id().equals(clinicId)) return null;
        return patient;
    }

    public String findClinicFor(Patient patient) {
        return get(patient.getId()).getClinic_id();
    }

    public void addToClinic(Patient patient, String clinicId, String userName) {
        patient.setClinic_id(clinicId);
        add(patient, userName);
    }

    @Override
    public void add(Patient entity, String userName) {
        entity.setId(UUIDUtil.newUUID());
        allUniquePatientFields.add(entity);
        super.add(entity, userName);
    }

    @Override
    public void update(Patient entity, String userName) {
        allUniquePatientFields.update(entity);
        super.update(entity, userName);
    }

    @Override
    public void remove(Patient entity, String userName) {
        allUniquePatientFields.remove(entity);
        super.remove(entity, userName);
    }

    @Override
    public Patient get(String id) {
        Patient patient = super.get(id);
        loadPatientDependencies(patient, true);
        return patient;
    }

    @Override
    @View(name = "all_patients_join_clinic", map = "function(doc) {" +
            "                             if (doc.documentType =='Patient') {" +
            "                                   emit([doc.clinic_id, 1], doc);" +
            "                             } else if(doc.documentType =='Clinic') {" +
            "                                   emit([doc._id, 0], doc);" +
            "                             }" +
            "                         }")
    @ListFunction(name = "all_patients_join_clinic_list", function = "function(head, req) { " +
            "                 var headers = {'Content-Type': 'application/json'};  " +
            "                 var result;  " +
            "                 if(req.query.include_docs != 'true') {  " +
            "                       start({'code': 400, headers: headers});  " +
            "                       result = {'error': 'I require include_docs=true'};  " +
            "                 } else {  " +
            "                        start({'headers': headers});  " +
            "                        result = {'rows': []};  " +
            "                        var clinicRow = getRow(); " +
            "                        while(clinicRow) { " +
            "                           var nextRow;" +
            "                           while(nextRow = getRow()){ " +
            "                               if(nextRow.value.type === 'Patient'){ " +
            "                                    var patientRow = nextRow; " +
            "                                    patientRow.value.clinic = clinicRow.value; " +
            "                                    var ektorpRow={'value':{}};" +
            "                                    ektorpRow.value=patientRow.value; " +
            "                                    result.rows.push(ektorpRow);  " +
            "                               }else{" +
            "                                    break;" +
            "                               } " +
            "                           } " +
            "                           clinicRow = nextRow; " +
            "                        }  " +
            "                 } " +
            "  send(JSON.stringify(result)); " +
            "}")
    public List<Patient> getAll() {
        ComplexKey startKey = ComplexKey.of(null, null);
        ComplexKey endKey = ComplexKey.of(ComplexKey.emptyObject(), ComplexKey.emptyObject());
        List<Patient> patients = new ArrayList<Patient>();
        try {
            ViewQuery q = createQuery("all_patients_join_clinic").startKey(startKey).endKey(endKey).includeDocs(true).listName("all_patients_join_clinic_list");
            ViewResult result = db.queryView(q);
            ObjectMapper mapper = new ObjectMapper();
            for (ViewResult.Row row : result) {
                Patient patient = mapper.readValue(row.getValueAsNode(), Patient.class);
                loadPatientDependencies(patient, false);
                patients.add(patient);
            }
        } catch (IOException e) {
        }
        return patients;
    }

    @View(name = "find_by_mobile_number_and_passcode", map = "function(doc) {if (doc.documentType =='Patient' && doc.mobilePhoneNumber && doc.patientPreferences.passcode) {emit([doc.mobilePhoneNumber, doc.patientPreferences.passcode], doc._id);}}")
    private List<Patient> findAllByMobileNumberAndPasscode(String phoneNumber, String passcode) {
        ComplexKey key = ComplexKey.of(phoneNumber, passcode);
        ViewQuery q = createQuery("find_by_mobile_number_and_passcode").key(key).includeDocs(true);
        return db.queryView(q, Patient.class);
    }

    private void loadPatientDependencies(Patient patient, boolean shouldLoadClinic) {
        if (patient == null) return;
        if (!StringUtils.isBlank(patient.getGenderId()))
            patient.setGender(allGenders.getBy(patient.getGenderId()));
        PatientPreferences patientPreferences = patient.getPatientPreferences();
        if (!StringUtils.isBlank(patientPreferences.getIvrLanguageId()))
            patientPreferences.setIvrLanguage(allIVRLanguages.getBy(patientPreferences.getIvrLanguageId()));
        if (!StringUtils.isBlank(patient.getClinic_id()) && shouldLoadClinic)
            patient.setClinic(allClinics.get(patient.getClinic_id()));
        MedicalHistory medicalHistory = patient.getMedicalHistory();
        if (medicalHistory != null) {
            HIVMedicalHistory hivMedicalHistory = medicalHistory.getHivMedicalHistory();
            if (!StringUtils.isBlank(hivMedicalHistory.getTestReasonId()))
                hivMedicalHistory.setTestReason(allHIVTestReasons.getBy(hivMedicalHistory.getTestReasonId()));
            if (!StringUtils.isBlank(hivMedicalHistory.getModeOfTransmissionId()))
                hivMedicalHistory.setModeOfTransmission(allModesOfTransmission.getBy(hivMedicalHistory.getModeOfTransmissionId()));
        }
    }
}