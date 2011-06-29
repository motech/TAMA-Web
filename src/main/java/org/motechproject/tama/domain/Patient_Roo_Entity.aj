// WARNING: DO NOT EDIT THIS FILE. THIS FILE IS MANAGED BY SPRING ROO.
// You may push code into the target .java compilation unit if you wish to edit any member(s).

package org.motechproject.tama.domain;

import java.util.List;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.motechproject.tama.repository.Patients;
import org.springframework.beans.factory.annotation.Autowired;

privileged aspect Patient_Roo_Entity {


    declare parents : Patient extends BaseEntity;
    @Autowired
    transient Patients Patient.patients;

    private String Patient.doctorId;
    private String Patient.genderId;
    private String Patient.ivrLanguageId;

    public void Patient.persist() {
        this.patients.add(this);
    }
    
    public void Patient.remove() {
        this.patients.remove(this);
    }
    
    public void Patient.flush() {
    }
    
    public void Patient.clear() {
    }
    
    public Patient Patient.merge() {
       this.setRevision(this.patients.get(this.getId()).getRevision());
       this.patients.update(this);
       return this;
    }
    

    public static final Patients Patient.patients() {
        Patients patients = new Patient().patients;
        return patients;
    }

    public static long Patient.countPatients() {
        return patients().getAll().size();
    }
    
    public static List<Patient> Patient.findAllPatients() {
       return patients().getAll();
    }

    public static Patient Patient.findPatient(String id) {
        if (id == null) return null;
        return patients().get(id);
    }
    
    public static List<Patient> Patient.findPatientEntries(int firstResult, int maxResults) {
         return patients().getAll();
    }

    @JsonIgnore
    public Patients Patient.getPatients() {
        return patients;
    }

    public void Patient.setPatients(Patients patients) {
        this.patients = patients;
    }

    public String Patient.getDoctorId() {
        return doctorId;
    }

    public void Patient.setDoctorId(String doctorId) {
        this.doctorId = doctorId;
    }

    public String Patient.getGenderId() {
        return genderId;
    }

    public void Patient.setGenderId(String genderId) {
        this.genderId = genderId;
    }

    public String Patient.getIvrLanguageId() {
        return ivrLanguageId;
    }

    public void Patient.setIvrLanguageId(String ivrLanguageId) {
        this.ivrLanguageId = ivrLanguageId;
    }
}
