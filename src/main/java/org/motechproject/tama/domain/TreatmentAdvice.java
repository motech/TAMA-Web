package org.motechproject.tama.domain;

import org.ektorp.support.TypeDiscriminator;
import org.motechproject.util.DateUtil;

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

    @NotNull
    private String reasonForDiscontinuing;

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

    public String getReasonForDiscontinuing() {
        return reasonForDiscontinuing;
    }

    public void setReasonForDiscontinuing(String reasonForDiscontinuing) {
        this.reasonForDiscontinuing = reasonForDiscontinuing;
    }

    public static TreatmentAdvice newDefault() {
        TreatmentAdvice advice = new TreatmentAdvice();
        advice.setDrugDosages(Arrays.asList(DrugDosage.dosageStartingToday(), DrugDosage.dosageStartingToday()));
        return advice;
    }

    public void endTheRegimen() {
        for (DrugDosage dosage : getDrugDosages()) {
            dosage.setEndDate(DateUtil.today());
        }
    }
}
