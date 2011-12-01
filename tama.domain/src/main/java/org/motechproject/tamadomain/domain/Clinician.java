package org.motechproject.tamadomain.domain;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.ektorp.support.TypeDiscriminator;
import org.motechproject.tamacommon.TAMAConstants;
import org.motechproject.tamacommon.TAMAMessages;
import org.motechproject.tamacommon.domain.CouchEntity;

import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@TypeDiscriminator("doc.documentType == 'Clinician'")
public class Clinician extends CouchEntity implements TAMAUser {
    @NotNull
    private String name;
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
        return this.password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getClinicId() {
        return clinicId;
    }

    public void setClinicId(String clinicId) {
        this.clinicId = clinicId;
    }

    @JsonIgnore
    public Clinic getClinic() {
        return clinic;
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
