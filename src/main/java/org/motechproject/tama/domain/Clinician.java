package org.motechproject.tama.domain;

import flexjson.JSONDeserializer;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.DeserializationContext;
import org.codehaus.jackson.map.JsonDeserializer;
import org.codehaus.jackson.map.JsonSerializer;
import org.codehaus.jackson.map.SerializerProvider;
import org.codehaus.jackson.map.annotate.JsonDeserialize;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.ektorp.support.TypeDiscriminator;
import org.motechproject.tama.TAMAConstants;
import org.motechproject.tama.TAMAMessages;
import org.motechproject.tama.repository.Clinics;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.roo.addon.entity.RooEntity;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.tostring.RooToString;

import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.io.IOException;

@RooJavaBean
@RooToString
@TypeDiscriminator("doc.documentType == 'Clinician'")
public class Clinician extends CouchEntity{

    @Autowired
    private Clinics clinics;

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

    private String clinicId;

}
