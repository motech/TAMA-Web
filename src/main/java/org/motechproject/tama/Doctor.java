package org.motechproject.tama;

import org.springframework.roo.addon.entity.RooEntity;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.tostring.RooToString;

@RooJavaBean
@RooToString
@RooEntity
public class Doctor {

    private String doctorId;

    private String firstName;

    private String lastName;
}
