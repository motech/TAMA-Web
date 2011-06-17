package org.motechproject.tama;

import org.springframework.roo.addon.entity.RooEntity;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.tostring.RooToString;
import java.util.Date;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import org.springframework.format.annotation.DateTimeFormat;
import org.motechproject.tama.Gender;
import javax.persistence.ManyToOne;
import org.motechproject.tama.Initials;
import org.motechproject.tama.Doctor;

@RooJavaBean
@RooToString
@RooEntity
public class Patient {

    private String patientId;

    private String mobilePhoneNumber;

    @Temporal(TemporalType.TIMESTAMP)
    @DateTimeFormat(style = "S-")
    private Date dateOfBirth;

    private int travelTimeToClincInDays;

    private int travelTimeToClincInHours;

    private int travelTimeToClincInMinutes;

    @ManyToOne
    private Gender gender;

    @ManyToOne
    private Initials initials;

    @ManyToOne
    private Doctor principalDoctor;
}
