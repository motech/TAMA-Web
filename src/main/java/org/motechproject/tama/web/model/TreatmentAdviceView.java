package org.motechproject.tama.web.model;

import java.util.ArrayList;
import java.util.List;

public class TreatmentAdviceView {

    private String patientId;

    private String regimenName;

    private String regimenCompositionName;

    private List<DrugDosageView> drugDosageViews = new ArrayList<DrugDosageView>();

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

    public String getRegimenCompositionName() {
        return regimenCompositionName;
    }

    public void setRegimenCompositionName(String regimenCompositionName) {
        this.regimenCompositionName = regimenCompositionName;
    }

    public List<DrugDosageView> getDrugDosageViews() {
        return drugDosageViews;
    }

    public void setDrugDosageViews(List<DrugDosageView> drugDosageViews) {
        this.drugDosageViews = drugDosageViews;
    }
}
