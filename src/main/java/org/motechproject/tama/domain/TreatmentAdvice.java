package org.motechproject.tama.domain;

import org.ektorp.support.TypeDiscriminator;
import org.springframework.roo.addon.entity.RooEntity;
import org.springframework.roo.addon.javabean.RooJavaBean;

import javax.persistence.CascadeType;
import javax.persistence.ManyToMany;
import javax.validation.constraints.NotNull;
import java.util.HashSet;
import java.util.Set;

@RooJavaBean
@RooEntity
@TypeDiscriminator("doc.documentType == 'TreatmentAdvice'")
public class TreatmentAdvice {

    @NotNull
    private String patientId;

    @NotNull
    private String regimenId;
    
    @NotNull
    private String regimenCompositionId;

    @ManyToMany(cascade = CascadeType.ALL)
    private Set<DrugDosage> drugDosages = new HashSet<DrugDosage>();
}
