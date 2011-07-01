package org.motechproject.tama.domain;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.ManyToMany;
import javax.validation.constraints.NotNull;

import org.springframework.roo.addon.entity.RooEntity;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.tostring.RooToString;

@RooJavaBean
@RooToString
@RooEntity
public class Regimen {

    @NotNull
    private String name;

    @ManyToMany(cascade = CascadeType.ALL)
    private Set<RegimenComposition> compositions = new HashSet<RegimenComposition>();

    @NotNull
    private String regimenDisplayName;
}
