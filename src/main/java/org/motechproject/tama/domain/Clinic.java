package org.motechproject.tama.domain;

import org.ektorp.support.TypeDiscriminator;
import org.motechproject.tama.repository.Clinics;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.roo.addon.entity.RooEntity;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.tostring.RooToString;
import javax.validation.constraints.NotNull;
import org.motechproject.tama.domain.City;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.ManyToOne;

@RooJavaBean
@RooToString
@TypeDiscriminator("doc.documentType == 'Clinic'")
public class Clinic extends CouchEntity{

    @Autowired
    private Clinics clinics;

    @NotNull
    private String name;

    @NotNull
    private String address;

    private String phone;

    @ManyToOne
    private City city;
}
