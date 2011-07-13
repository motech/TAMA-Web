package org.motechproject.tama.domain;

import org.apache.log4j.Logger;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.ektorp.support.TypeDiscriminator;
import org.jasypt.encryption.pbe.PBEStringEncryptor;
import org.motechproject.tama.TAMAConstants;
import org.motechproject.tama.TAMAMessages;
import org.motechproject.tama.repository.Clinics;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Configurable
@TypeDiscriminator("doc.documentType == 'Clinician'")
public class Clinician extends CouchEntity implements TAMAUser {
    @Autowired
    private Clinics clinics;

    @Autowired
    private PBEStringEncryptor encryptor;

    @NotNull
    private String name;
    @NotNull
    private String username;
    @NotNull
    @Pattern(regexp = TAMAConstants.MOBILE_NUMBER_REGEX, message = TAMAMessages.MOBILE_NUMBER_REGEX_MESSAGE)
    private String contactNumber;
    @ManyToOne
    private Clinic clinic;
    private String alternateContactNumber;
    private String password;
    private String encryptedPassword;
    private String clinicId;
    private Role role;

    private static Logger LOG = Logger.getLogger(Clinician.class);

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUsername() {
        return this.username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getContactNumber() {
        return this.contactNumber;
    }

    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }

    public String getAlternateContactNumber() {
        return this.alternateContactNumber;
    }

    public void setAlternateContactNumber(String alternateContactNumber) {
        this.alternateContactNumber = alternateContactNumber;
    }

    @JsonIgnore
    public String getPassword() {
        if(this.password != null) return this.password;
        if(this.encryptedPassword != null) return this.encryptor.decrypt(encryptedPassword);
        return null;
    }

    public void setPassword(String password) {
        this.password = password;
        this.encryptedPassword = this.encryptor.encrypt(password);
    }

    public String getClinicId() {
        return clinicId;
    }

    public void setClinicId(String clinicId) {
        this.clinicId = clinicId;
    }

    @JsonIgnore
    public Clinic getClinic() {
        if (this.clinic != null) return this.clinic;
        if (this.clinicId != null) return clinics.get(this.clinicId);
        return null;
    }

    public void setClinic(Clinic clinic) {
        this.clinic = clinic;
        this.clinicId = clinic.getId();
    }

    public String getEncryptedPassword() {
        return encryptedPassword;
    }

    public void setEncryptedPassword(String encryptedPassword) {
        this.encryptedPassword = encryptedPassword;
    }

    @JsonIgnore
    public PBEStringEncryptor getEncryptor() {
        return this.encryptor;
    }

    public void setEncryptor(PBEStringEncryptor encryptor) {
        this.encryptor = encryptor;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public enum Role {
        Doctor {
            public String toString() {
                return "Doctor";
            }
        }, StudyNurse {
            public String toString() {
                return "Study Nurse";
            }
        };
    }
    public boolean credentialsAre(String password) {
        return getPassword().equals(password);
    }

    @Override
    @JsonIgnore
    public String getClinicName() {
        return getClinic().getName();
    }

}
