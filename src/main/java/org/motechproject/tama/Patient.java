package org.motechproject.tama;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.roo.addon.entity.RooEntity;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.tostring.RooToString;

import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import java.util.Date;

@RooJavaBean
@RooToString
@RooEntity
public class Patient {

    @NotNull
    private String patientId;

    @NotNull
    private String mobilePhoneNumber;

    @Temporal(TemporalType.TIMESTAMP)
    @DateTimeFormat(style = "S-")
    @NotNull
    private Date dateOfBirth;

    private int travelTimeToClinicInDays;

    private int travelTimeToClinicInHours;

    private int travelTimeToClinicInMinutes;

    @ManyToOne
    private Gender gender;

    @ManyToOne
    private Title title;

    @ManyToOne
    private Doctor principalDoctor;
}
