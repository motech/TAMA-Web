// WARNING: DO NOT EDIT THIS FILE. THIS FILE IS MANAGED BY SPRING ROO.
// You may push code into the target .java compilation unit if you wish to edit any member(s).

package org.motechproject.tama.domain;

import java.util.Set;

privileged aspect TreatmentAdvice_Roo_JavaBean {
    
    public String TreatmentAdvice.getPatientId() {
        return this.patientId;
    }
    
    public void TreatmentAdvice.setPatientId(String patientId) {
        this.patientId = patientId;
    }
    
    public String TreatmentAdvice.getRegimenId() {
        return this.regimenId;
    }
    
    public void TreatmentAdvice.setRegimenId(String regimenId) {
        this.regimenId = regimenId;
    }
    
    public String TreatmentAdvice.getRegimenCompositionId() {
        return this.regimenCompositionId;
    }
    
    public void TreatmentAdvice.setRegimenCompositionId(String regimenCompositionId) {
        this.regimenCompositionId = regimenCompositionId;
    }
    
    public Set<DrugDosage> TreatmentAdvice.getDrugDosages() {
        return this.drugDosages;
    }
    
    public void TreatmentAdvice.setDrugDosages(Set<DrugDosage> drugDosages) {
        this.drugDosages = drugDosages;
    }
}
