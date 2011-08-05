package org.motechproject.tama.web.model;

import java.util.ArrayList;
import java.util.List;

public class TreatmentAdviceView {

    private String patientId;

    private String regimenName;

    private String drugCompositionName;

    private List<DrugDosageView> drugDosages = new ArrayList<DrugDosageView>();

    public String getPatientId() {
        return patientId;
    }

    public void setPatientId(String patientId) {
        this.patientId = patientId;
    }

    public String getRegimenName() {
        return regimenName;
    }

    public void setRegimenName(String regimenName) {
        this.regimenName = regimenName;
    }

    public String getDrugCompositionName() {
        return drugCompositionName;
    }

    public void setDrugCompositionName(String drugCompositionName) {
        this.drugCompositionName = drugCompositionName;
    }

    public void addDrugDosage(DrugDosageView drugDosage) {
        drugDosages.add(drugDosage);
    }

    public List<DrugDosageView> getDrugDosages() {
        return drugDosages;
    }

    public void setDrugDosages(List<DrugDosageView> drugDosages) {
        this.drugDosages = drugDosages;
    }
}
