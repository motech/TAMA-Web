package org.motechproject.tama;

import org.ektorp.docref.DocumentReferences;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.roo.addon.entity.RooEntity;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.tostring.RooToString;

import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Past;
import javax.validation.constraints.Pattern;
import java.util.Date;

@RooJavaBean
@RooToString
@RooEntity
public class Patient {

    @NotNull
    private String patientId;

    @NotNull
    @Pattern(regexp = TAMAConstants.MOBILE_NUMBER_REGEX, message = TAMAMessages.MOBILE_NUMBER_REGEX_MESSAGE)
    private String mobilePhoneNumber;

    @Temporal(TemporalType.DATE)
    @DateTimeFormat(style = "S-", pattern = "dd/MM/yyyy")
    @Past(message = TAMAMessages.DATE_OF_BIRTH_MUST_BE_IN_PAST)
    @NotNull
    private Date dateOfBirth;

    private int travelTimeToClinicInDays;

    private int travelTimeToClinicInHours;

    private int travelTimeToClinicInMinutes;

    @ManyToOne
    private Gender gender;

    @ManyToOne
    private Doctor principalDoctor;
}
