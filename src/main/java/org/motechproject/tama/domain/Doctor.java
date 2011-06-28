package org.motechproject.tama.domain;

import org.springframework.roo.addon.entity.RooEntity;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.tostring.RooToString;
import javax.validation.constraints.NotNull;
import de.saxsys.roo.equals.addon.RooEquals;

@RooJavaBean
@RooToString
@RooEntity
@RooEquals
public class Doctor {

    @NotNull
    private String doctorId;

    @NotNull
    private String firstName;

    @NotNull
    private String lastName;
}
