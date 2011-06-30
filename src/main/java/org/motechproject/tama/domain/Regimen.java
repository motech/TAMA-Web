package org.motechproject.tama.domain;

import org.springframework.roo.addon.entity.RooEntity;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.tostring.RooToString;
import javax.validation.constraints.NotNull;
import java.util.Set;
import org.motechproject.tama.domain.RegimenComposition;
import java.util.HashSet;
import javax.persistence.ManyToMany;
import javax.persistence.CascadeType;

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
