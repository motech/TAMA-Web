package org.motechproject.tama.domain;

import org.ektorp.support.TypeDiscriminator;
import org.springframework.roo.addon.entity.RooEntity;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.tostring.RooToString;
import javax.validation.constraints.NotNull;

@RooJavaBean
@RooToString
@TypeDiscriminator("doc.documentType == 'Clinician'")
public class Clinician extends CouchEntity{

    @NotNull
    private String name;

    @NotNull
    private String username;

    @NotNull
    private String contactNumber;

    @NotNull
    private String alternateContactNumber;

    @NotNull
    private String password;
}
