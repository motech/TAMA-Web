// WARNING: DO NOT EDIT THIS FILE. THIS FILE IS MANAGED BY SPRING ROO.
// You may push code into the target .java compilation unit if you wish to edit any member(s).

package org.motechproject.tama.domain;

import org.apache.log4j.Logger;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.jasypt.encryption.pbe.PBEStringEncryptor;

import java.lang.String;

privileged aspect Clinician_Roo_JavaBean {
    private static Logger LOG = Logger.getLogger(Clinician.class);

    public String Clinician.getName() {
        return this.name;
    }

    public void Clinician.setName(String name) {
        this.name = name;
    }

    public String Clinician.getUsername() {
        return this.username;
    }

    public void Clinician.setUsername(String username) {
        this.username = username;
    }

    public String Clinician.getContactNumber() {
        return this.contactNumber;
    }

    public void Clinician.setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }

    public String Clinician.getAlternateContactNumber() {
        return this.alternateContactNumber;
    }

    public void Clinician.setAlternateContactNumber(String alternateContactNumber) {
        this.alternateContactNumber = alternateContactNumber;
    }

    @JsonIgnore
    public String Clinician.getPassword() {
        if(this.password != null) return this.password;
        if(this.encryptedPassword != null) return this.encryptor.decrypt(encryptedPassword);
        return null;
    }

    public void Clinician.setPassword(String password) {
        this.password = password;
        this.encryptedPassword = this.encryptor.encrypt(password);
    }

    public String Clinician.getClinicId() {
        return clinicId;
    }

    public void Clinician.setClinicId(String clinicId) {
        this.clinicId = clinicId;
    }

    @JsonIgnore
    public Clinic Clinician.getClinic() {
        if (this.clinic != null) return this.clinic;
        if (this.clinicId != null) return clinics.get(this.clinicId);
        return null;
    }

    public void Clinician.setClinic(Clinic clinic) {
        this.clinic = clinic;
        this.clinicId = clinic.getId();
    }

    public String Clinician.getEncryptedPassword() {
        return encryptedPassword;
    }

    public void Clinician.setEncryptedPassword(String encryptedPassword) {
        this.encryptedPassword = encryptedPassword;
    }

    @JsonIgnore
    public PBEStringEncryptor Clinician.getEncryptor() {
        return this.encryptor;
    }

    public void Clinician.setEncryptor(PBEStringEncryptor encryptor) {
        this.encryptor = encryptor;
    }



}
