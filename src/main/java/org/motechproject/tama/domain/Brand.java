package org.motechproject.tama.domain;

import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;

import org.springframework.roo.addon.entity.RooEntity;
import org.springframework.roo.addon.javabean.RooJavaBean;

@RooJavaBean
@RooEntity
public class Brand {

    @NotNull
    private String name;

    @ManyToOne
    private Company company;
}
