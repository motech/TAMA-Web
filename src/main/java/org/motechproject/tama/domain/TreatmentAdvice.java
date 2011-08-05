package org.motechproject.tama.domain;

import org.ektorp.support.TypeDiscriminator;

import javax.persistence.CascadeType;
import javax.persistence.ManyToMany;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@TypeDiscriminator("doc.documentType == 'TreatmentAdvice'")
public class TreatmentAdvice extends CouchEntity {

    @NotNull
    private String patientId;

    @NotNull
    private String regimenId;
    
    @NotNull
    private String drugCompositionId;

    @NotNull
    private String drugCompositionGroupId;

    @ManyToMany(cascade = CascadeType.ALL)
    private List<DrugDosage> drugDosages = new ArrayList<DrugDosage>();
    
    public String getPatientId() {
        return this.patientId;
    }
    
    public void setPatientId(String patientId) {
        this.patientId = patientId;
    }
    
    public String getRegimenId() {
        return this.regimenId;
    }
    
    public void setRegimenId(String regimenId) {
        this.regimenId = regimenId;
    }
    
    public String getDrugCompositionId() {
        return this.drugCompositionId;
    }
    
    public void setDrugCompositionId(String drugCompositionId) {
        this.drugCompositionId = drugCompositionId;
    }
    
    public List<DrugDosage> getDrugDosages() {
        return this.drugDosages;
    }
    
    public void setDrugDosages(List<DrugDosage> drugDosages) {
        this.drugDosages = drugDosages;
    }

    public void addDrugDosage(DrugDosage drugDosage) {
        this.drugDosages.add(drugDosage);
    }

    public String getDrugCompositionGroupId() {
        return drugCompositionGroupId;
    }

    public void setDrugCompositionGroupId(String drugCompositionGroupId) {
        this.drugCompositionGroupId = drugCompositionGroupId;
    }

    public static TreatmentAdvice newDefault() {
        TreatmentAdvice advice = new TreatmentAdvice();
        DrugDosage firstDosage = new DrugDosage();
        DrugDosage secondDosage = new DrugDosage();
        advice.setDrugDosages(Arrays.asList(firstDosage, secondDosage));
        return  advice;
    }
}
