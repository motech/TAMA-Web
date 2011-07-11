package org.motechproject.tama.domain;

import org.ektorp.support.TypeDiscriminator;
import org.springframework.roo.addon.entity.RooEntity;
import org.springframework.roo.addon.javabean.RooJavaBean;

import javax.persistence.CascadeType;
import javax.persistence.ManyToMany;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

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
    private List<DrugDosage> drugDosages = new ArrayList<DrugDosage>();
}
