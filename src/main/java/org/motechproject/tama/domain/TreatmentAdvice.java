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
public class TreatmentAdvice {

    @NotNull
    private String patientId;

    
    private String regimenId;
    
    @NotNull
    private Regimen regimen;

    @NotNull
    private String regimenCompositionId;

    @ManyToMany(cascade = CascadeType.ALL)
    private Set<DrugDosage> drugDosages = new HashSet<DrugDosage>();
}
