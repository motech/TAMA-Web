package org.motechproject.tama.domain;

import java.util.Date;

import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Past;
import javax.validation.constraints.Pattern;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.motechproject.tama.TAMAConstants;
import org.motechproject.tama.TAMAMessages;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.roo.addon.entity.RooEntity;
import org.springframework.roo.addon.javabean.RooJavaBean;

@RooJavaBean
@RooEntity
public class Patient {

    @NotNull
    protected String patientId;

    @NotNull
    @Pattern(regexp = TAMAConstants.PASSCODE_REGEX, message = TAMAMessages.PASSCODE_REGEX_MESSAGE)
    protected String passcode;

    @NotNull
    @Pattern(regexp = TAMAConstants.MOBILE_NUMBER_REGEX, message = TAMAMessages.MOBILE_NUMBER_REGEX_MESSAGE)
    protected String mobilePhoneNumber;

    @Temporal(TemporalType.DATE)
    @DateTimeFormat(style = "S-", pattern = "dd/MM/yyyy")
    @Past(message = TAMAMessages.DATE_OF_BIRTH_MUST_BE_IN_PAST)
    @NotNull
    protected Date dateOfBirth;

    private ReminderCall reminderCall = ReminderCall.Daily;

    private Status status = Status.Inactive;

    private int travelTimeToClinicInDays;

    private int travelTimeToClinicInHours;

    private int travelTimeToClinicInMinutes;

    @ManyToOne
    private Gender gender;

    @ManyToOne
    private IVRLanguage ivrLanguage;

    @ManyToOne
    @JsonIgnore
    private Doctor principalDoctor;

    private Date registrationDate;

    public enum ReminderCall {
        Daily, Weekly
    }

    public enum Status {
        Inactive, Active
    }
}
