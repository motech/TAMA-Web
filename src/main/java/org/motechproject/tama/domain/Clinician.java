package org.motechproject.tama.domain;

import org.ektorp.support.TypeDiscriminator;
import org.jasypt.encryption.pbe.PBEStringEncryptor;
import org.motechproject.tama.TAMAConstants;
import org.motechproject.tama.TAMAMessages;
import org.motechproject.tama.repository.Clinics;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.tostring.RooToString;

import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@RooJavaBean
@RooToString
@TypeDiscriminator("doc.documentType == 'Clinician'")
public class Clinician extends CouchEntity{

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

    @Pattern(regexp = TAMAConstants.MOBILE_NUMBER_REGEX, message = TAMAMessages.MOBILE_NUMBER_REGEX_MESSAGE)
    private String alternateContactNumber;

    @NotNull
    private String password;

    @ManyToOne
    private Clinic clinic;

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    private Role role;

    public enum Role {
        Doctor {
            public String toString() {
                return "Doctor";
            }
        }
        , StudyNurse {
            public String toString() {
                return "Study Nurse";
            }
        }
    }

    private String encryptedPassword;


    private String clinicId;

}
