// WARNING: DO NOT EDIT THIS FILE. THIS FILE IS MANAGED BY SPRING ROO.
// You may push code into the target .java compilation unit if you wish to edit any member(s).

package org.motechproject.tama;

import org.codehaus.jackson.annotate.JsonProperty;
import org.ektorp.support.TypeDiscriminator;
import org.motechproject.tama.repository.Patients;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

privileged aspect Patient_Roo_Entity {
    

    @Autowired
    transient Patients Patient.patients;


    @TypeDiscriminator
    private String Patient.documentType = "Patient";
    
    @JsonProperty("_id")
    private String Patient.id;

    @JsonProperty("_rev")
    private String Patient.revision;

    private Integer Patient.version;

    public Integer Patient.getVersion() {
        return version;
    }

    public void Patient.setVersion(Integer version) {
        this.version = version;
    }

    public String Patient.getId() {
        return this.id;
    }
    
    public void Patient.setId(String id) {
        this.id = id;
    }
    
    public String Patient.getRevision() {
        return this.revision;
    }

    public void Patient.setRevision(String revision) {
        this.revision = revision;
    }

    public String Patient.getDocumentType() {
        return this.documentType;
    }

    public void Patient.setDocumentType(String documentType) {
        this.documentType = documentType;
    }

    @Transactional
    public void Patient.persist() {
        this.patients.add(this);
    }
    
    @Transactional
    public void Patient.remove() {
        this.patients.remove(this);
    }
    
    @Transactional
    public void Patient.flush() {
    }
    
    @Transactional
    public void Patient.clear() {
    }
    
    @Transactional
    public Patient Patient.merge() {
        return this;
    }
    

    public static final Patients Patient.patients() {
        Patients patients = new Patient().patients;
        return patients;
    }

    public static long Patient.countPatients() {
       // return entityManager().createQuery("SELECT COUNT(o) FROM Patient o", Long.class).getSingleResult();
        return 10;
    }
    
    public static List<Patient> Patient.findAllPatients() {
        //return entityManager().createQuery("SELECT o FROM Patient o", Patient.class).getResultList();
        return null;
    }
    
    public static Patient Patient.findPatient(String id) {
        if (id == null) return null;
        return patients().get(id);
    }
    
    public static List<Patient> Patient.findPatientEntries(int firstResult, int maxResults) {
       // return entityManager().createQuery("SELECT o FROM Patient o", Patient.class).setFirstResult(firstResult).setMaxResults(maxResults).getResultList();
        return null;
    }
    
}
