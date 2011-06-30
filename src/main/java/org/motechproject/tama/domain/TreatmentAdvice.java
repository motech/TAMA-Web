package org.motechproject.tama.domain;

import org.springframework.roo.addon.entity.RooEntity;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.tostring.RooToString;
import javax.validation.constraints.NotNull;
import java.util.Set;
import org.motechproject.tama.domain.DrugDosage;
import java.util.HashSet;
import javax.persistence.ManyToMany;
import javax.persistence.CascadeType;

@RooJavaBean
@RooToString
@RooEntity
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
