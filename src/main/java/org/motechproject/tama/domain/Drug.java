package org.motechproject.tama.domain;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.ManyToMany;
import javax.validation.constraints.NotNull;

import org.springframework.roo.addon.entity.RooEntity;
import org.springframework.roo.addon.javabean.RooJavaBean;

@RooJavaBean
@RooEntity
public class Drug {

    @NotNull
    private String name;

    @ManyToMany(cascade = CascadeType.ALL)
    private Set<Brand> brands = new HashSet<Brand>();
}
